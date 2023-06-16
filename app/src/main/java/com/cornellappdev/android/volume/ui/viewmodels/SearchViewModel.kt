package com.cornellappdev.android.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.repositories.ArticleRepository
import com.cornellappdev.android.volume.data.repositories.MagazineRepository
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val magazineRepository: MagazineRepository,
    private val articleRepository: ArticleRepository,
) : ViewModel() {

    data class SearchUiState(
        val searchedMagazinesState: MagazinesRetrievalState = MagazinesRetrievalState.Loading,
        val searchedArticlesState: ArticlesRetrievalState = ArticlesRetrievalState.Loading,
    )

    var searchUiState by mutableStateOf(SearchUiState())
        private set

    /**
     * This function updates the view model with the result from performing a search
     * with the given query for magazines.
     *
     * @param query the search query from the user.
     */
    fun searchMagazines(query: String) {
        searchUiState = searchUiState.copy(
            searchedMagazinesState = MagazinesRetrievalState.Loading
        )
        viewModelScope.launch {
            searchUiState = try {
                searchUiState.copy(
                    searchedMagazinesState = MagazinesRetrievalState.Success(
                        magazines = magazineRepository.searchMagazines(query)
                    )
                )
            } catch (ignored: Exception) {
                searchUiState.copy(
                    searchedMagazinesState = MagazinesRetrievalState.Error
                )
            }
        }
    }


    /**
     * This function updates the view model with the result from performing a search
     * with the given query for articles.
     *
     * @param query the search query from the user.
     */
    fun searchArticles(query: String) {
        searchUiState = searchUiState.copy(
            searchedArticlesState = ArticlesRetrievalState.Loading
        )
        viewModelScope.launch {
            searchUiState = try {
                searchUiState.copy(
                    searchedArticlesState = ArticlesRetrievalState.Success(
                        articles = articleRepository.searchArticles(query)
                    )
                )
            } catch (ignored: java.lang.Exception) {
                searchUiState.copy(
                    searchedArticlesState = ArticlesRetrievalState.Error
                )
            }
        }
    }
}