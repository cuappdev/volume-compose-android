package com.cornellappdev.android.volume.ui.states

import com.cornellappdev.android.volume.data.models.Article

/**
 * A sealed hierarchy describing the current status of retrieving the article.
 */
sealed interface ArticleRetrievalState {
    /**
     * The article is successfully retrieved with the given article.
     */
    data class Success(val article: Article) : ArticleRetrievalState

    /**
     * There was an error in retrieving the article.
     */
    object Error : ArticleRetrievalState

    /**
     * The article are in the process of being retrieved.
     */
    object Loading : ArticleRetrievalState
}
