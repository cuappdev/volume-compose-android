package com.cornellappdev.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cornellappdev.volume.CreateUserMutation
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
import com.cornellappdev.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.volume.ui.states.PublicationRetrievalState
import com.cornellappdev.volume.ui.states.PublicationsRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.nio.file.Files.copy
import java.util.Collections.copy
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

    private val _setOfPubsFollowed = mutableSetOf<String>()

    data class PublicationsUiState(
        val followedPublicationsState: PublicationsRetrievalState=PublicationsRetrievalState.Loading,
        val unfollowedPublicationsState: PublicationsRetrievalState=PublicationsRetrievalState.Loading
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
    fun addPublicationToFollowed(pubId: String) =  _setOfPubsFollowed.add(pubId)

    fun removePublicationFromFollowed(pubId: String) = _setOfPubsFollowed.remove(pubId)

    private fun queryAllPublications() = viewModelScope.launch {
        try {
            val allPublications= publicationRepository.fetchAllPublications()
            val followedPublicationsId =
                userRepository.getUser(userPreferencesRepository.fetchUuid()).followedPublicationIDs
            val followedPublications= allPublications.filter {
                followedPublicationsId.contains(it.id)
            }
            val unfollowedPublications= allPublications.filter{
                !followedPublicationsId.contains(it.id)
            }
            publicationsUiState = publicationsUiState.copy(
                followedPublicationsState = PublicationsRetrievalState.Success(
                    followedPublications
                ),
                unfollowedPublicationsState = PublicationsRetrievalState.Success(
                    unfollowedPublications
                )
            )
        } catch (e: Exception) {
            publicationsUiState = publicationsUiState.copy(
                followedPublicationsState = PublicationsRetrievalState.Error,
                unfollowedPublicationsState = PublicationsRetrievalState.Error
            )
        }
    }

}
