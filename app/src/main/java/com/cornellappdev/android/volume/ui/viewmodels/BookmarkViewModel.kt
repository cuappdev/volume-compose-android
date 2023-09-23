package com.cornellappdev.android.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.models.Flyer
import com.cornellappdev.android.volume.data.repositories.ArticleRepository
import com.cornellappdev.android.volume.data.repositories.FlyerRepository
import com.cornellappdev.android.volume.data.repositories.MagazineRepository
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.states.FlyersRetrievalState
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
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
        val pastFlyersState: FlyersRetrievalState = FlyersRetrievalState.Loading,
        val allUpcomingFlyers: List<Flyer> = emptyList(),
        val allPastFlyers: List<Flyer> = emptyList(),
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
        getBookmarkedFlyers()
    }

    private fun getBookmarkedFlyers() = viewModelScope.launch {
        try {
            val bookmarkedFlyerIds = userPreferencesRepository.fetchBookmarkedFlyerIds()
            val flyers = flyersRepository.fetchFlyersByIds(bookmarkedFlyerIds)

            bookmarkUiState = bookmarkUiState.copy(
                allPastFlyers = flyers.filter {
                    it.endDateTime < LocalDateTime.now()
                }.sortedDescending(),
                allUpcomingFlyers = flyers.filter {
                    it.endDateTime > LocalDateTime.now()
                }.sortedDescending()
            )
            applyQuery("all", isUpcoming = true)
            applyQuery("all", isUpcoming = false)
        } catch (e: java.lang.Exception) {
            bookmarkUiState = bookmarkUiState.copy(
                upcomingFlyersState = FlyersRetrievalState.Error,
                pastFlyersState = FlyersRetrievalState.Error
            )
        }
    }

    /**
     * Applies the query for upcoming or past flyers to fit what was selected in the dropdown.
     * @param categorySlug a valid category slug for filtering.
     * @param isUpcoming If this is true, the category slug will filter what is shown in upcoming flyers.
     * Otherwise, it will filter for past flyers.
     */
    fun applyQuery(categorySlug: String, isUpcoming: Boolean) {
        if (isUpcoming) {
            val allUpcomingFlyers = bookmarkUiState.allUpcomingFlyers
            bookmarkUiState = bookmarkUiState.copy(
                upcomingFlyersState =
                FlyersRetrievalState.Success(if (categorySlug.lowercase() == "all") allUpcomingFlyers
                else allUpcomingFlyers.filter {
                    it.categorySlug == categorySlug
                })
            )
        } else {
            val allPastFlyers = bookmarkUiState.allPastFlyers
            bookmarkUiState = bookmarkUiState.copy(
                pastFlyersState =
                FlyersRetrievalState.Success(if (categorySlug.lowercase() == "all") allPastFlyers
                else allPastFlyers.filter {
                    it.categorySlug == categorySlug
                })
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
