package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NavigationViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    data class OnboardingRetrievalState(
        val state: OnboardingState
    )

    sealed interface OnboardingState {
        data class Success(val onboardingCompleted: Boolean) : OnboardingState
        object Pending : OnboardingState
    }

    // Backing property to avoid state updates from other classes
    private val _onboardingState =
        MutableStateFlow(OnboardingRetrievalState(OnboardingState.Pending))

    // The UI collects from this StateFlow to get its state updates
    val onboardingState: StateFlow<OnboardingRetrievalState> = _onboardingState.asStateFlow()


    init {
        fetchOnboardingCompleted()
    }

    fun fetchOnboardingCompleted() = viewModelScope.launch {
        _onboardingState.value =
            OnboardingRetrievalState(OnboardingState.Success(userPreferencesRepository.fetchOnboardingCompleted()))
    }
}

