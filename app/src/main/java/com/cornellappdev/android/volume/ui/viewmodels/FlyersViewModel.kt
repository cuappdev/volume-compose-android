package com.cornellappdev.android.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.models.Organization
import com.cornellappdev.android.volume.data.repositories.FlyerRepository
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.volume.ui.states.FlyersRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject


private const val TAG = "MagazinesViewModel"

@HiltViewModel
class FlyersViewModel @Inject constructor(
    private val flyerRepository: FlyerRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    data class FlyersUiState(
        val weeklyFlyersState: FlyersRetrievalState = FlyersRetrievalState.Loading,
        val pastFlyersState: FlyersRetrievalState = FlyersRetrievalState.Loading,
        val upcomingFlyersState: FlyersRetrievalState = FlyersRetrievalState.Loading,
        val todayFlyersState: FlyersRetrievalState = FlyersRetrievalState.Loading,
    )

    var flyersUiState by mutableStateOf(FlyersViewModel.FlyersUiState())
        private set

    init {
        queryTodayFlyers()
    }

    /**
     * Returns a sorted list of Flyer's achieved from using the Flyers After Date query to find Flyers
     * after and including today, but are filtered to ensure that they end before 11:59PM.
     */
    private fun queryTodayFlyers() {
        viewModelScope.launch {
            try {
                flyersUiState = flyersUiState.copy(
                    todayFlyersState = FlyersRetrievalState.Success(
                        flyerRepository.fetchFlyersAfterDate(getToday()).filter { flyer ->
                            val flyerDate =
                                LocalDateTime.parse(flyer.endDate, DateTimeFormatter.ISO_DATE_TIME)
                            val todayDate = LocalDateTime.now().withHour(23).withMinute(59)

                            // Filter ensures flyers are before 11:59PM today.
                            flyerDate < todayDate
                        }.sorted()
                    )
                )
                queryWeeklyFlyers()
            } catch (e: Exception) {
                flyersUiState = flyersUiState.copy(
                    todayFlyersState = FlyersRetrievalState.Error
                )
            }
        }
    }

    /**
     * Returns a sorted list of Flyers between now and Sunday 11:59PM
     */
    private fun queryWeeklyFlyers() {
        viewModelScope.launch {
            try {
                flyersUiState = flyersUiState.copy(
                    weeklyFlyersState = FlyersRetrievalState.Success(
                        // Apply a filter to find flyers with dates before upcoming Sunday at 11:59PM
                        flyerRepository.fetchFlyersAfterDate(getToday()).filter {
                            val upcomingSunday =
                                LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
                                    .withHour(23).withMinute(59)

                            // Ensure Flyer's date is before upcoming Sunday
                            LocalDateTime.parse(
                                it.endDate,
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME
                            ) < upcomingSunday
                        }.sorted()
                    )
                )
                queryPastFlyers()
            } catch (e: Exception) {
                flyersUiState = flyersUiState.copy(
                    weeklyFlyersState = FlyersRetrievalState.Error
                )
            }
        }
    }

    /**
     * Queries for all flyers with an end date greater than now, given a category slug.
     * If the category slug is "all", then there will be no filter for categories applied.
     * Otherwise, it will only get flyers that fall in that category, determined by the
     * organizations that fall under that category.
     * @param categorySlug: A valid category slugs.
     */
    fun queryUpcomingFlyers(categorySlug: String) {
        viewModelScope.launch {
            try {
                if (categorySlug.lowercase() == "all") {
                    flyersUiState = flyersUiState.copy(
                        upcomingFlyersState = FlyersRetrievalState.Success(
                            flyerRepository.fetchFlyersAfterDate(getToday()).sorted()
                        )
                    )
                } else {
                    // Perform procedure to get flyers based on category.

                    // Step 1: Find organizations related to category.
                    val organizations: List<Organization> =
                        flyerRepository.fetchOrganizationsByCategorySlug(categorySlug)
                    // Step 2: Find flyers by those organizations:
                    val organizationIds = organizations.map { it.id }
                    flyersUiState = flyersUiState.copy(
                        upcomingFlyersState = FlyersRetrievalState.Success(
                            flyerRepository.fetchFlyersByOrganizationIds(organizationIds)
                                .sorted()
                        )
                    )
                }

                queryPastFlyers()
            } catch (e: Exception) {
                flyersUiState = flyersUiState.copy(
                    upcomingFlyersState = FlyersRetrievalState.Error
                )
            }
        }
    }

    /**
     * Queries all flyers before now, and returns a sorted list of them in descending order.
     */
    private fun queryPastFlyers() {
        viewModelScope.launch {
            flyersUiState = try {
                flyersUiState.copy(
                    // Since we want most recent flyers to show first but we are looking at past
                    // flyers, we need to do a sorted descending here.
                    pastFlyersState = FlyersRetrievalState.Success(
                        flyerRepository.fetchFlyersBeforeDate(getToday()).sortedDescending()
                    )
                )
            } catch (e: Exception) {
                flyersUiState.copy(
                    pastFlyersState = FlyersRetrievalState.Error
                )
            }
        }
    }


    /*private fun queryTodayFlyers() {
        viewModelScope.launch {
            try {
                val flyers = flyerRepository.fetchTodayFlyers()
                flyersUiState = if (flyers == null) {
                    flyersUiState.copy(
                        todayFlyersState = FlyersRetrievalState.Error
                    )
                } else {
                    flyersUiState.copy(
                        todayFlyersState = FlyersRetrievalState.Success(
                            flyers
                        )
                    )
                }
                queryWeeklyFlyers()
            } catch (e: Exception) {
                flyersUiState = flyersUiState.copy(
                    todayFlyersState = FlyersRetrievalState.Error
                )
            }
        }
    }

    */
    /**
     * Queries the backend for weekly flyers magazines. If successful, it will update
     * the ui state with the flyers retrieval state.
     * Otherwise it will update it with a failure state.
     *//*
    private fun queryWeeklyFlyers() {
        viewModelScope.launch {
            try {
                flyersUiState = flyersUiState.copy(
                    weeklyFlyersState = FlyersRetrievalState.Success(
                        flyerRepository.fetchWeeklyFlyers() ?: listOf()
                    )
                )
                queryUpcomingFlyers("All")
            } catch (e: Exception) {
                flyersUiState = flyersUiState.copy(
                    weeklyFlyersState = FlyersRetrievalState.Error
                )
            }
        }
    }

    */
    /**
     * Queries the backend for the magazines of the current semester. If successful, it will update
     * the ui state with the magazines retrieval state.
     * If the query is "View all" the function will query for all magazines and update the
     * magazine state accordingly.
     * Otherwise it will update it with a failure state.
     * @param query Current semester, format of "fa" or "sp", and then last 2 digits of year
     *//*
    fun queryUpcomingFlyers (query: String) {
        flyersUiState = flyersUiState.copy(
            upcomingFlyersState = FlyersRetrievalState.Loading
        )
        viewModelScope.launch {
            try {
                flyersUiState = if (query.lowercase() == "all") {
                    flyersUiState.copy(
                        upcomingFlyersState = FlyersRetrievalState.Success(
                            flyerRepository.fetchWeeklyFlyers() ?: listOf()
                        )
                    )
                } else {
                    flyersUiState.copy(
                        upcomingFlyersState = FlyersRetrievalState.Success(
                            flyerRepository.fetchUpcomingFlyers()?.filter { f -> f.organizations.first().type.lowercase() == query.lowercase() } ?: listOf()
                        )
                    )
                }
                queryPastFlyers()
            } catch (ignored: Exception) {
                flyersUiState = flyersUiState.copy(
                    upcomingFlyersState = FlyersRetrievalState.Error
                )
            }
        }
    }
    private fun queryPastFlyers () {
        viewModelScope.launch {
            flyersUiState = try {
                flyersUiState.copy(
                    pastFlyersState = FlyersRetrievalState.Success(flyerRepository.fetchPastFlyers(limit = NUMBER_OF_PAST_FLYERS) ?: listOf())
                )
            } catch (ignored: Exception) {
                flyersUiState.copy(
                    upcomingFlyersState = FlyersRetrievalState.Error
                )
            }
        }
    }*/

    suspend fun getIsBookmarked(flyerId: String): Boolean {
        return userPreferencesRepository.fetchBookmarkedFlyerIds().contains(flyerId)
    }
}

/**
 * Returns a string representation of today's date, formatted with the ISO local date time formatter.
 */
private fun getToday(): String {
    return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}