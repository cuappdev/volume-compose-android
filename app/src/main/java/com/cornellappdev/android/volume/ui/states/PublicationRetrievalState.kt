package com.cornellappdev.android.volume.ui.states

import com.cornellappdev.android.volume.data.models.Publication

/**
 * A sealed hierarchy describing the current status of retrieving the publication.
 */
sealed interface PublicationRetrievalState {
    /**
     * The publication is successfully retrieved with the given publication.
     */
    data class Success(val publication: Publication) : PublicationRetrievalState

    /**
     * There was an error in retrieving the article.
     */
    object Error : PublicationRetrievalState

    /**
     * The publication is in the process of being retrieved.
     */
    object Loading : PublicationRetrievalState
}
