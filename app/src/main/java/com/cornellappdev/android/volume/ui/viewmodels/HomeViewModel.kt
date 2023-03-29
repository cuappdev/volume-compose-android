package com.cornellappdev.android.volume.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.models.Article
import com.cornellappdev.android.volume.data.models.Publication
import com.cornellappdev.android.volume.data.repositories.ArticleRepository
import com.cornellappdev.android.volume.data.repositories.PublicationRepository
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.volume.data.repositories.UserRepository
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.states.PublicationSlugsRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

// TODO add refreshing if user follows new publications?
// TODO optimize loading?
private const val TAG = "HomeViewModel"
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository
) : ViewModel() {
    companion object {
        const val NUMBER_OF_TRENDING_ARTICLES = 7.0
        const val NUMBER_OF_FOLLOWING_ARTICLES = 20
        const val NUMBER_OF_OTHER_ARTICLES = 45
    }

    data class HomeUiState(
        val trendingArticlesState: ArticlesRetrievalState = ArticlesRetrievalState.Loading,
        val otherArticlesState: ArticlesRetrievalState = ArticlesRetrievalState.Loading,
        val followingArticlesState: ArticlesRetrievalState = ArticlesRetrievalState.Loading,
        val publicationsState: PublicationSlugsRetrievalState = PublicationSlugsRetrievalState.Loading,

        /**
         * State that holds information on whether the user has any followed articles
         */
        val isFollowingEmpty: Boolean = false,
    )

    var homeUiState by mutableStateOf(HomeUiState())
        private set

    private var remainingFollowing: ArticlesRetrievalState = ArticlesRetrievalState.Loading

    init {
        queryTrendingArticles()
    }

    fun getNotificationPermissionFlowStatus() = runBlocking {
        return@runBlocking userPreferencesRepository.fetchNotificationFlowStatus()
    }

    fun updateNotificationPermissionFlowStatus(value: Boolean) = viewModelScope.launch {
        userPreferencesRepository.updateNotificationFlowStatus(value)
    }

    // Updates the state accordingly with the trending articles
    fun queryTrendingArticles(limit: Double? = NUMBER_OF_TRENDING_ARTICLES) =
        viewModelScope.launch {
            try {
                homeUiState = homeUiState.copy(
                    trendingArticlesState = ArticlesRetrievalState.Success(
                        articleRepository.fetchTrendingArticles(
                            limit
                        )
                    )
                )
                queryFollowingArticles()
            } catch (e: Exception) {
                homeUiState = homeUiState.copy(
                    trendingArticlesState = ArticlesRetrievalState.Error
                )
            }
        }

    fun queryFollowingArticles(limit: Int = NUMBER_OF_FOLLOWING_ARTICLES) =
        viewModelScope.launch {
            try {
                val followedPublications =
                    userRepository.getUser(userPreferencesRepository.fetchUuid()).followedPublicationSlugs
                val trendingArticlesIDs =
                    (homeUiState.trendingArticlesState as ArticlesRetrievalState.Success).articles.map(
                        Article::id
                    ).toHashSet()
                val followingArticles = articleRepository.fetchArticlesByPublicationSlugs(
                    followedPublications
                ).toMutableList()

                // Filters any articles the user follows that are trending.
                followingArticles.removeAll { article ->
                    trendingArticlesIDs.contains(article.id)
                }

                Article.sortByDate(followingArticles)

                var filteredArticles = followingArticles.take(limit)
                filteredArticles =
                    filteredArticles.ifEmpty { listOf() }

                homeUiState = homeUiState.copy(
                    isFollowingEmpty = filteredArticles.isEmpty()
                )

                // We took the first limit articles, the remaining ones (after removing them)
                // can be used for the other article section
                followingArticles.removeAll(
                    filteredArticles
                )

                homeUiState = homeUiState.copy(
                    followingArticlesState = ArticlesRetrievalState.Success(
                        filteredArticles
                    )
                )

                remainingFollowing = ArticlesRetrievalState.Success(followingArticles)

                queryAllPublications()
            } catch (e: Exception) {
                homeUiState = homeUiState.copy(
                    followingArticlesState = ArticlesRetrievalState.Error
                )
            }
        }

    fun queryAllPublications() =
        viewModelScope.launch {
             try {
                homeUiState = homeUiState.copy(
                    publicationsState = PublicationSlugsRetrievalState.Success(publicationRepository.fetchAllPublicationSlugs())
                )
                 Log.d(TAG, "queryAllPublications: PUBLICATIONS SUCCESS")
                queryShuffledArticlesByPublicationSlugs()
            } catch (e: Exception) {
                 Log.d(TAG, "queryAllPublications: PUBLICATIONS FAILURE")
                homeUiState = homeUiState.copy(
                    publicationsState = PublicationSlugsRetrievalState.Error
                )
                queryOtherArticles()
             }
        }

    /**
     * Fetches other articles.
     *
     * Other articles are articles excluding trending and from publications that the user follows.
     * If there isn't enough articles to reach the limit, articles are pulled from publications
     * that the user follows that aren't currently being used in the following section.
     *
     * @param limit how much other articles to fetch
     */
    private fun queryOtherArticles(limit: Int = NUMBER_OF_OTHER_ARTICLES) = viewModelScope.launch {
        Log.d(TAG, "queryOtherArticles: QUERYING OTHER ARTICLES")
        try {
            val followedPublications =
                userRepository.getUser(userPreferencesRepository.fetchUuid()).followedPublicationSlugs.toHashSet()
            val trendingArticlesIDs =
                (homeUiState.trendingArticlesState as ArticlesRetrievalState.Success).articles.map(
                    Article::id
                ).toHashSet()
            val remainingArticles =
                (remainingFollowing as ArticlesRetrievalState.Success).articles
            val allPublicationsExcludingFollowing =
                publicationRepository.fetchAllPublications().map(Publication::slug)
                    .toMutableList()
            allPublicationsExcludingFollowing.removeAll { slug ->
                followedPublications.contains(slug)
            }

            // Retrieves the articles from publications that the user doesn't follow.
            val otherArticles = articleRepository.fetchArticlesByPublicationSlugs(
                allPublicationsExcludingFollowing
            ).toMutableList()

            // Removes trending articles.
            otherArticles.removeAll { article ->
                trendingArticlesIDs.contains(article.id)
            }

            // If there isn't enough articles, we take from the remaining following articles.
            if (otherArticles.size < limit) {
                otherArticles.addAll(
                    remainingArticles.take(
                        limit - otherArticles.size
                    )
                )
            }

            // Lastly, we randomize the selection.
            homeUiState = homeUiState.copy(
                otherArticlesState = ArticlesRetrievalState.Success(
                    otherArticles.shuffled().take(limit)
                )
            )
        } catch (e: Exception) {
            homeUiState = homeUiState.copy(
                otherArticlesState = ArticlesRetrievalState.Error
            )
        }
    }

    private fun queryShuffledArticlesByPublicationSlugs(limit: Int = NUMBER_OF_OTHER_ARTICLES) = viewModelScope.launch {
        when (val publicationsState = homeUiState.publicationsState) {
            PublicationSlugsRetrievalState.Loading -> {
                Log.d(TAG, "queryOtherArticles: PUBLICATIONS STILL LOADING")
            }
            PublicationSlugsRetrievalState.Error -> {
                Log.d(TAG, "queryOtherArticles: PUBLICATIONS ERROR")
            }
            is PublicationSlugsRetrievalState.Success -> {
                try {
                    Log.d(TAG, "queryShuffledArticlesByPublicationSlugs: PUBLICATIONS SUCCESS 2")
                    homeUiState = homeUiState.copy(
                        otherArticlesState = ArticlesRetrievalState.Success(
                            articleRepository.fetchArticlesByShuffledPublicationSlugs(publicationsState.slugs)
                        )
                    )
                    Log.d(TAG, "queryShuffledArticlesByPublicationSlugs: SHUFFLED SUCCESS")
                } catch (e: Exception) {
                    Log.d(TAG, "queryOtherArticles: LOADING SHUFFLED ARTICLES FAILED")
                }
            }
        }
    }
}
