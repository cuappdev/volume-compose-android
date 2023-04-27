package com.cornellappdev.android.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.repositories.ArticleRepository
import com.cornellappdev.android.volume.data.repositories.MagazineRepository
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val articleRepository: ArticleRepository,
    private val magazineRepository: MagazineRepository
) : ViewModel() {

    data class BookmarkUiState(
        val articlesState: ArticlesRetrievalState = ArticlesRetrievalState.Loading,
        val magazinesState: MagazinesRetrievalState = MagazinesRetrievalState.Loading
    )

    var bookmarkUiState by mutableStateOf(BookmarkUiState())
        private set

    init {
        getBookmarkedArticles()
    }

    private fun getBookmarkedArticles() = viewModelScope.launch {
        bookmarkUiState = try {
            val bookmarkedArticleIds = userPreferencesRepository.fetchBookmarkedArticleIds()
            bookmarkUiState.copy(
                articlesState = ArticlesRetrievalState.Success(
                    articleRepository.fetchArticlesByIDs(bookmarkedArticleIds)
                )
            )
        } catch (e: Exception) {
            bookmarkUiState.copy(
                articlesState = ArticlesRetrievalState.Error
            )
        }
        getBookmarkedMagazines()
    }

    private fun getBookmarkedMagazines() = viewModelScope.launch {
        bookmarkUiState = try {
            val bookmarkedMagazineIds = userPreferencesRepository.fetchBookmarkedMagazineIds()
            bookmarkUiState.copy(
                magazinesState = MagazinesRetrievalState.Success(
                    magazines = magazineRepository.fetchMagazinesByIds(bookmarkedMagazineIds)
                )
            )
        } catch (e: Exception) {
            bookmarkUiState.copy(
                articlesState = ArticlesRetrievalState.Error
            )
        }
    }

    private fun getBookmarkedFlyers() = viewModelScope.launch {
        // TODO
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
