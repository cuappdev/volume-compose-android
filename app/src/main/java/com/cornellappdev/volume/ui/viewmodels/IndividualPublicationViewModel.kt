package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.repositories.ArticleRepository
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
import com.cornellappdev.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.volume.ui.states.PublicationRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IndividualPublicationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    // Navigation arguments can be retrieved through the SavedStateHandle
    private val publicationId: String = checkNotNull(savedStateHandle["publicationId"])

    data class PublicationUIState(
        val publication: PublicationRetrievalState = PublicationRetrievalState.Loading,
        val articlesByPublication: ArticlesRetrievalState = ArticlesRetrievalState.Loading,
        val isFollowed: Boolean = false
    )

    // Backing property to avoid state updates from other classes
    private val _publicationByIDState =
        MutableStateFlow(PublicationUIState())

    // The UI collects from this StateFlow to get its state updates
    val publicationByIDState: StateFlow<PublicationUIState> = _publicationByIDState.asStateFlow()

    init {
        queryPublication()
        queryArticleByPublication()
    }

    fun followPublication() = viewModelScope.launch {
        val uuid = userPreferencesRepository.fetchUuid()
        userRepository.followPublication(publicationId, uuid)
        _publicationByIDState.value = _publicationByIDState.value.copy(
            isFollowed = true
        )
    }

    fun unfollowPublication() = viewModelScope.launch {
        val uuid = userPreferencesRepository.fetchUuid()
        userRepository.unfollowPublication(publicationId, uuid)
        _publicationByIDState.value = _publicationByIDState.value.copy(
            isFollowed = false
        )
    }

    private fun queryPublication() = viewModelScope.launch {
        try {
            _publicationByIDState.value = _publicationByIDState.value.copy(
                publication = PublicationRetrievalState.Success(
                    publicationRepository.fetchPublicationByID(
                        publicationId
                    ).first()
                ),
                isFollowed = userRepository.getUser(userPreferencesRepository.fetchUuid()).followedPublicationIDs.contains(
                    publicationId
                )
            )
        } catch (e: Exception) {
            _publicationByIDState.value = _publicationByIDState.value.copy(
                publication = PublicationRetrievalState.Error
            )
        }
    }

    private fun queryArticleByPublication() = viewModelScope.launch {
        try {
            _publicationByIDState.value = _publicationByIDState.value.copy(
                articlesByPublication = ArticlesRetrievalState.Success(
                    articleRepository.fetchArticlesByPublicationID(publicationId)
                )
            )
        } catch (e: Exception) {
            _publicationByIDState.value = _publicationByIDState.value.copy(
                articlesByPublication = ArticlesRetrievalState.Error
            )
        }
    }
}
