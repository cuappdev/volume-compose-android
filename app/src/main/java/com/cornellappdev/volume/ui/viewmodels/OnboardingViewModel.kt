package com.cornellappdev.volume.ui.viewmodels

import android.R
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class OnboardingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    // A factory is necessary to create a ViewModel with arguments
    class Factory(private val userPreferencesRepository: UserPreferencesRepository) :
        ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            OnboardingViewModel(userPreferencesRepository) as T
    }

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
    }

    fun createUser() = viewModelScope.launch {
        try {
            val listOfPubsFollowed = setOfPubsFollowed.toList()
            val user = UserRepository.createUser(
                listOfPubsFollowed,
                userPreferencesRepository.fetchDeviceToken()
            )
            userPreferencesRepository.updateUuid(user.uuid)
            UserRepository.followPublications(listOfPubsFollowed, user.uuid)
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
