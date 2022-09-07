package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// TODO test updating from individual publication view model onClick to publication tab
class PublicationTabViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) :
    ViewModel() {

    // A factory is necessary to create a ViewModel with arguments
    class Factory(private val userPreferencesRepository: UserPreferencesRepository) :
        ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PublicationTabViewModel(userPreferencesRepository) as T
    }


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
        // queryFollowedPublications()
        // queryMorePublications()
        queryAllPublications()
    }

    fun followPublication(id: String) = viewModelScope.launch {
        val uuid = userPreferencesRepository.fetchUuid()
        UserRepository.followPublication(id, uuid)

        // move from more to followed
    }

    private fun queryAllPublications() = viewModelScope.launch {
        try {
            _allPublicationsState.value = _allPublicationsState.value.copy(
                publicationsRetrievalState = PublicationsRetrievalState.Success(
                    PublicationRepository.fetchAllPublications()
                )
            )
        } catch (e: Exception) {
            _allPublicationsState.value = _allPublicationsState.value.copy(
                publicationsRetrievalState = PublicationsRetrievalState.Error
            )
        }
    }
}
