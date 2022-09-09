package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.analytics.EventType
import com.cornellappdev.volume.analytics.VolumeEvent
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.repositories.ArticleRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository.Companion.MAX_SHOUTOUT
import com.cornellappdev.volume.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleWebViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    // Navigation arguments can be retrieved through the SavedStateHandle
    private val articleId: String = checkNotNull(savedStateHandle["articleId"])

    data class WebState(
        val articleState: ArticleState,
        val isBookmarked: Boolean,
        val isMaxedShoutout: Boolean
    )

    sealed interface ArticleState {
        data class Success(val article: Article) : ArticleState
        object Error : ArticleState
        object Loading : ArticleState
    }

    private val _webState = MutableStateFlow(
        WebState(
            articleState = ArticleState.Loading,
            isBookmarked = false,
            isMaxedShoutout = false
        )
    )

    val webState: StateFlow<WebState> =
        _webState.asStateFlow()

    init {
        getArticle()
    }

    private fun getArticle() = viewModelScope.launch {
        try {
            val article = articleRepository.fetchArticleByID(articleId)
            val isBookmarked =
                userPreferencesRepository.fetchBookmarkedArticleIds().contains(articleId)
            val isMaxedShoutout = userPreferencesRepository.fetchShoutoutCount(articleId) == 5
            _webState.value = _webState.value.copy(
                articleState = ArticleState.Success(article),
                isBookmarked = isBookmarked,
                isMaxedShoutout = isMaxedShoutout
            )
        } catch (e: Exception) {
            _webState.value = _webState.value.copy(
                articleState = ArticleState.Error
            )
        }
    }

    fun shoutoutArticle() = viewModelScope.launch {
        val shoutoutCount = userPreferencesRepository.fetchShoutoutCount(articleId)
        if (shoutoutCount < MAX_SHOUTOUT) {
            VolumeEvent.logEvent(EventType.ARTICLE, VolumeEvent.SHOUTOUT_ARTICLE, id = articleId)
            userPreferencesRepository.increaseShoutoutCount(articleId)
            articleRepository.incrementShoutout(articleId, userPreferencesRepository.fetchUuid())
        }
        if (shoutoutCount == MAX_SHOUTOUT - 1) {
            _webState.value = _webState.value.copy(
                isMaxedShoutout = true
            )
        }
    }

    /**
     * Bookmarks an article if it isn't already bookmarked. If it is, unboomarks for the user.
     *
     * @return
     */
    fun bookmarkArticle() = viewModelScope.launch {
        if (_webState.value.isBookmarked) {
            userPreferencesRepository.removeBookmarkedArticle(articleId)
            VolumeEvent.logEvent(
                EventType.ARTICLE,
                VolumeEvent.UNBOOKMARK_ARTICLE,
                id = articleId
            )
        } else {
            userPreferencesRepository.addBookmarkedArticle(articleId)
            userRepository.bookmarkArticle(userPreferencesRepository.fetchUuid())
            VolumeEvent.logEvent(
                EventType.ARTICLE,
                VolumeEvent.BOOKMARK_ARTICLE,
                id = articleId
            )
        }

        _webState.value = _webState.value.copy(
            isBookmarked = !_webState.value.isBookmarked
        )
    }
}
