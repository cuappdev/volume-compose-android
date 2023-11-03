package com.cornellappdev.android.volume.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.NetworkApi
import com.cornellappdev.android.volume.data.models.Flyer
import com.cornellappdev.android.volume.data.models.Organization
import com.cornellappdev.android.volume.data.repositories.FlyerRepository
import com.cornellappdev.android.volume.data.repositories.OrganizationRepository
import com.cornellappdev.android.volume.ui.states.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlyerUploadViewModel @Inject constructor
    (
    private val organizationsRepository: OrganizationRepository,
    private val flyersRepository: FlyerRepository,
    private val networkApi: NetworkApi,
) : ViewModel() {
    private val _orgFlow: MutableStateFlow<ResponseState<Organization>> =
        MutableStateFlow(ResponseState.Loading)
    val orgFlow = _orgFlow.asStateFlow()

    private val _flyerFlow: MutableStateFlow<ResponseState<Flyer>> =
        MutableStateFlow(ResponseState.Loading)
    val flyerFlow = _flyerFlow.asStateFlow()

    private val _uploadResult: MutableStateFlow<ResponseState<Flyer>> =
        MutableStateFlow(ResponseState.Loading)
    val uploadResultFlow = _uploadResult.asStateFlow()

    fun initViewModel(organizationSlug: String, flyerId: String?) = viewModelScope.launch {
        try {
            _orgFlow.value =
                ResponseState.Success(organizationsRepository.getOrganizationBySlug(organizationSlug)!!)
        } catch (_: Exception) {
            _orgFlow.value = ResponseState.Error()
        }
        try {
            flyerId?.let {
                _flyerFlow.value = ResponseState.Success(flyersRepository.fetchFlyerById(flyerId))
            }
        } catch (_: Exception) {
            _flyerFlow.value = ResponseState.Error()
        }
    }

    /**
     * Updates the Flyer view model to show an error state for the upload result. Even though this
     * does violate encapsulation in a way,
     */
    fun errorFlyerUpload() {
        _uploadResult.value = ResponseState.Error()
    }

    fun uploadFlyer(flyer: Flyer, imageUri: Uri?, context: Context, isUpdating: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result =
                    networkApi.mutateFlyer(flyer, imageUri, context, isUpdating = isUpdating)
                _uploadResult.value =
                    if (result) ResponseState.Success(flyer) else ResponseState.Error()
            } catch (e: Exception) {
                _uploadResult.value = ResponseState.Error()
            }
        }
}