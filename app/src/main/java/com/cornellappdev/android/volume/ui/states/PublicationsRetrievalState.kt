package com.cornellappdev.android.volume.ui.states

import com.cornellappdev.android.volume.data.models.Publication

/**
 * A sealed hierarchy describing the current status of retrieving the publications.
 */
sealed interface PublicationsRetrievalState {
    /**
     * The publications are successfully retrieved with the given list.
     */
    data class Success(val publications: List<Publication>) : PublicationsRetrievalState

    /**
     * There was an error in retrieving the publications.
     */
    object Error : PublicationsRetrievalState

    /**
     * The publications are in the process of being retrieved.
     */
    object Loading : PublicationsRetrievalState
}
