package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.repositories.ArticleRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.volume.util.BookmarkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val articleRepository: ArticleRepository
) : ViewModel() {

    val bookmarkStatus = savedStateHandle.getLiveData<BookmarkStatus?>("bookmarkStatus")

    data class BookmarkState(
        val articles: ArticlesRetrievalState = ArticlesRetrievalState.Loading
    )

    private val _bookmarkState = MutableStateFlow(BookmarkState())

    val bookmarkState: StateFlow<BookmarkState> =
        _bookmarkState.asStateFlow()

    init {
        getBookmarkedArticles()
    }

    fun getBookmarkedArticles() = viewModelScope.launch {
        try {
            val bookmarkedArticleIds = userPreferencesRepository.fetchBookmarkedArticleIds()
            _bookmarkState.value = _bookmarkState.value.copy(
                articles = ArticlesRetrievalState.Success(
                    articleRepository.fetchArticlesByIDs(bookmarkedArticleIds)
                )
            )
        } catch (e: Exception) {
            _bookmarkState.value = _bookmarkState.value.copy(
                articles = ArticlesRetrievalState.Error
            )
        }
    }

    fun removeArticle(id: String) = viewModelScope.launch {
        if (_bookmarkState.value.articles is ArticlesRetrievalState.Success) {
            val currentList =
                (_bookmarkState.value.articles as ArticlesRetrievalState.Success).articles.toMutableList()
            currentList.removeIf { article ->
                article.id == id
            }
            userPreferencesRepository.removeBookmarkedArticle(id)

            _bookmarkState.value = _bookmarkState.value.copy(
                articles = ArticlesRetrievalState.Success(currentList)
            )
        }
    }
}
