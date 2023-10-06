package com.cornellappdev.android.volume.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.NetworkApi
import com.cornellappdev.android.volume.data.models.Organization
import com.cornellappdev.android.volume.data.repositories.OrganizationRepository
import com.cornellappdev.android.volume.ui.states.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrganizationLoginViewModel @Inject constructor
    (
    private val organizationRepository: OrganizationRepository,
    private val networkApi: NetworkApi,
) : ViewModel() {
    data class OrganizationLoginUiState(
        val checkAccessCodeResult: ResponseState<Organization> = ResponseState.Loading,
    )

    var organizationsLoginUiState by mutableStateOf(OrganizationLoginUiState())
        private set

    fun checkAccessCode(accessCode: String, organizationSlug: String) = viewModelScope.launch {
        Log.d(
            "TAG",
            "checkAccessCode: access code = $accessCode, organization slug = $organizationSlug"
        )
        val res = networkApi.verifyAccessCode(accessCode, organizationSlug)
        res.errors?.let {
            organizationsLoginUiState = organizationsLoginUiState.copy(
                // We can use non-null assertion since
                checkAccessCodeResult = ResponseState.Error(res.errors!!)
            )
        }
        res.data?.checkAccessCode?.let {
            organizationsLoginUiState = organizationsLoginUiState.copy(
                checkAccessCodeResult = ResponseState.Success(
                    Organization(
                        name = it.name,
                        categorySlug = it.categorySlug,
                        websiteURL = it.websiteURL,
                        id = it.id
                    )
                )
            )
        }
    }
}