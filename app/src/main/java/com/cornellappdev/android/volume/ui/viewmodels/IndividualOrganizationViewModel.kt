package com.cornellappdev.android.volume.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.models.Flyer
import com.cornellappdev.android.volume.data.models.Organization
import com.cornellappdev.android.volume.data.repositories.FlyerRepository
import com.cornellappdev.android.volume.data.repositories.OrganizationRepository
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.volume.data.repositories.UserRepository
import com.cornellappdev.android.volume.ui.states.ResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class IndividualOrganizationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val organizationRepository: OrganizationRepository,
    private val userRepository: UserRepository,
    private val flyersRepository: FlyerRepository,
) : ViewModel() {
    private val _orgFlyersFlow: MutableStateFlow<ResponseState<List<Flyer>>> =
        MutableStateFlow(ResponseState.Loading)
    val orgFlyersFlow = _orgFlyersFlow.asStateFlow()

    private val _orgFlow: MutableStateFlow<ResponseState<Organization>> =
        MutableStateFlow(ResponseState.Loading)
    val orgFlow = _orgFlow.asStateFlow()

    private val _isFollowingFlow: MutableStateFlow<ResponseState<Boolean>> =
        MutableStateFlow(ResponseState.Loading)
    val isFollowingFlow = _isFollowingFlow.asStateFlow()

    private val organizationSlug: String = checkNotNull(savedStateHandle["organizationSlug"])

    init {
        loadOrganization()
        checkFollowing()
        loadFlyers()
    }

    private fun loadOrganization() = viewModelScope.launch {
        try {
            _orgFlow.value = ResponseState.Success(
                organizationRepository.getOrganizationBySlug(organizationSlug)!!
            )
        } catch (_: Exception) {
            _orgFlow.value = ResponseState.Error()
        }

    }

    private fun loadFlyers() = viewModelScope.launch {
        try {
            _orgFlyersFlow.value = ResponseState.Success(
                flyersRepository.fetchFlyersByOrganizationSlug(organizationSlug)
            )
        } catch (_: Exception) {
            _orgFlyersFlow.value = ResponseState.Error()
        }
    }

    private fun checkFollowing() = viewModelScope.launch {
        try {
            val user = userRepository.getUser(userPreferencesRepository.fetchUuid())
            _isFollowingFlow.value =
                ResponseState.Success(organizationSlug in user.followedOrganizationSlugs)
        } catch (_: Exception) {
            _isFollowingFlow.value = ResponseState.Error()
        }

    }
}