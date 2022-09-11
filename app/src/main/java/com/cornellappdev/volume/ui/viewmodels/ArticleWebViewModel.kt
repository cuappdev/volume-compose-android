package com.cornellappdev.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.analytics.EventType
import com.cornellappdev.volume.analytics.VolumeEvent
import com.cornellappdev.volume.data.repositories.ArticleRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository.Companion.MAX_SHOUTOUT
import com.cornellappdev.volume.data.repositories.UserRepository
import com.cornellappdev.volume.ui.states.ArticleRetrievalState
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
        val article: ArticleRetrievalState = ArticleRetrievalState.Loading,
    )

    private val _webState = MutableStateFlow(WebState())
    val webState: StateFlow<WebState> =
        _webState.asStateFlow()

    /**
     * The initial state of the article being bookmarked while the article is open.
     * If false, the article hasn't been bookmarked prior.
     */
    var initialBookmarkState by mutableStateOf(false)
        private set

    /**
     * The state of shoutout. If isMaxedShoutout is true, shoutouts are maxed for the
     * given article.
     */
    var isMaxedShoutout by mutableStateOf(false)
        private set

    /**
     * Represents the current bookmark state of the article. If true, the article
     * is currently bookmarked.
     */
    var isBookmarked by mutableStateOf(false)
        private set

    /**
     * Represents the current shoutout count of the article.
     */
    var shoutoutCount by mutableStateOf(0)
        private set

    init {
        getArticle()
    }

    private fun getArticle() = viewModelScope.launch {
        try {
            val article = articleRepository.fetchArticleByID(articleId)
            userPreferencesRepository.fetchBookmarkedArticleIds().contains(articleId).let {
                initialBookmarkState = it
                isBookmarked = it
            }
            userPreferencesRepository.fetchShoutoutCount(articleId).let { count ->
                shoutoutCount = count
                isMaxedShoutout = count == MAX_SHOUTOUT
            }
            _webState.value = _webState.value.copy(
                article = ArticleRetrievalState.Success(article)
            )
        } catch (e: Exception) {
            _webState.value = _webState.value.copy(
                article = ArticleRetrievalState.Error
            )
        }
    }

    fun shoutoutArticle() = viewModelScope.launch {
        if (shoutoutCount < MAX_SHOUTOUT) {
            VolumeEvent.logEvent(EventType.ARTICLE, VolumeEvent.SHOUTOUT_ARTICLE, id = articleId)
            userPreferencesRepository.increaseShoutoutCount(articleId)
            articleRepository.incrementShoutout(articleId, userPreferencesRepository.fetchUuid())
            shoutoutCount++
        }

        isMaxedShoutout = shoutoutCount == MAX_SHOUTOUT
    }

    /**
     * Bookmarks an article if it isn't already bookmarked. If it is, unboomarks for the user.
     */
    fun bookmarkArticle() = viewModelScope.launch {
        if (isBookmarked) {
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

        isBookmarked = !isBookmarked
    }
}
