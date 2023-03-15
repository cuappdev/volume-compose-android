package com.cornellappdev.android.volume.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.repositories.MagazineRepository
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

// TODO optimize loading?
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
        val semesterMagazinesState: MagazinesRetrievalState = MagazinesRetrievalState.Loading,
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
                querySemesterMagazines(semester = getCurrentSemester())
            } catch (e: Exception) {
                magazineUiState = magazineUiState.copy(
                    featuredMagazinesState = MagazinesRetrievalState.Error
                )
                Log.d(TAG, "queryFeaturedMagazines: LOAD FAILED: ${e.message}")
            }
        }
    }

    /**
     * Queries the backend for the magazines of the current semester. If successful, it will update
     * the ui state with the magazines retrieval state.
     * Otherwise it will update it with a failure state.
     * @param semester Current semester, format of "fa" or "sp", and then last 2 digits of year
     * @param limit The limit of how many magazines to query for.
     */
    fun querySemesterMagazines (semester: String, limit: Double? = NUMBER_OF_SEMESTER_MAGAZINES) {
        magazineUiState = magazineUiState.copy(
            semesterMagazinesState = MagazinesRetrievalState.Loading
        )
        viewModelScope.launch {
            try {
                magazineUiState = magazineUiState.copy(
                    semesterMagazinesState = MagazinesRetrievalState.Success(
                        magazineRepository.fetchMagazinesBySemester(
                            limit = limit,
                            semester = semester
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
}

/**
 * Gets the current semester that the user is in based on the year and month.
 * @return string in the form of "faXX" or "spXX", depending on semester and year
 */
fun getCurrentSemester(): String {
    val calendar = Calendar.getInstance()
    val currentYear: Int = calendar.get(Calendar.YEAR) - 2000
    val season: String = if (calendar.get(Calendar.MONTH) <= 9) "sp" else "fa"
    return "$season$currentYear"
}


