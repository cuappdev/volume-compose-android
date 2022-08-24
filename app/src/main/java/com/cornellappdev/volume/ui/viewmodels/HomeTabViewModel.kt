package com.cornellappdev.volume.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.repositories.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeTabViewModel(private val articleRepository: ArticleRepository) : ViewModel() {

    data class TrendingArticlesUiState(
        val articleState: ArticleState
    )

    data class AllArticlesUiState(
        val articleState: ArticleState
    )

    sealed interface ArticleState {
        data class Success(val article: List<Article>) : ArticleState
        object Error : ArticleState
        object Loading : ArticleState
    }

    // Backing property to avoid state updates from other classes
    private val _allArticlesState = MutableStateFlow(AllArticlesUiState(ArticleState.Loading))

    // The UI collects from this StateFlow to get its state updates
    val allArticlesState: StateFlow<AllArticlesUiState> = _allArticlesState.asStateFlow()

    // Backing property to avoid state updates from other classes
    private val _trendingArticlesState =
        MutableStateFlow(TrendingArticlesUiState(ArticleState.Loading))

    // The UI collects from this StateFlow to get its state updates
    val trendingArticlesState: StateFlow<TrendingArticlesUiState> =
        _trendingArticlesState.asStateFlow()

    init {
        queryTrendingArticles()
        queryAllArticles()
    }

    fun queryTrendingArticles() = viewModelScope.launch {
        try {
            _trendingArticlesState.value = _trendingArticlesState.value.copy(
                articleState = ArticleState.Success(articleRepository.fetchTrendingArticles())
            )
        } catch (e: Exception) {
            _trendingArticlesState.value = _trendingArticlesState.value.copy(
                articleState = ArticleState.Error
            )
        }
    }

    fun queryAllArticles() = viewModelScope.launch {
        try {
            _allArticlesState.value = _allArticlesState.value.copy(
                articleState = ArticleState.Success(articleRepository.fetchAllArticles())
            )
        } catch (e: Exception) {
            _allArticlesState.value = _allArticlesState.value.copy(
                articleState = ArticleState.Error
            )
        }
    }
}
