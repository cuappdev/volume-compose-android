package com.cornellappdev.android.volume.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornellappdev.android.volume.data.repositories.ArticleRepository
import com.cornellappdev.android.volume.data.repositories.FlyerRepository
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
    private val flyerRepository: FlyerRepository,
) : ViewModel() {
    init {
        getMainFeaturedArticle()
    }

    data class TrendingUiState(
        val mainFeaturedArticleRetrievalState: ArticleRetrievalState = ArticleRetrievalState.Loading,
        val featuredMagazinesRetrievalState: MagazinesRetrievalState = MagazinesRetrievalState.Loading,
        val featuredArticlesRetrievalState: ArticlesRetrievalState = ArticlesRetrievalState.Loading,
        val featuredFlyers: FlyersRetrievalState = FlyersRetrievalState.Loading,
    )

    var trendingUiState by mutableStateOf(TrendingUiState())
        private set


    private fun getMainFeaturedArticle() = viewModelScope.launch {
        try {
            val trendingArticles = articleRepository.fetchArticlesByPublicationSlug("guac")
            val myRandom = Random(System.currentTimeMillis())
            val featuredArticle = trendingArticles[myRandom.nextInt(trendingArticles.size)]
            trendingUiState = trendingUiState.copy(
                mainFeaturedArticleRetrievalState = ArticleRetrievalState.Success(featuredArticle)
            )
            getFeaturedArticles()
        } catch (e: java.lang.Exception) {
            trendingUiState = trendingUiState.copy(
                mainFeaturedArticleRetrievalState = ArticleRetrievalState.Error
            )
        }
    }

    private fun getFeaturedArticles() = viewModelScope.launch {
        try {
            val potentialArticles = articleRepository.fetchTrendingArticles()
            val featuredArticles = potentialArticles.filter {/*
                 TODO add filter before release (so we get real images)
                 it.publication.slug != "nooz" && it.publication.slug != "review" */
                true
            }.shuffled(Random(System.currentTimeMillis()))
            trendingUiState = trendingUiState.copy(
                featuredArticlesRetrievalState = ArticlesRetrievalState.Success(featuredArticles)
            )
            getFeaturedFlyers()
        } catch (e: java.lang.Exception) {
            trendingUiState = trendingUiState.copy(
                featuredArticlesRetrievalState = ArticlesRetrievalState.Error
            )
        }
    }

    private fun getFeaturedFlyers() = viewModelScope.launch {
        try {
            trendingUiState = trendingUiState.copy(
                featuredFlyers = FlyersRetrievalState.Success(flyers = flyerRepository.fetchTrendingFlyers())
            )
            getFeaturedMagazines()
        } catch (e: Exception) {
            trendingUiState = trendingUiState.copy(
                featuredFlyers = FlyersRetrievalState.Error
            )
        }
    }

    private fun getFeaturedMagazines() = viewModelScope.launch {
        trendingUiState = try {
            val potentialMags = magazineRepository.fetchAllMagazines(limit = 10.0)
            val featuredMags = potentialMags.filter {
                it.publication.slug != "nooz" && it.publication.slug != "review"
            }
            trendingUiState.copy(
                featuredMagazinesRetrievalState = MagazinesRetrievalState.Success(featuredMags)
            )
        } catch (e: java.lang.Exception) {
            trendingUiState.copy(
                featuredMagazinesRetrievalState = MagazinesRetrievalState.Error
            )
        }
    }
}
