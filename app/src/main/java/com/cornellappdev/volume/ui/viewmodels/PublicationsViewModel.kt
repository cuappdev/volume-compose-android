package com.cornellappdev.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
import com.cornellappdev.volume.ui.states.PublicationsRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO test updating from individual publication view model onClick to publication tab. Follows
// similarly with how bookmarking works from the BookmarkScreen -> ArticleWebViewScreen
@HiltViewModel
class PublicationsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository
) :
    ViewModel() {

    data class PublicationsUiState(
        val followedPublicationsState: PublicationsRetrievalState=PublicationsRetrievalState.Loading,
        val morePublicationsState: PublicationsRetrievalState = PublicationsRetrievalState.Loading
    )

    var publicationsUiState by mutableStateOf(PublicationsUiState())
        private set

    init {
        queryAllPublications()
    }

    fun followPublication(id: String) = viewModelScope.launch {
        val uuid = userPreferencesRepository.fetchUuid()
        userRepository.followPublication(id, uuid)

        // move from more to followed
    }
    fun unfollowPublication(id: String)=viewModelScope.launch {
        val uuid=userPreferencesRepository.fetchUuid()
        userRepository.unfollowPublication(id, uuid)
    }

    private fun queryAllPublications() = viewModelScope.launch {
        try {
            val allPublications= publicationRepository.fetchAllPublications()
            val followedPublicationsId =
                userRepository.getUser(userPreferencesRepository.fetchUuid()).followedPublicationIDs
            val followedPublications = allPublications.filter {
                followedPublicationsId.contains(it.id)
            }
            val morePublicationsState = allPublications.filter {
                !followedPublicationsId.contains(it.id)
            }
            publicationsUiState = publicationsUiState.copy(
                followedPublicationsState = PublicationsRetrievalState.Success(
                    followedPublications
                ),
                morePublicationsState = PublicationsRetrievalState.Success(
                    morePublicationsState
                )
            )
        } catch (e: Exception) {
            publicationsUiState = publicationsUiState.copy(
                followedPublicationsState = PublicationsRetrievalState.Error,
                morePublicationsState = PublicationsRetrievalState.Error
            )
        }
    }
}
