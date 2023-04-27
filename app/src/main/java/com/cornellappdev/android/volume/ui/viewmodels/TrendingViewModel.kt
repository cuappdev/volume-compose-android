package com.cornellappdev.android.volume.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.repositories.ArticleRepository
import com.cornellappdev.android.volume.data.repositories.MagazineRepository
import com.cornellappdev.android.volume.ui.states.ArticleRetrievalState
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.states.FlyersRetrievalState
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject

private const val TAG = "TrendingViewModel"
@HiltViewModel
class TrendingViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val magazineRepository: MagazineRepository,
) : ViewModel() {
    init {
        getMainFeaturedArticle()
    }

    data class TrendingUiState(
        val mainFeaturedArticleRetrievalState: ArticleRetrievalState = ArticleRetrievalState.Loading,
        val featuredMagazinesRetrievalState: MagazinesRetrievalState =  MagazinesRetrievalState.Loading,
        val featuredArticlesRetrievalState: ArticlesRetrievalState = ArticlesRetrievalState.Loading,
        val featuredFlyers: FlyersRetrievalState = FlyersRetrievalState.Loading
    )

    var trendingUiState by mutableStateOf(TrendingUiState())
        private set


    private fun getMainFeaturedArticle() = viewModelScope.launch {
        try {
            val trendingArticles = articleRepository.fetchArticlesByPublicationSlug("guac")
            val myRandom = Random(System.currentTimeMillis())
            Log.d(TAG, "getMainFeaturedArticle: trending articles: $trendingArticles")
            val featuredArticle = trendingArticles[myRandom.nextInt(trendingArticles.size)]
            trendingUiState = trendingUiState.copy(
              mainFeaturedArticleRetrievalState = ArticleRetrievalState.Success(featuredArticle)
            )
            getFeaturedMagazines()
        } catch (e: java.lang.Exception) {
            trendingUiState = trendingUiState.copy(
                mainFeaturedArticleRetrievalState = ArticleRetrievalState.Error
            )
        }
    }
    private fun getFeaturedMagazines() = viewModelScope.launch {
        try {
            val potentialMags = magazineRepository.fetchAllMagazines(limit = 10.0)
            val featuredMags = potentialMags.filter {
                it.publication.slug != "nooz"  && it.publication.slug != "review"
            }
            trendingUiState = trendingUiState.copy(
                featuredMagazinesRetrievalState = MagazinesRetrievalState.Success(featuredMags)
            )
            getFeaturedArticles()
        } catch (e: java.lang.Exception) {
            trendingUiState = trendingUiState.copy(
                featuredMagazinesRetrievalState = MagazinesRetrievalState.Error
            )
        }
    }
    private fun getFeaturedArticles() = viewModelScope.launch {
        trendingUiState = try {
            val potentialArticles = articleRepository.fetchTrendingArticles()
            val featuredArticles =  potentialArticles.filter {/*
                 TODO add filter before release (so we get real images)
                 it.publication.slug != "nooz" && it.publication.slug != "review" */
            true }
            trendingUiState.copy(
                featuredArticlesRetrievalState = ArticlesRetrievalState.Success(featuredArticles)
            )
        } catch (e: java.lang.Exception) {
            trendingUiState.copy(
                featuredArticlesRetrievalState = ArticlesRetrievalState.Error
            )
        }
    }

    private fun getFeaturedFlyers() = viewModelScope.launch {

    }
}
