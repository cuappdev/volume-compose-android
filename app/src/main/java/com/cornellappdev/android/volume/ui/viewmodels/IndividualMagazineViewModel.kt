package com.cornellappdev.android.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.repositories.MagazineRepository
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository.Companion.MAX_SHOUTOUT
import com.cornellappdev.android.volume.data.repositories.UserRepository
import com.cornellappdev.android.volume.ui.states.MagazineRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MagazinesViewModel"

@HiltViewModel
class IndividualMagazineViewModel @Inject constructor(
    private val magazineRepository: MagazineRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    data class IndividualMagazineUiState(
        val magazineState: MagazineRetrievalState = MagazineRetrievalState.Loading,

        /**
         * Represents whether or not the user has bookmarked the current magazine.
         */
        val isBookmarked: Boolean = false,

        /**
         * Represents the current shoutout count of the magazine.
         */
        val shoutoutCount: Int = 0,

        val hasMaxShoutouts: Boolean = false,

        val magazineId: String = "",
    )

    var magazineUiState by mutableStateOf(IndividualMagazineUiState())
        private set


    /**
     * Queries the backend for a specific magazine based on a given magazine id. If successful, it
     * will update the UI state with the magazine retrieval state.
     * Otherwise it will update it to a failure state.
     * @param id The id of the magazine to query for.
     */
    fun queryMagazineById(id: String) {
        viewModelScope.launch {
            try {
                val magazine = magazineRepository.fetchMagazineById(id)
                magazineUiState = magazineUiState.copy(
                    magazineState = MagazineRetrievalState.Success(
                        magazine
                    ),
                    shoutoutCount = magazine.shoutouts.toInt(),
                    isBookmarked = userPreferencesRepository
                        .fetchBookmarkedMagazineIds().contains(magazine.id),
                    magazineId = magazine.id,
                    hasMaxShoutouts = userPreferencesRepository.fetchShoutoutCount(magazine.id) == MAX_SHOUTOUT
                )
            } catch (e: Exception) {
                magazineUiState = magazineUiState.copy(
                    magazineState = MagazineRetrievalState.Error
                )
            }
        }
    }

    /**
     * Bookmarks the current magazine using local storage.
     * If the magazine is not yet loaded, this function will do nothing.
     */
    fun bookmarkMagazine() = viewModelScope.launch {
        if (magazineUiState.magazineId != "") {
            val id = magazineUiState.magazineId
            if (userPreferencesRepository.fetchBookmarkedMagazineIds().contains(id)) {
                userPreferencesRepository.removeBookmarkedMagazine(id)
                magazineUiState = magazineUiState.copy(
                    isBookmarked = false
                )
                userRepository.bookmarkMagazine(
                    magazineUiState.magazineId,
                    userPreferencesRepository.fetchUuid()
                )
            } else {
                userPreferencesRepository.addBookmarkedMagazine(id)
                magazineUiState = magazineUiState.copy(
                    isBookmarked = true
                )
                userRepository.unbookmarkMagazine(
                    magazineUiState.magazineId,
                    userPreferencesRepository.fetchUuid()
                )
            }
        }
    }

    fun shoutoutMagazine() = viewModelScope.launch {
        if (magazineUiState.magazineId != "" && !magazineUiState.hasMaxShoutouts) {
            val id = magazineUiState.magazineId

            magazineRepository.incrementMagazineShoutouts(id, userPreferencesRepository.fetchUuid())
            userPreferencesRepository.increaseShoutoutCount(id)
            magazineUiState = magazineUiState.copy(
                shoutoutCount = magazineUiState.shoutoutCount + 1,
                hasMaxShoutouts = magazineUiState.shoutoutCount >= MAX_SHOUTOUT
            )
        }
    }
}
