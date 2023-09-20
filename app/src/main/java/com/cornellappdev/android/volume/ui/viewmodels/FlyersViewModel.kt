package com.cornellappdev.android.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.models.Flyer
import com.cornellappdev.android.volume.data.repositories.FlyerRepository
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.volume.ui.states.FlyersRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject


private const val TAG = "FlyersViewModel"

@HiltViewModel
class FlyersViewModel @Inject constructor(
    private val flyerRepository: FlyerRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    data class FlyersUiState(
        val weeklyFlyersState: FlyersRetrievalState = FlyersRetrievalState.Loading,
        val pastFlyersState: FlyersRetrievalState = FlyersRetrievalState.Loading,
        val upcomingFlyersState: FlyersRetrievalState = FlyersRetrievalState.Loading,
        val todayFlyersState: FlyersRetrievalState = FlyersRetrievalState.Loading,
    )

    lateinit var dailyFlyers: List<Flyer>
    lateinit var weeklyFlyers: List<Flyer>

    var flyersUiState by mutableStateOf(FlyersUiState())
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
                dailyFlyers = flyerRepository.fetchFlyersAfterDate(getToday()).filter { flyer ->
                    val flyerDate =
                        LocalDateTime.parse(flyer.endDate, DateTimeFormatter.ISO_DATE_TIME)
                    val todayDate = LocalDateTime.now().withHour(23).withMinute(59)

                    // Filter ensures flyers are before 11:59PM today.
                    flyerDate < todayDate
                }.sorted()
                flyersUiState = flyersUiState.copy(
                    todayFlyersState = FlyersRetrievalState.Success(
                        dailyFlyers
                    )
                )
                queryWeeklyFlyers()
            } catch (e: Exception) {
                dailyFlyers = listOf()
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
                weeklyFlyers =
                        // Apply a filter to find flyers with dates before upcoming Sunday at 11:59PM
                    flyerRepository.fetchFlyersAfterDate(getToday()).filter {
                        val upcomingSunday =
                            LocalDateTime.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
                                .withHour(23).withMinute(59)

                        // Ensure Flyer's date is before upcoming Sunday
                        LocalDateTime.parse(
                            it.endDate,
                            DateTimeFormatter.ISO_DATE_TIME
                        ) < upcomingSunday
                    }.filter { !dailyFlyers.contains(it) }.sorted()
                flyersUiState = flyersUiState.copy(
                    weeklyFlyersState = FlyersRetrievalState.Success(
                        weeklyFlyers
                    )
                )
                queryUpcomingFlyers(categorySlug = "all")
            } catch (e: Exception) {
                weeklyFlyers = listOf()
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
                flyersUiState =
                    flyersUiState.copy(upcomingFlyersState = FlyersRetrievalState.Loading)
                if (categorySlug.lowercase() == "all") {
                    val initialFlyers = flyerRepository.fetchFlyersAfterDate(getToday())
                    flyersUiState = flyersUiState.copy(
                        upcomingFlyersState = FlyersRetrievalState.Success(
                            initialFlyers
                                .filter { !weeklyFlyers.contains(it) && !dailyFlyers.contains(it) }
                                .sorted()
                        )
                    )
                } else {
                    // Get Flyers based on category
                    flyersUiState = flyersUiState.copy(
                        upcomingFlyersState = FlyersRetrievalState.Success(
                            flyerRepository.fetchFlyersByCategorySlug(categorySlug)
                                .filter {
                                    !dailyFlyers.contains(it) &&
                                            it.startDateTime > LocalDateTime.now()
                                }
                                .sorted()
                        )
                    )
                }
                if (flyersUiState.pastFlyersState == FlyersRetrievalState.Loading) {
                    queryPastFlyers()
                }
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
                        flyerRepository.fetchFlyersBeforeDate(
                            LocalDateTime.now().withHour(23).withMinute(59)
                                .format(DateTimeFormatter.ISO_DATE_TIME)
                        )
                            .sortedDescending()
                    )
                )
            } catch (e: Exception) {
                flyersUiState.copy(
                    pastFlyersState = FlyersRetrievalState.Error
                )
            }
        }
    }

    fun incrementTimesClicked(id: String) {
        viewModelScope.launch {
            flyerRepository.incrementTimesClicked(id)
        }
    }

    suspend fun getIsBookmarked(flyerId: String): Boolean {
        return userPreferencesRepository.fetchBookmarkedFlyerIds().contains(flyerId)
    }

    fun addBookmarkedFlyer(flyerId: String) {
        viewModelScope.launch {
            userPreferencesRepository.addBookmarkedFlyer(flyerId)
        }
    }

    fun removeBookmarkedFlyer(flyerId: String) {
        viewModelScope.launch {
            userPreferencesRepository.removeBookmarkedFlyer(flyerId)
        }
    }


}

/**
 * Returns a string representation of today's date, formatted with the ISO local date time formatter.
 */
private fun getToday(): String {
    return LocalDateTime.now()
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}