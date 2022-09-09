package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.analytics.EventType
import com.cornellappdev.volume.analytics.VolumeEvent
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    data class CreatingUserState(
        val createUserState: CreateUserState
    )

    data class AllPublicationUIState(
        val publicationsRetrievalState: PublicationsRetrievalState
    )

    sealed interface PublicationsRetrievalState {
        data class Success(val publications: List<Publication>) : PublicationsRetrievalState
        object Error : PublicationsRetrievalState
        object Loading : PublicationsRetrievalState
    }

    sealed interface CreateUserState {
        object Success : CreateUserState
        object Pending : CreateUserState
        object Error : CreateUserState
    }

    // Backing property to avoid state updates from other classes
    private val _creatingUserState = MutableStateFlow(
        CreatingUserState(CreateUserState.Pending)
    )

    // The UI collects from this StateFlow to get its state updates
    val creatingUserState: StateFlow<CreatingUserState> =
        _creatingUserState.asStateFlow()

    private val _allPublicationsState =
        MutableStateFlow(AllPublicationUIState(PublicationsRetrievalState.Loading))

    val allPublicationsState: StateFlow<AllPublicationUIState> = _allPublicationsState.asStateFlow()

    private val _setOfPubsFollowed = mutableSetOf<String>()
    val setOfPubsFollowed: Set<String>
        get() = _setOfPubsFollowed

    init {
        queryAllPublications()
    }

    fun addPublicationToFollowed(pubId: String) = _setOfPubsFollowed.add(pubId)

    fun removePublicationFromFollowed(pubId: String) = _setOfPubsFollowed.remove(pubId)

    fun updateOnboardingCompleted() = viewModelScope.launch {
        userPreferencesRepository.updateOnboardingCompleted(completed = true)
        VolumeEvent.logEvent(EventType.GENERAL, VolumeEvent.COMPLETE_ONBOARDING)
    }

    fun createUser() = viewModelScope.launch {
        try {
            val listOfPubsFollowed = setOfPubsFollowed.toList()
            val user = userRepository.createUser(
                listOfPubsFollowed,
                userPreferencesRepository.fetchDeviceToken()
            )
            userPreferencesRepository.updateUuid(user.uuid)
            userRepository.followPublications(listOfPubsFollowed, user.uuid)
            _creatingUserState.value = _creatingUserState.value.copy(
                createUserState = CreateUserState.Success
            )
        } catch (e: Exception) {
            _creatingUserState.value = _creatingUserState.value.copy(
                createUserState = CreateUserState.Error
            )
        }
    }

    fun queryAllPublications() = viewModelScope.launch {
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
