package com.cornellappdev.android.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.NetworkApi
import com.cornellappdev.android.volume.data.models.Organization
import com.cornellappdev.android.volume.data.repositories.OrganizationRepository
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.volume.data.repositories.UserRepository
import com.cornellappdev.android.volume.ui.states.ResponseState
import com.cornellappdev.android.volume.util.transformWithResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrganizationsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val organizationsRepository: OrganizationRepository,
    private val networkApi: NetworkApi,
) : ViewModel() {
    private val allOrganizationsFlow: MutableStateFlow<ResponseState<List<Organization>>> =
        MutableStateFlow(ResponseState.Loading)


    private val _followedOrganizationSlugsFlow: MutableStateFlow<ResponseState<List<String>>> =
        MutableStateFlow(ResponseState.Loading)

    /**
     * This flow represents a partition (p1, p2), where p1 is the organizations that the user follows,
     * and p2 is the organizations the user doesn't follow
     */
    val partitionedOrgsFlow: StateFlow<ResponseState<Pair<List<Organization>, List<Organization>>>> =
        allOrganizationsFlow.combine(_followedOrganizationSlugsFlow) { allOrganizations, followedSlugs ->
            transformWithResponseState(allOrganizations, followedSlugs) { orgs, slugs ->
                orgs.partition { it.slug in slugs }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ResponseState.Loading)


    init {
        queryAllOrganizations()
        queryFollowedOrganizationSlugs()
    }

    private fun queryFollowedOrganizationSlugs() = viewModelScope.launch {
        try {
            _followedOrganizationSlugsFlow.value = ResponseState.Loading
            val uuid = userPreferencesRepository.fetchUuid()
            val currentUser = userRepository.getUser(uuid)
            _followedOrganizationSlugsFlow.value =
                ResponseState.Success(currentUser.followedOrganizationSlugs)
        } catch (_: Exception) {
            _followedOrganizationSlugsFlow.value = ResponseState.Error()
        }
    }

    private fun queryAllOrganizations() = viewModelScope.launch {
        try {
            allOrganizationsFlow.value = ResponseState.Loading
            allOrganizationsFlow.value =
                ResponseState.Success(organizationsRepository.getAllOrganizations())
        } catch (_: Exception) {
            allOrganizationsFlow.value = ResponseState.Error()
        }
    }

    fun reload() {
        queryAllOrganizations()
        queryFollowedOrganizationSlugs()
    }

    fun followOrganization(organizationSlug: String) = viewModelScope.launch {
        try {
            val uuid = userPreferencesRepository.fetchUuid()
            userRepository.followOrganization(organizationSlug, uuid)
        } catch (_: Exception) {
        }
    }

    fun unfollowOrganization(organizationSlug: String) = viewModelScope.launch {
        try {
            val uuid = userPreferencesRepository.fetchUuid()
            userRepository.unfollowOrganization(organizationSlug, uuid)
        } catch (_: Exception) {
        }
    }

}