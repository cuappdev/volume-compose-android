package com.cornellappdev.android.volume.ui.states

import com.cornellappdev.android.volume.data.models.Magazine

/**
 * A sealed hierarchy describing the current status of retrieving the articles.
 */
sealed interface MagazinesRetrievalState {
    /**
     * The magazines are successfully retrieved with the given list.
     */
    data class Success(val magazines: List<Magazine>) : MagazinesRetrievalState

    /**
     * There was an error in retrieving the articles.
     */
    object Error : MagazinesRetrievalState

    /**
     * The magazines are in the process of being retrieved.
     */
    object Loading : MagazinesRetrievalState
}
