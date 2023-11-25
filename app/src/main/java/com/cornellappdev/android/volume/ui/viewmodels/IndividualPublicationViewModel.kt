package com.cornellappdev.android.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.repositories.ArticleRepository
import com.cornellappdev.android.volume.data.repositories.MagazineRepository
import com.cornellappdev.android.volume.data.repositories.PublicationRepository
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.volume.data.repositories.UserRepository
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import com.cornellappdev.android.volume.ui.states.PublicationRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IndividualPublicationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val articleRepository: ArticleRepository,
    private val magazineRepository: MagazineRepository,
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository,
) : ViewModel() {

    private val publicationSlug: String = checkNotNull(savedStateHandle["publicationSlug"])

    data class PublicationUiState(
        val publicationState: PublicationRetrievalState = PublicationRetrievalState.Loading,
        val articlesByPublicationState: ArticlesRetrievalState = ArticlesRetrievalState.Loading,
        val magazinesByPublicationState: MagazinesRetrievalState = MagazinesRetrievalState.Loading,
        val isFollowed: Boolean = false,
    )

    var publicationUiState by mutableStateOf(PublicationUiState())
        private set

    init {
        queryPublication()

    }

    fun followPublication() = viewModelScope.launch {
        val uuid = userPreferencesRepository.fetchUuid()
        userRepository.followPublication(publicationSlug, uuid)
        publicationUiState = publicationUiState.copy(
            isFollowed = true
        )
    }

    fun unfollowPublication() = viewModelScope.launch {
        val uuid = userPreferencesRepository.fetchUuid()
        userRepository.unfollowPublication(publicationSlug, uuid)
        publicationUiState = publicationUiState.copy(
            isFollowed = false
        )
    }

    private fun queryPublication() = viewModelScope.launch {
        try {
            val publication = publicationRepository.fetchPublicationBySlug(publicationSlug)
            publicationUiState = publicationUiState.copy(
                publicationState = PublicationRetrievalState.Success(
                    publication
                ),
                isFollowed = userRepository.getUser(userPreferencesRepository.fetchUuid()).followedPublicationSlugs.contains(
                    publicationSlug
                )
            )
            queryArticleByPublication()
        } catch (e: Exception) {
            publicationUiState = publicationUiState.copy(
                publicationState = PublicationRetrievalState.Error
            )
        }
    }


    private fun queryArticleByPublication() = viewModelScope.launch {
        try {
            publicationUiState = publicationUiState.copy(
                articlesByPublicationState = ArticlesRetrievalState.Success(
                    articleRepository.fetchArticlesByPublicationSlug(publicationSlug)
                )
            )
        } catch (e: Exception) {
            publicationUiState = publicationUiState.copy(
                articlesByPublicationState = ArticlesRetrievalState.Error
            )
        }
        queryMagazinesByPublication()
    }

    private fun queryMagazinesByPublication() = viewModelScope.launch {
        publicationUiState = try {
            publicationUiState.copy(
                magazinesByPublicationState = MagazinesRetrievalState.Success(
                    magazineRepository.fetchMagazinesByPublicationSlug(publicationSlug)
                )
            )
        } catch (e: Exception) {
            publicationUiState.copy(
                magazinesByPublicationState = MagazinesRetrievalState.Success(
                    magazineRepository.fetchMagazinesByPublicationSlug(publicationSlug)
                )
            )
        }
    }
}
