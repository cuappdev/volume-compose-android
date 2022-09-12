package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
import com.cornellappdev.volume.ui.states.PublicationsRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO test updating from individual publication view model onClick to publication tab. Follows
// similarly with how bookmarking works from the BookmarkScreen -> ArticleWebViewScreen
@HiltViewModel
class PublicationTabViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository
) :
    ViewModel() {

    data class AllPublicationUIState(
        val publication: PublicationsRetrievalState = PublicationsRetrievalState.Loading
    )

    private val _allPublicationsState =
        MutableStateFlow(AllPublicationUIState())

    val allPublicationsState: StateFlow<AllPublicationUIState> = _allPublicationsState.asStateFlow()

    init {
        // queryFollowedPublications()
        // queryMorePublications()
        queryAllPublications()
    }

    fun followPublication(id: String) = viewModelScope.launch {
        val uuid = userPreferencesRepository.fetchUuid()
        userRepository.followPublication(id, uuid)

        // move from more to followed
    }

    private fun queryAllPublications() = viewModelScope.launch {
        try {
            _allPublicationsState.value = _allPublicationsState.value.copy(
                publication = PublicationsRetrievalState.Success(
                    publicationRepository.fetchAllPublications()
                )
            )
        } catch (e: Exception) {
            _allPublicationsState.value = _allPublicationsState.value.copy(
                publication = PublicationsRetrievalState.Error
            )
        }
    }
}
