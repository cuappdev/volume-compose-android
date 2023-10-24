package com.cornellappdev.android.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.models.Organization
import com.cornellappdev.android.volume.data.repositories.FlyerRepository
import com.cornellappdev.android.volume.data.repositories.OrganizationRepository
import com.cornellappdev.android.volume.ui.states.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlyerUploadViewModel @Inject constructor
    (
    private val organizationsRepository: OrganizationRepository,
    private val flyersRepository: FlyerRepository,
) : ViewModel() {
    data class FlyerUploadUiState(
        val uploadFlyerResult: ResponseState<String> = ResponseState.Loading,
        val organizationInfoResult: ResponseState<Organization> = ResponseState.Loading,
    )

    var uploadFlyerUiState by mutableStateOf(FlyerUploadUiState())
        private set

    fun getOrganization(slug: String) = viewModelScope.launch {
        uploadFlyerUiState = try {
            uploadFlyerUiState.copy(
                organizationInfoResult = ResponseState.Success(
                    // Non-null assertion is ok since we are inside try catch
                    organizationsRepository.getOrganizationBySlug(slug)!!
                )
            )
        } catch (e: Exception) {
            uploadFlyerUiState.copy(
                organizationInfoResult = ResponseState.Error()
            )
        }
    }

    fun error() {
        uploadFlyerUiState = uploadFlyerUiState.copy(
            uploadFlyerResult = ResponseState.Error(listOf())
        )
    }

    fun uploadFlyer(
        title: String,
        startDate: String,
        location: String,
        flyerURL: String,
        endDate: String,
        categorySlug: String,
        imageBase64: String,
        organizationId: String,
    ) =
        viewModelScope.launch {
            val res = flyersRepository.createFlyer(
                title,
                startDate,
                location,
                flyerURL,
                endDate,
                categorySlug,
                imageBase64,
                organizationId
            )
            res.errors?.let {
                uploadFlyerUiState = uploadFlyerUiState.copy(
                    uploadFlyerResult = ResponseState.Error(it)
                )
            }
            res.data?.let {
                val createdFlyer = it.createFlyer
                uploadFlyerUiState = uploadFlyerUiState.copy(
                    uploadFlyerResult = ResponseState.Success(title)
                )
            }
        }
}