package com.cornellappdev.android.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.repositories.ArticleRepository
import com.cornellappdev.android.volume.data.repositories.FlyerRepository
import com.cornellappdev.android.volume.data.repositories.MagazineRepository
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.states.FlyersRetrievalState
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val articleRepository: ArticleRepository,
    private val magazineRepository: MagazineRepository,
    private val flyersRepository: FlyerRepository,
) : ViewModel() {

    data class BookmarkUiState(
        val articlesState: ArticlesRetrievalState = ArticlesRetrievalState.Loading,
        val magazinesState: MagazinesRetrievalState = MagazinesRetrievalState.Loading,
        val upcomingFlyersState: FlyersRetrievalState = FlyersRetrievalState.Loading,
        val pastFlyersRetrievalState: FlyersRetrievalState = FlyersRetrievalState.Loading,
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
        try {
            val bookmarkedMagazineIds = userPreferencesRepository.fetchBookmarkedMagazineIds()
            bookmarkUiState = bookmarkUiState.copy(
                magazinesState = MagazinesRetrievalState.Success(
                    magazines = magazineRepository.fetchMagazinesByIds(bookmarkedMagazineIds)
                )
            )
        } catch (e: Exception) {
            bookmarkUiState = bookmarkUiState.copy(
                articlesState = ArticlesRetrievalState.Error
            )
        }
        getBookmarkedFlyers()
    }

    private fun getBookmarkedFlyers() = viewModelScope.launch {
        try {
            val bookmarkedFlyerIds = userPreferencesRepository.fetchBookmarkedFlyerIds()
            bookmarkUiState = bookmarkUiState.copy(
                // TODO filter by date for both upcoming and past
                pastFlyersRetrievalState = FlyersRetrievalState.Success(
                    flyers = flyersRepository.fetchFlyersByIds(bookmarkedFlyerIds)
                )
            )
        } catch (e: java.lang.Exception) {
            bookmarkUiState = bookmarkUiState.copy(
                upcomingFlyersState = FlyersRetrievalState.Error,
                pastFlyersRetrievalState = FlyersRetrievalState.Error
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
