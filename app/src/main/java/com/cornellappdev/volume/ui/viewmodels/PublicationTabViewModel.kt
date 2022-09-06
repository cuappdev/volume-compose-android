package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Should also use the UserRepository to retrieve the list of publications followed
// by the User using the UUID obtained from userPreferencesRepository
class PublicationTabViewModel(
    private val publicationRepository: PublicationRepository = PublicationRepository,
    private val userRepository: UserRepository = UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) :
    ViewModel() {

    data class AllPublicationUIState(
        val publicationsRetrievalState: PublicationsRetrievalState
    )

    sealed interface PublicationsRetrievalState {
        data class Success(val publications: List<Publication>) : PublicationsRetrievalState
        object Error : PublicationsRetrievalState
        object Loading : PublicationsRetrievalState
    }

    private val _allPublicationsState =
        MutableStateFlow(AllPublicationUIState(PublicationsRetrievalState.Loading))

    val allPublicationsState: StateFlow<AllPublicationUIState> = _allPublicationsState.asStateFlow()

    init {
        queryAllPublications()
    }

    fun followPublication(id: String) = viewModelScope.launch {
        val uuid = userPreferencesRepository.fetchUuid()
        userRepository.followPublication(id, uuid)
    }

    fun unfollowPublication(id: String) = viewModelScope.launch {
        val uuid = userPreferencesRepository.fetchUuid()
        userRepository.unfollowPublication(id, uuid)
    }

    private fun queryAllPublications() = viewModelScope.launch {
        try {
            _allPublicationsState.value = _allPublicationsState.value.copy(
                publicationsRetrievalState = PublicationsRetrievalState.Success(
                    publicationRepository.fetchAllPublications()
                )
            )
        } catch (e: Exception) {
            _allPublicationsState.value = _allPublicationsState.value.copy(
                publicationsRetrievalState = PublicationsRetrievalState.Error
            )
        }
    }
}
