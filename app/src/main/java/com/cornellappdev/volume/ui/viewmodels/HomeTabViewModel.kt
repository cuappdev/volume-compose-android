package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.data.repositories.ArticleRepository
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
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

    data class ArticlesState(
        val trendingArticlesState: ArticleState,
        val otherArticlesState: ArticleState,
        val followingArticlesState: ArticleState,
        val remainingFollowing: ArticleState
    )

    sealed interface ArticleState {
        data class Success(val articles: List<Article>) : ArticleState
        object Error : ArticleState
        object Loading : ArticleState
    }

    private val _articlesState = MutableStateFlow(
        ArticlesState(
            trendingArticlesState = ArticleState.Loading,
            otherArticlesState = ArticleState.Loading,
            followingArticlesState = ArticleState.Loading,
            remainingFollowing = ArticleState.Loading
        )
    )

    val articlesState: StateFlow<ArticlesState> =
        _articlesState.asStateFlow()

    init {
        queryTrendingArticles()
    }

    // Updates the state accordingly with the trending articles
    fun queryTrendingArticles(limit: Double? = NUMBER_OF_TRENDING_ARTICLES) =
        viewModelScope.launch {
            try {
                _articlesState.value = _articlesState.value.copy(
                    trendingArticlesState = ArticleState.Success(
                        articleRepository.fetchTrendingArticles(
                            limit
                        )
                    )
                )
                queryFollowingArticles()
            } catch (e: Exception) {
                _articlesState.value = _articlesState.value.copy(
                    trendingArticlesState = ArticleState.Error
                )
            }
        }

    fun queryFollowingArticles(limit: Int = NUMBER_OF_FOLLOWING_ARTICLES) =
        viewModelScope.launch {
            try {
                val followedPublications =
                    userRepository.getUser(userPreferencesRepository.fetchUuid()).followedPublicationIDs
                val trendingArticlesIDs =
                    (_articlesState.value.trendingArticlesState as ArticleState.Success).articles.map(
                        Article::id
                    ).toHashSet()
                val followingArticles = articleRepository.fetchArticlesByPublicationIDs(
                    followedPublications.toMutableList()
                ).toMutableList()

                // Filters any articles the user follows that are trending.
                followingArticles.removeAll { article ->
                    trendingArticlesIDs.contains(article.id)
                }

                Article.sortByDate(followingArticles)

                var filteredArticles = followingArticles.take(limit)
                filteredArticles =
                    filteredArticles.ifEmpty { listOf() }

                // We took the first limit articles, the remaining ones (after removing them)
                // can be used for the other article section
                followingArticles.removeAll(
                    filteredArticles
                )

                _articlesState.value = _articlesState.value.copy(
                    followingArticlesState = ArticleState.Success(
                        filteredArticles
                    ),
                    remainingFollowing = ArticleState.Success(followingArticles)
                )

                queryOtherArticles()
            } catch (e: Exception) {
                _articlesState.value = _articlesState.value.copy(
                    followingArticlesState = ArticleState.Error
                )
            }
        }

    fun queryOtherArticles(limit: Int = NUMBER_OF_OTHER_ARTICLES) = viewModelScope.launch {
        try {
            val followedPublications =
                userRepository.getUser(userPreferencesRepository.fetchUuid()).followedPublicationIDs.toHashSet()
            val trendingArticlesIDs =
                (_articlesState.value.trendingArticlesState as ArticleState.Success).articles.map(
                    Article::id
                ).toHashSet()
            val remainingArticles =
                (_articlesState.value.remainingFollowing as ArticleState.Success).articles
            val allPublicationsExcludingFollowing =
                publicationRepository.fetchAllPublications().map(Publication::id)
                    .toMutableList()
            allPublicationsExcludingFollowing.removeAll { id ->
                followedPublications.contains(id)
            }

            // Other articles are the articles on volume that aren't from publications
            // that are followed by the user and the big read.
            val otherArticles = articleRepository.fetchArticlesByPublicationIDs(
                allPublicationsExcludingFollowing
            ).toMutableList()
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

            _articlesState.value = _articlesState.value.copy(
                otherArticlesState = ArticleState.Success(
                    otherArticles.shuffled().take(limit)
                )
            )
        } catch (e: Exception) {
            _articlesState.value = _articlesState.value.copy(
                otherArticlesState = ArticleState.Error
            )
        }
    }
}
