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

    init {
        viewModelScope.launch {
            userRepository.readArticle(articleId, userPreferencesRepository.fetchUuid())
        }
    }

    data class WebViewUiState(
        val articleState: ArticleRetrievalState = ArticleRetrievalState.Loading,

        /**
         * The initial state of the article being bookmarked while the article is open.
         * If false, the article hasn't been bookmarked prior.
         */
        val initialBookmarkState: Boolean = false,

        /**
         * The state of shoutout. If isMaxedShoutout is true, shoutouts are maxed for the
         * given article.
         */
        val isMaxedShoutout: Boolean = false,

        /**
         * Represents the current bookmark state of the article. If true, the article
         * is currently bookmarked.
         */
        val isBookmarked: Boolean = false,

        /**
         * Represents the current shoutout count of the article.
         */
        val shoutoutCount: Int = 0
    )

    var webViewUiState by mutableStateOf(WebViewUiState())
        private set

    init {
        getArticle()
    }

    private fun getArticle() = viewModelScope.launch {
        try {
            val article = articleRepository.fetchArticleByID(articleId)
            userPreferencesRepository.fetchBookmarkedArticleIds().contains(articleId).let {
                webViewUiState = webViewUiState.copy(
                    initialBookmarkState = it,
                    isBookmarked = it
                )
            }
            userPreferencesRepository.fetchShoutoutCount(articleId).let { count ->
                webViewUiState = webViewUiState.copy(
                    shoutoutCount = count,
                    isMaxedShoutout = count == MAX_SHOUTOUT
                )
            }
            webViewUiState = webViewUiState.copy(
                articleState = ArticleRetrievalState.Success(article)
            )
        } catch (e: Exception) {
            webViewUiState = webViewUiState.copy(
                articleState = ArticleRetrievalState.Error
            )
        }
    }

    fun shoutoutArticle() = viewModelScope.launch {
        if (webViewUiState.shoutoutCount < MAX_SHOUTOUT) {
            VolumeEvent.logEvent(EventType.ARTICLE, VolumeEvent.SHOUTOUT_ARTICLE, id = articleId)
            userPreferencesRepository.increaseShoutoutCount(articleId)
            articleRepository.incrementShoutout(articleId, userPreferencesRepository.fetchUuid())
            webViewUiState = webViewUiState.copy(
                shoutoutCount = webViewUiState.shoutoutCount + 1
            )
        }
        webViewUiState = webViewUiState.copy(
            isMaxedShoutout = webViewUiState.shoutoutCount == MAX_SHOUTOUT
        )
    }

    /**
     * Bookmarks an article if it isn't already bookmarked. If it is, unboomarks for the user.
     */
    fun bookmarkArticle() = viewModelScope.launch {
        if (webViewUiState.isBookmarked) {
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

        webViewUiState = webViewUiState.copy(
            isBookmarked = !webViewUiState.isBookmarked
        )
    }
}


