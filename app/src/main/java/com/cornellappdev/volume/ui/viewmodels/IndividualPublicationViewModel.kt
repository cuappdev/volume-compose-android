package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.data.repositories.ArticleRepository
import com.cornellappdev.volume.data.repositories.PublicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IndividualPublicationViewModel(
    private val articleRepository: ArticleRepository,
    private val publicationRepository: PublicationRepository,
    id: String
) : ViewModel() {
    data class ArticlesByPublicationUIState(
        val articleState: ArticleState
    )

    data class PublicationUIState(
        val publicationRetrievalState: PublicationRetrievalState
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
        MutableStateFlow(PublicationUIState(PublicationRetrievalState.Loading))

    // The UI collects from this StateFlow to get its state updates
    val publicationByIDState: StateFlow<PublicationUIState> = _publicationByIDState.asStateFlow()

    // Backing property to avoid state updates from other classes
    private val _articlesByPubIDState =
        MutableStateFlow(ArticlesByPublicationUIState(ArticleState.Loading))

    // The UI collects from this StateFlow to get its state updates
    val articlesByPubIDState: StateFlow<ArticlesByPublicationUIState> =
        _articlesByPubIDState.asStateFlow()

    init {
        queryPublicationByID(id)
        queryArticleByPublicationID(id)
    }

    private fun queryPublicationByID(id: String) = viewModelScope.launch {
        try {
            _publicationByIDState.value = _publicationByIDState.value.copy(
                publicationRetrievalState = PublicationRetrievalState.Success(
                    publicationRepository.fetchPublicationByID(
                        id
                    ).first()
                )
            )
        } catch (e: Exception) {
            _publicationByIDState.value = _publicationByIDState.value.copy(
                publicationRetrievalState = PublicationRetrievalState.Error
            )
        }
    }

    private fun queryArticleByPublicationID(id: String) = viewModelScope.launch {
        try {
            _articlesByPubIDState.value = _articlesByPubIDState.value.copy(
                articleState = ArticleState.Success(
                    articleRepository.fetchArticlesByPublicationID(id)
                )
            )
        } catch (e: Exception) {
            _articlesByPubIDState.value = _articlesByPubIDState.value.copy(
                articleState = ArticleState.Error
            )
        }
    }
}
