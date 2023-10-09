package com.cornellappdev.android.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.NetworkApi
import com.cornellappdev.android.volume.data.models.Organization
import com.cornellappdev.android.volume.data.repositories.OrganizationRepository
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.volume.ui.states.ResponseState
import com.cornellappdev.android.volume.util.letIfAllNotNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrganizationLoginViewModel @Inject constructor
    (
    private val organizationRepository: OrganizationRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val networkApi: NetworkApi,
) : ViewModel() {
    data class OrganizationLoginUiState(
        val checkAccessCodeResult: ResponseState<Organization> = ResponseState.Loading,
    )

    var organizationsLoginUiState by mutableStateOf(OrganizationLoginUiState())
        private set

    init {
        viewModelScope.launch {
            letIfAllNotNull(
                userPreferencesRepository.fetchOrgAccessCode(),
                userPreferencesRepository.fetchOrgSlug()
            ) { (accessCode, slug) ->
                // We don't need to save here because we already have information
                checkAccessCode(accessCode, slug, false)
            }
        }
    }

    fun checkAccessCode(accessCode: String, organizationSlug: String, saveOnSuccess: Boolean) =
        viewModelScope.launch {
            val res = networkApi.verifyAccessCode(accessCode, organizationSlug)
            res.errors?.let {
                organizationsLoginUiState = organizationsLoginUiState.copy(
                    checkAccessCodeResult = ResponseState.Error(it)
                )
            }
            res.data?.checkAccessCode?.let {
                if (saveOnSuccess) {
                    userPreferencesRepository.updateOrgAccessCode(accessCode)
                    userPreferencesRepository.updateOrgSlug(organizationSlug)
                }

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