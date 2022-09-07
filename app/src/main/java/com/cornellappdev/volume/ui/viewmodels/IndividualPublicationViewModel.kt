package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.data.repositories.ArticleRepository
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IndividualPublicationViewModel(
    private val publicationId: String,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    // A factory is necessary to create a ViewModel with arguments
    class Factory(
        private val publicationId: String,
        private val userPreferencesRepository: UserPreferencesRepository
    ) :
        ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            IndividualPublicationViewModel(publicationId, userPreferencesRepository) as T
    }

    data class PublicationUIState(
        val publicationRetrievalState: PublicationRetrievalState,
        val articlesByPublication: ArticleState,
        val isFollowed: Boolean
    )

    sealed interface PublicationRetrievalState {
        data class Success(val publication: Publication) : PublicationRetrievalState
        object Error : PublicationRetrievalState
        object Loading : PublicationRetrievalState
    }

    sealed interface ArticleState {
        data class Success(val articles: List<Article>) : ArticleState
        object Error : ArticleState
        object Loading : ArticleState
    }

    // Backing property to avoid state updates from other classes
    private val _publicationByIDState =
        MutableStateFlow(
            PublicationUIState(
                publicationRetrievalState = PublicationRetrievalState.Loading,
                articlesByPublication = ArticleState.Loading,
                isFollowed = false
            )
        )

    // The UI collects from this StateFlow to get its state updates
    val publicationByIDState: StateFlow<PublicationUIState> = _publicationByIDState.asStateFlow()

    init {
        queryPublication()
        queryArticleByPublication()
    }

    fun followPublication() = viewModelScope.launch {
        val uuid = userPreferencesRepository.fetchUuid()
        UserRepository.followPublication(publicationId, uuid)
        _publicationByIDState.value = _publicationByIDState.value.copy(
            isFollowed = true
        )
    }

    fun unfollowPublication() = viewModelScope.launch {
        val uuid = userPreferencesRepository.fetchUuid()
        UserRepository.unfollowPublication(publicationId, uuid)
        _publicationByIDState.value = _publicationByIDState.value.copy(
            isFollowed = false
        )
    }

    private fun queryPublication() = viewModelScope.launch {
        try {
            _publicationByIDState.value = _publicationByIDState.value.copy(
                publicationRetrievalState = PublicationRetrievalState.Success(
                    PublicationRepository.fetchPublicationByID(
                        publicationId
                    ).first()
                ),
                isFollowed = UserRepository.getUser(userPreferencesRepository.fetchUuid()).followedPublicationIDs.contains(
                    publicationId
                )
            )
        } catch (e: Exception) {
            _publicationByIDState.value = _publicationByIDState.value.copy(
                publicationRetrievalState = PublicationRetrievalState.Error
            )
        }
    }

    private fun queryArticleByPublication() = viewModelScope.launch {
        try {
            _publicationByIDState.value = _publicationByIDState.value.copy(
                articlesByPublication = ArticleState.Success(
                    ArticleRepository.fetchArticlesByPublicationID(publicationId)
                )
            )
        } catch (e: Exception) {
            _publicationByIDState.value = _publicationByIDState.value.copy(
                articlesByPublication = ArticleState.Error
            )
        }
    }
}
