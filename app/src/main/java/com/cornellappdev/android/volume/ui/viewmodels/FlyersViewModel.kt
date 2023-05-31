package com.cornellappdev.android.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.repositories.FlyerRepository
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.volume.ui.states.FlyersRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    companion object {
        const val NUMBER_OF_UPCOMING_FLYERS = 20.0
        const val NUMBER_OF_PAST_FLYERS = 25.0
    }

    var flyersUiState by mutableStateOf(FlyersViewModel.FlyersUiState())
        private set

    init {
        queryTodayFlyers()
    }

    private fun queryTodayFlyers() {
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

    /**
     * Queries the backend for weekly flyers magazines. If successful, it will update
     * the ui state with the flyers retrieval state.
     * Otherwise it will update it with a failure state.
     */
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

    /**
     * Queries the backend for the magazines of the current semester. If successful, it will update
     * the ui state with the magazines retrieval state.
     * If the query is "View all" the function will query for all magazines and update the
     * magazine state accordingly.
     * Otherwise it will update it with a failure state.
     * @param query Current semester, format of "fa" or "sp", and then last 2 digits of year
     */
    fun queryUpcomingFlyers (query: String) {
        flyersUiState = flyersUiState.copy(
            upcomingFlyersState = FlyersRetrievalState.Loading
        )
        viewModelScope.launch {
            try {
                if (query.lowercase() == "all") {
                    flyersUiState = flyersUiState.copy(
                        upcomingFlyersState = FlyersRetrievalState.Success(
                            flyerRepository.fetchWeeklyFlyers() ?: listOf()
                        )
                    )
                } else {
                    flyersUiState = flyersUiState.copy(
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
    }

    suspend fun getIsBookmarked (flyerId: String): Boolean {
        return userPreferencesRepository.fetchBookmarkedFlyerIds().contains(flyerId)
    }
}

/**
 * Returns a string representation of the date one week after today, with the pattern
 * yyyy-MM-dd
 */
private fun getWeekAfter(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.now().plusWeeks(1)
    return formatter.format(date)
}

/**
 * Returns a string representation of today's date, with the pattern
 * yyyy-MM-dd
 */
private fun getToday(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.now()
    return formatter.format(date)
}