package com.cornellappdev.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.data.repositories.ArticleRepository
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
import com.cornellappdev.volume.ui.states.ArticlesRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO add refreshing if user follows new users?
// TODO optimize loading?
@HiltViewModel
class HomeTabViewModel @Inject constructor(
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

    data class HomeState(
        val trendingArticles: ArticlesRetrievalState = ArticlesRetrievalState.Loading,
        val otherArticles: ArticlesRetrievalState = ArticlesRetrievalState.Loading,
        val followingArticles: ArticlesRetrievalState = ArticlesRetrievalState.Loading,
        val remainingFollowing: ArticlesRetrievalState = ArticlesRetrievalState.Loading
    )

    private val _homeState = MutableStateFlow(HomeState())

    val homeState: StateFlow<HomeState> =
        _homeState.asStateFlow()

    /**
     * State that holds information on whether the user has any followed articles
     */
    var isFollowingEmpty by mutableStateOf(false)
        private set

    init {
        queryTrendingArticles()
    }

    // Updates the state accordingly with the trending articles
    fun queryTrendingArticles(limit: Double? = NUMBER_OF_TRENDING_ARTICLES) =
        viewModelScope.launch {
            try {
                _homeState.value = _homeState.value.copy(
                    trendingArticles = ArticlesRetrievalState.Success(
                        articleRepository.fetchTrendingArticles(
                            limit
                        )
                    )
                )
                queryFollowingArticles()
            } catch (e: Exception) {
                _homeState.value = _homeState.value.copy(
                    trendingArticles = ArticlesRetrievalState.Error
                )
            }
        }

    fun queryFollowingArticles(limit: Int = NUMBER_OF_FOLLOWING_ARTICLES) =
        viewModelScope.launch {
            try {
                val followedPublications =
                    userRepository.getUser(userPreferencesRepository.fetchUuid()).followedPublicationIDs
                val trendingArticlesIDs =
                    (_homeState.value.trendingArticles as ArticlesRetrievalState.Success).articles.map(
                        Article::id
                    ).toHashSet()
                val followingArticles = articleRepository.fetchArticlesByPublicationIDs(
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

                isFollowingEmpty = filteredArticles.isEmpty()

                // We took the first limit articles, the remaining ones (after removing them)
                // can be used for the other article section
                followingArticles.removeAll(
                    filteredArticles
                )

                _homeState.value = _homeState.value.copy(
                    followingArticles = ArticlesRetrievalState.Success(
                        filteredArticles
                    ),
                    remainingFollowing = ArticlesRetrievalState.Success(followingArticles)
                )

                queryOtherArticles()
            } catch (e: Exception) {
                _homeState.value = _homeState.value.copy(
                    followingArticles = ArticlesRetrievalState.Error
                )
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
    fun queryOtherArticles(limit: Int = NUMBER_OF_OTHER_ARTICLES) = viewModelScope.launch {
        try {
            val followedPublications =
                userRepository.getUser(userPreferencesRepository.fetchUuid()).followedPublicationIDs.toHashSet()
            val trendingArticlesIDs =
                (_homeState.value.trendingArticles as ArticlesRetrievalState.Success).articles.map(
                    Article::id
                ).toHashSet()
            val remainingArticles =
                (_homeState.value.remainingFollowing as ArticlesRetrievalState.Success).articles
            val allPublicationsExcludingFollowing =
                publicationRepository.fetchAllPublications().map(Publication::id)
                    .toMutableList()
            allPublicationsExcludingFollowing.removeAll { id ->
                followedPublications.contains(id)
            }

            // Retrieves the articles from publications that the user doesn't follow.
            val otherArticles = articleRepository.fetchArticlesByPublicationIDs(
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
            _homeState.value = _homeState.value.copy(
                otherArticles = ArticlesRetrievalState.Success(
                    otherArticles.shuffled().take(limit)
                )
            )
        } catch (e: Exception) {
            _homeState.value = _homeState.value.copy(
                otherArticles = ArticlesRetrievalState.Error
            )
        }
    }
}
