package com.cornellappdev.android.volume.ui.states

/**
 * A sealed hierarchy describing the current status of retrieving the publication.
 */
sealed interface PublicationSlugsRetrievalState {
    /**
     * The publication is successfully retrieved with the given publication.
     */
    data class Success(val slugs: List<String>) : PublicationSlugsRetrievalState

    /**
     * There was an error in retrieving the article.
     */
    object Error : PublicationSlugsRetrievalState

    /**
     * The publication is in the process of being retrieved.
     */
    object Loading : PublicationSlugsRetrievalState
}
