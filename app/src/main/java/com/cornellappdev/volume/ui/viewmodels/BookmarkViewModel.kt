package com.cornellappdev.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.repositories.ArticleRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.ui.states.ArticlesRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val articleRepository: ArticleRepository
) : ViewModel() {

    data class BookmarkUiState(
        val articlesState: ArticlesRetrievalState = ArticlesRetrievalState.Loading
    )

    var bookmarkUiState by mutableStateOf(BookmarkUiState())
        private set

    init {
        getBookmarkedArticles()
    }

    fun getBookmarkedArticles() = viewModelScope.launch {
        try {
            val bookmarkedArticleIds = userPreferencesRepository.fetchBookmarkedArticleIds()
            bookmarkUiState = bookmarkUiState.copy(
                articlesState = ArticlesRetrievalState.Success(
                    articleRepository.fetchArticlesByIDs(bookmarkedArticleIds)
                )
            )
        } catch (e: Exception) {
            bookmarkUiState = bookmarkUiState.copy(
                articlesState = ArticlesRetrievalState.Error
            )
        }
    }

    fun removeArticle(id: String) = viewModelScope.launch {
        if (bookmarkUiState.articlesState is ArticlesRetrievalState.Success) {
            val currentList =
                (bookmarkUiState.articlesState as ArticlesRetrievalState.Success).articles.toMutableList()
            currentList.removeIf { article ->
                article.id == id
            }
            userPreferencesRepository.removeBookmarkedArticle(id)

            bookmarkUiState = bookmarkUiState.copy(
                articlesState = ArticlesRetrievalState.Success(currentList)
            )
        }
    }
}
