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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SearchViewModel"

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

    // Have two main jobs to represent the search queries, default initialized to an empty job.
    // This way we can cancel jobs from search requests we no longer need.
    private var magazineJob: Job = viewModelScope.launch { }
    private var articleJob: Job = viewModelScope.launch { }

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
        // Cancel any previous jobs so we don't unnecessarily load those search results.
        magazineJob.cancel()
        magazineJob = viewModelScope.launch {
            // Only query once the user has stopped typing for one second.
            delay(1000L)
            searchUiState = try {
                searchUiState.copy(
                    searchedMagazinesState = MagazinesRetrievalState.Success(
                        magazines = magazineRepository.searchMagazines(query)
                    )
                )
            } catch (e: java.lang.Exception) {
                if (e is CancellationException) {
                    searchUiState
                } else {
                    searchUiState.copy(
                        searchedMagazinesState = MagazinesRetrievalState.Error
                    )
                }
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
        // Cancel any previous jobs so we don't unnecessarily load those search results.
        articleJob.cancel()
        articleJob = viewModelScope.launch {
            // Only query once the user has stopped typing for one second.
            delay(1000L)
            searchUiState = try {
                searchUiState.copy(
                    searchedArticlesState = ArticlesRetrievalState.Success(
                        articles = articleRepository.searchArticles(query)
                    )
                )
            } catch (e: java.lang.Exception) {
                if (e is CancellationException) {
                    searchUiState
                } else {
                    searchUiState.copy(
                        searchedArticlesState = ArticlesRetrievalState.Error
                    )
                }
            }
        }
    }
}