package com.cornellappdev.android.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.models.Flyer
import com.cornellappdev.android.volume.data.models.Organization
import com.cornellappdev.android.volume.data.repositories.FlyerRepository
import com.cornellappdev.android.volume.data.repositories.OrganizationRepository
import com.cornellappdev.android.volume.ui.states.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class OrganizationsHomeViewModel @Inject constructor(
    private val flyerRepository: FlyerRepository,
    private val orgRepository: OrganizationRepository,
) : ViewModel() {

    private val _orgFlyersFlow: MutableStateFlow<ResponseState<List<Flyer>>> =
        MutableStateFlow(ResponseState.Loading)

    private val orgFlyersFlow: StateFlow<ResponseState<List<Flyer>>> = _orgFlyersFlow.asStateFlow()

    private val _orgFlow: MutableStateFlow<ResponseState<Organization>> =
        MutableStateFlow(ResponseState.Loading)

    val orgFlow = _orgFlow.asStateFlow()

    val currentFlyersFlow = orgFlyersFlow.map { apiResponse ->
        when (apiResponse) {
            ResponseState.Loading -> ResponseState.Loading
            is ResponseState.Error -> ResponseState.Error()
            is ResponseState.Success -> {
                val flyers = apiResponse.data
                ResponseState.Success(flyers.filter { it.endDateTime >= LocalDateTime.now() })
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ResponseState.Loading)

    val pastFlyersFlow = orgFlyersFlow.map { apiResponse ->
        when (apiResponse) {
            ResponseState.Loading -> ResponseState.Loading
            is ResponseState.Error -> ResponseState.Error()
            is ResponseState.Success -> {
                val flyers = apiResponse.data
                ResponseState.Success(flyers.filter { it.endDateTime < LocalDateTime.now() })
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ResponseState.Loading)

    fun initViewModel(organizationSlug: String) = viewModelScope.launch {
        try {
            _orgFlyersFlow.value =
                ResponseState.Success(flyerRepository.fetchFlyersByOrganizationSlug(organizationSlug))
            // Non-null assertion is ok because we are in try-catch
            _orgFlow.value = orgRepository.getOrganizationBySlug(organizationSlug)?.let {
                ResponseState.Success(it)
            } ?: ResponseState.Error()
        } catch (e: Exception) {
            _orgFlyersFlow.value = ResponseState.Error()
        }
    }
}