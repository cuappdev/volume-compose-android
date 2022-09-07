package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.repositories.ArticleRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository.Companion.MAX_SHOUTOUT
import com.cornellappdev.volume.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArticleWebViewModel(
    private val articleId: String,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    // A factory is necessary to create a ViewModel with arguments
    class Factory(
        private val articleId: String,
        private val userPreferencesRepository: UserPreferencesRepository
    ) :
        ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ArticleWebViewModel(articleId, userPreferencesRepository) as T
    }

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
            val article = ArticleRepository.fetchArticleByID(articleId)
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
            userPreferencesRepository.increaseShoutoutCount(articleId)
            ArticleRepository.incrementShoutout(articleId, userPreferencesRepository.fetchUuid())
        }
        if (shoutoutCount == MAX_SHOUTOUT - 1) {
            _webState.value = _webState.value.copy(
                isMaxedShoutout = true
            )
        }
    }

    fun bookmarkArticle() = viewModelScope.launch {
        if (_webState.value.isBookmarked) {
            userPreferencesRepository.removeBookmarkedArticle(articleId)
        } else {
            userPreferencesRepository.addBookmarkedArticle(articleId)
            UserRepository.bookmarkArticle(userPreferencesRepository.fetchUuid())
        }

        _webState.value = _webState.value.copy(
            isBookmarked = !_webState.value.isBookmarked
        )
    }
}
