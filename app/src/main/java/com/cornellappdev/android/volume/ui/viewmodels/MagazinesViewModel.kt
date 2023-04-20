package com.cornellappdev.android.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.repositories.MagazineRepository
import com.cornellappdev.android.volume.ui.states.MagazineRetrievalState
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MagazinesViewModel"
@HiltViewModel
class MagazinesViewModel @Inject constructor(
    private val magazineRepository: MagazineRepository
) : ViewModel() {

    companion object {
        const val NUMBER_OF_FEATURED_MAGAZINES = 7.0
        const val NUMBER_OF_SEMESTER_MAGAZINES = 20.0
    }

    data class MagazinesUiState(
        val featuredMagazinesState: MagazinesRetrievalState = MagazinesRetrievalState.Loading,
        val moreMagazinesState: MagazinesRetrievalState = MagazinesRetrievalState.Loading,
        val magazineByIdState: MagazineRetrievalState = MagazineRetrievalState.Loading
    )

    var magazineUiState by mutableStateOf(MagazinesUiState())
        private set

    init {
        queryFeaturedMagazines()
    }

    /**
     * Queries the backend for the featured magazines. If successful, it will update
     * the ui state with the magazines retrieval state.
     * Otherwise it will update it with a failure state.
     * @param limit The limit of how many magazines to query for.
     */
    fun queryFeaturedMagazines(limit: Double? = NUMBER_OF_FEATURED_MAGAZINES) {
        viewModelScope.launch {
            try {
                magazineUiState = magazineUiState.copy(
                    featuredMagazinesState = MagazinesRetrievalState.Success(
                        magazineRepository.fetchFeaturedMagazines(
                            limit = limit
                        )
                    )
                )
                queryMoreMagazines(query = "View all")
            } catch (e: Exception) {
                magazineUiState = magazineUiState.copy(
                    featuredMagazinesState = MagazinesRetrievalState.Error
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
     * @param limit The limit of how many magazines to query for.
     */
    fun queryMoreMagazines (query: String, limit: Double? = NUMBER_OF_SEMESTER_MAGAZINES) {
        magazineUiState = magazineUiState.copy(
            moreMagazinesState = MagazinesRetrievalState.Loading
        )
        viewModelScope.launch {
            try {
                if (query == "View all") {
                    magazineUiState = magazineUiState.copy(
                        moreMagazinesState = MagazinesRetrievalState.Success(
                            magazineRepository.fetchAllMagazines(
                                limit = limit
                            )
                        )
                    )
                } else
                    magazineUiState = magazineUiState.copy(
                        moreMagazinesState = MagazinesRetrievalState.Success(
                            magazineRepository.fetchMagazinesBySemester(
                                limit = limit,
                                semester = query
                            )
                        )
                    )
            } catch (e: Exception) {
                magazineUiState = magazineUiState.copy(
                    featuredMagazinesState = MagazinesRetrievalState.Error
                )
            }
        }
    }

    /**
     * Queries the backend for a specific magazine based on a given magazine id. If successful, it
     * will update the UI state with the magazine retrieval state.
     * Otherwise it will update it to a failure state.
     * @param id The id of the magazine to query for.
     */
    fun queryMagazineById(id: String) {
        viewModelScope.launch {
            try {
                magazineUiState = magazineUiState.copy(
                    magazineByIdState = MagazineRetrievalState.Success(
                        magazineRepository.fetchMagazineById(id)
                    )
                )
            } catch (e: Exception) {
                magazineUiState = magazineUiState.copy(
                    magazineByIdState = MagazineRetrievalState.Error
                )
            }
        }
    }
}