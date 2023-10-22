package com.cornellappdev.android.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.models.Flyer
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
    private val organizationRepository: OrganizationRepository,
    private val flyerRepository: FlyerRepository,
) : ViewModel() {

    private val _orgFlyersFlow: MutableStateFlow<ResponseState<List<Flyer>>> =
        MutableStateFlow(ResponseState.Loading)

    private val orgFlyersFlow: StateFlow<ResponseState<List<Flyer>>> = _orgFlyersFlow.asStateFlow()

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

    val pastFlyersFlow: StateFlow<ResponseState<List<Flyer>>> = orgFlyersFlow.map { apiResponse ->
        when (apiResponse) {
            ResponseState.Loading -> ResponseState.Loading
            is ResponseState.Error -> ResponseState.Error()
            is ResponseState.Success -> {
                val flyers = apiResponse.data
                ResponseState.Success(flyers.filter { it.endDateTime < LocalDateTime.now() })
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ResponseState.Loading)

    fun initViewModel(organizationId: String) = viewModelScope.launch {
        try {
            _orgFlyersFlow.value =
                ResponseState.Success(flyerRepository.fetchFlyersByOrganizationId(organizationId))
        } catch (_: Exception) {
            _orgFlyersFlow.value = ResponseState.Error()
        }
    }

}