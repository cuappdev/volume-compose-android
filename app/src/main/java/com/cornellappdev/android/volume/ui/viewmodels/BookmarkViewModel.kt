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
import java.time.format.DateTimeFormatter
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

    private lateinit var allUpcomingFlyers: List<Flyer>
    private lateinit var allPastFlyers: List<Flyer>
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
            val flyers = flyersRepository.fetchFlyersByIds(bookmarkedFlyerIds)
            allUpcomingFlyers = flyers.filter {
                LocalDateTime.parse(
                    it.endDate,
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
                ) < LocalDateTime.now()
            }.sortedDescending()
            allPastFlyers = flyers.filter {
                LocalDateTime.parse(
                    it.endDate,
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
                ) > LocalDateTime.now()
            }.sorted()

            bookmarkUiState = bookmarkUiState.copy(
                // TODO filter by date for both upcoming and past
                pastFlyersRetrievalState = FlyersRetrievalState.Success(
                    flyers = allUpcomingFlyers
                ),
                upcomingFlyersState = FlyersRetrievalState.Success(
                    flyers = allPastFlyers
                )
            )
        } catch (e: java.lang.Exception) {
            bookmarkUiState = bookmarkUiState.copy(
                upcomingFlyersState = FlyersRetrievalState.Error,
                pastFlyersRetrievalState = FlyersRetrievalState.Error
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
            bookmarkUiState = bookmarkUiState.copy(
                upcomingFlyersState = when (bookmarkUiState.upcomingFlyersState) {
                    FlyersRetrievalState.Error -> FlyersRetrievalState.Error
                    FlyersRetrievalState.Loading -> FlyersRetrievalState.Loading
                    is FlyersRetrievalState.Success -> {
                        // The success state ensures that allUpcomingFlyers has been initialized,
                        // so it is safe to use here.
                        FlyersRetrievalState.Success(allUpcomingFlyers.filter {
                            it.belongsToCategory(
                                categorySlug
                            )
                        })
                    }
                }
            )
        } else {
            bookmarkUiState = bookmarkUiState.copy(
                upcomingFlyersState = when (bookmarkUiState.pastFlyersRetrievalState) {
                    FlyersRetrievalState.Error -> FlyersRetrievalState.Error
                    FlyersRetrievalState.Loading -> FlyersRetrievalState.Loading
                    is FlyersRetrievalState.Success -> {
                        // The success state ensures that allPastFlyers has been initialized,
                        // so it is safe to use here.
                        FlyersRetrievalState.Success(allPastFlyers.filter {
                            it.belongsToCategory(
                                categorySlug
                            )
                        })
                    }
                }
            )
        }
    }

    /**
     * Checks if a Flyer belongs in a certain category by checking if any of its organizations
     * fall in that category.
     * @param categorySlug a valid category slug
     */
    private fun Flyer.belongsToCategory(categorySlug: String): Boolean {
        this.organizations.forEach {
            if (it.categorySlug == categorySlug) {
                return true
            }
        }
        return false
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
