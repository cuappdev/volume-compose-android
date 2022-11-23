package com.cornellappdev.android.volume.ui.states

import com.cornellappdev.android.volume.data.models.Article

/**
 * A sealed hierarchy describing the current status of retrieving the articles.
 */
sealed interface ArticlesRetrievalState {
    /**
     * The articles are successfully retrieved with the given list.
     */
    data class Success(val articles: List<Article>) : ArticlesRetrievalState

    /**
     * There was an error in retrieving the articles.
     */
    object Error : ArticlesRetrievalState

    /**
     * The articles are in the process of being retrieved.
     */
    object Loading : ArticlesRetrievalState
}
