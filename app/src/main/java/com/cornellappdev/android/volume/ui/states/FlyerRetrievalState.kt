package com.cornellappdev.android.volume.ui.states

import com.cornellappdev.android.volume.data.models.Flyer

/**
 * A sealed hierarchy describing the current status of retrieving the flyers.
 */
sealed interface FlyersRetrievalState {
    /**
     * The flyers are successfully retrieved with the given list.
     */
    data class Success(val flyers: List<Flyer>) : FlyersRetrievalState

    /**
     * There was an error in retrieving the flyers.
     */
    object Error : FlyersRetrievalState

    /**
     * The flyers are in the process of being retrieved.
     */
    object Loading : FlyersRetrievalState
}
