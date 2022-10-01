package com.cornellappdev.volume.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.analytics.EventType
import com.cornellappdev.volume.analytics.VolumeEvent
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
import com.cornellappdev.volume.ui.states.PublicationsRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    sealed interface CreateUserState {
        object Success : CreateUserState
        object Pending : CreateUserState
        object Error : CreateUserState
    }

    data class OnboardingUiState(
        val createUserState: CreateUserState = CreateUserState.Pending,
        val publicationsState: PublicationsRetrievalState = PublicationsRetrievalState.Loading
    )

    var onboardingUiState by mutableStateOf(OnboardingUiState())
        private set

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
            Log.d("GoingHome", "CreatingUser")
            val listOfPubsFollowed = setOfPubsFollowed.toList()
            val user = userRepository.createUser(
                listOfPubsFollowed,
                userPreferencesRepository.fetchDeviceToken()
            )
            userPreferencesRepository.updateUuid(user.uuid)
            userRepository.followPublications(listOfPubsFollowed, user.uuid)
            onboardingUiState = onboardingUiState.copy(
                createUserState = CreateUserState.Success
            )
        } catch (e: Exception) {
            Log.d("GoingHome", "Error")
            Log.d("GoingHome", e.stackTraceToString())
            onboardingUiState = onboardingUiState.copy(
                createUserState = CreateUserState.Error
            )
        }
    }

    fun queryAllPublications() = viewModelScope.launch {
        try {
            onboardingUiState = onboardingUiState.copy(
                publicationsState = PublicationsRetrievalState.Success(
                    publicationRepository.fetchAllPublications()
                )
            )
        } catch (e: Exception) {
            onboardingUiState = onboardingUiState.copy(
                publicationsState = PublicationsRetrievalState.Error
            )
        }
    }
}