package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.data.repositories.ArticleRepository
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// TODO test following publication updates if articles update
// TODO optimize loading?
class HomeTabViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val articleRepository: ArticleRepository = ArticleRepository,
    private val publicationRepository: PublicationRepository = PublicationRepository,
    private val userRepository: UserRepository = UserRepository,
) : ViewModel() {

    companion object {
        const val NUMBER_OF_TRENDING_ARTICLES = 7.0
        const val NUMBER_OF_FOLLOWING_ARTICLES = 20
        const val NUMBER_OF_OTHER_ARTICLES = 45
    }

    data class TrendingArticlesUiState(
        val articleState: ArticleState
    )

    data class OtherArticlesUiState(
        val articleState: ArticleState
    )

    data class FollowedArticlesUiState(
        val articleState: ArticleState,
        val remainingFollowingArticleState: ArticleState
    )

    sealed interface ArticleState {
        data class Success(val article: List<Article>) : ArticleState
        object Error : ArticleState
        object Loading : ArticleState
    }

    // Backing property to avoid state updates from other classes
    private val _trendingArticlesState =
        MutableStateFlow(TrendingArticlesUiState(ArticleState.Loading))

    // The UI collects from this StateFlow to get its state updates
    val trendingArticlesState: StateFlow<TrendingArticlesUiState> =
        _trendingArticlesState.asStateFlow()

    // Backing property to avoid state updates from other classes
    private val _followedArticlesState =
        MutableStateFlow(FollowedArticlesUiState(ArticleState.Loading, ArticleState.Loading))

    // The UI collects from this StateFlow to get its state updates
    val followedArticlesState: StateFlow<FollowedArticlesUiState> =
        _followedArticlesState.asStateFlow()

    // Backing property to avoid state updates from other classes
    private val _otherArticlesState = MutableStateFlow(OtherArticlesUiState(ArticleState.Loading))

    // The UI collects from this StateFlow to get its state updates
    val otherArticlesState: StateFlow<OtherArticlesUiState> = _otherArticlesState.asStateFlow()

    // TODO test, not entirely sure if this works since each function following the one before
    // depends on the state
    init {
        queryTrendingArticles()
    }

    // Updates the state accordingly with the trending articles
    fun queryTrendingArticles(limit: Double? = NUMBER_OF_TRENDING_ARTICLES) =
        viewModelScope.launch {
            try {
                _trendingArticlesState.value = _trendingArticlesState.value.copy(
                    articleState = ArticleState.Success(
                        articleRepository.fetchTrendingArticles(
                            limit
                        )
                    )
                )

                queryFollowingArticles()
            } catch (e: Exception) {
                _trendingArticlesState.value = _trendingArticlesState.value.copy(
                    articleState = ArticleState.Error
                )
            }
        }

    fun queryFollowingArticles(limit: Int = NUMBER_OF_FOLLOWING_ARTICLES) =
        viewModelScope.launch {
            try {
                when (_trendingArticlesState.value.articleState) {
                    ArticleState.Error -> {
                        throw Exception()
                    }
                    is ArticleState.Success -> {
                        val followedPublications =
                            userRepository.getUser(userPreferencesRepository.fetchUuid()).followedPublicationIDs
                        val trendingArticlesIDs =
                            (_trendingArticlesState.value.articleState as ArticleState.Success).article.map(
                                Article::id
                            ).toHashSet()
                        val followingArticles = ArticleRepository.fetchArticlesByPublicationIDs(
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

                        _followedArticlesState.value = _followedArticlesState.value.copy(
                            articleState = ArticleState.Success(
                                filteredArticles
                            ),
                            remainingFollowingArticleState = ArticleState.Success(followingArticles)
                        )

                        queryOtherArticles()
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _followedArticlesState.value = _followedArticlesState.value.copy(
                    articleState = ArticleState.Error
                )
            }
        }

    fun queryOtherArticles(limit: Int = NUMBER_OF_OTHER_ARTICLES) = viewModelScope.launch {
        try {
            when (_followedArticlesState.value.articleState) {
                ArticleState.Error -> {
                    throw Exception()
                }
                is ArticleState.Success -> {
                    val followedPublications =
                        userRepository.getUser(userPreferencesRepository.fetchUuid()).followedPublicationIDs.toHashSet()
                    val trendingArticlesIDs =
                        (_trendingArticlesState.value.articleState as ArticleState.Success).article.map(
                            Article::id
                        ).toHashSet()
                    val remainingArticles =
                        (_followedArticlesState.value.remainingFollowingArticleState as ArticleState.Success).article
                    val allPublicationsExcludingFollowing =
                        publicationRepository.fetchAllPublications().map(Publication::id)
                            .toMutableList()
                    allPublicationsExcludingFollowing.removeAll { id ->
                        followedPublications.contains(id)
                    }
                    val otherArticles = ArticleRepository.fetchArticlesByPublicationIDs(
                        allPublicationsExcludingFollowing
                    ).toMutableList()
                    otherArticles.removeAll { article ->
                        trendingArticlesIDs.contains(article.id)
                    }

                    if (otherArticles.size < NUMBER_OF_OTHER_ARTICLES) {
                        otherArticles.addAll(
                            remainingArticles.take(
                                NUMBER_OF_OTHER_ARTICLES - otherArticles.size
                            )
                        )
                    }

                    _otherArticlesState.value = _otherArticlesState.value.copy(
                        articleState = ArticleState.Success(
                            otherArticles.shuffled().take(NUMBER_OF_OTHER_ARTICLES)
                        )
                    )

                }
                else -> {}
            }
        } catch (e: Exception) {
            _otherArticlesState.value = _otherArticlesState.value.copy(
                articleState = ArticleState.Error
            )
        }
    }
}
