package com.cornellappdev.android.volume.ui.states

import com.cornellappdev.android.volume.data.models.Magazine

/**
 * A sealed hierarchy describing the current status of retrieving the article.
 */
sealed interface MagazineRetrievalState {
    /**
     * The article is successfully retrieved with the given article.
     */
    data class Success(val magazine: Magazine) : MagazineRetrievalState

    /**
     * There was an error in retrieving the article.
     */
    object Error : MagazineRetrievalState

    /**
     * The article are in the process of being retrieved.
     */
    object Loading : MagazineRetrievalState
}
