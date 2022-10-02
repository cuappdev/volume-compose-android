package com.cornellappdev.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
import com.cornellappdev.volume.ui.states.PublicationsRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO test updating from individual publication view model onClick to publication tab. Follows
// similarly with how bookmarking works from the BookmarkScreen -> ArticleWebViewScreen
@HiltViewModel
class PublicationsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository
) :
    ViewModel() {

    data class PublicationsUiState(
        val publicationsState: PublicationsRetrievalState = PublicationsRetrievalState.Loading
    )

    var publicationsUiState by mutableStateOf(PublicationsUiState())
        private set

    init {
        queryAllPublications()
    }

    fun followPublication(publicationSlug: String) = viewModelScope.launch {
        val uuid = userPreferencesRepository.fetchUuid()
        userRepository.followPublication(publicationSlug, uuid)

        // move from more to followed
    }

    private fun queryAllPublications() = viewModelScope.launch {
        try {
            publicationsUiState = publicationsUiState.copy(
                publicationsState = PublicationsRetrievalState.Success(
                    publicationRepository.fetchAllPublications()
                )
            )
        } catch (e: Exception) {
            publicationsUiState = publicationsUiState.copy(
                publicationsState = PublicationsRetrievalState.Error
            )
        }
    }
}
