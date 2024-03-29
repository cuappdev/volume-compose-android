package com.cornellappdev.android.volume.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.analytics.NavigationSource
import com.cornellappdev.android.volume.data.models.Article
import com.cornellappdev.android.volume.data.models.Magazine
import com.cornellappdev.android.volume.ui.components.general.BigFlyer
import com.cornellappdev.android.volume.ui.components.general.BigShimmeringFlyer
import com.cornellappdev.android.volume.ui.components.general.CreateArticleRow
import com.cornellappdev.android.volume.ui.components.general.CreateMagazineColumn
import com.cornellappdev.android.volume.ui.components.general.ErrorState
import com.cornellappdev.android.volume.ui.components.general.MainArticleComponent
import com.cornellappdev.android.volume.ui.components.general.NothingToShowMessage
import com.cornellappdev.android.volume.ui.components.general.ShimmeringArticle
import com.cornellappdev.android.volume.ui.components.general.ShimmeringMagazine
import com.cornellappdev.android.volume.ui.components.general.VolumeLogo
import com.cornellappdev.android.volume.ui.components.general.shimmerEffect
import com.cornellappdev.android.volume.ui.states.ArticleRetrievalState
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.states.FlyersRetrievalState
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import com.cornellappdev.android.volume.ui.viewmodels.TrendingViewModel

private const val TAG = "TrendingScreen"

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun TrendingScreen(
    trendingViewModel: TrendingViewModel = hiltViewModel(),
    onArticleClick: (Article, NavigationSource) -> Unit,
    onMagazineClick: (Magazine) -> Unit,
    onOrganizationNameClick: (slug: String) -> Unit,
) {
    val config = LocalConfiguration.current
    val screenWidthDp = config.screenWidthDp.dp
    val uiState = trendingViewModel.trendingUiState
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        item(span = { GridItemSpan(2) }) {
            Column(horizontalAlignment = Alignment.Start) {
                VolumeLogo(modifier = Modifier.padding(bottom = 24.dp))
            }
        }
        // Main featured article component:
        when (val articleUiState = uiState.mainFeaturedArticleRetrievalState) {
            ArticleRetrievalState.Loading -> {
                item(span = { GridItemSpan(2) }) {
                    Column {
                        Box(
                            modifier = Modifier
                                .requiredSize(screenWidthDp)
                                .shimmerEffect()
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .shimmerEffect()
                                    .weight(1F)
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.weight(2F))
                        }
                    }
                }
            }

            ArticleRetrievalState.Error -> {
                item(span = { GridItemSpan(2) }) {
                    ErrorState()
                }
            }

            is ArticleRetrievalState.Success -> {
                item(span = { GridItemSpan(2) }) {
                    MainArticleComponent(
                        article = articleUiState.article,
                        onArticleClick = onArticleClick
                    )
                }

            }
        }

        // Space
        item(span = { GridItemSpan(2) }) {
            Spacer(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
            )
        }

        // Article items
        when (val articleUiState = uiState.featuredArticlesRetrievalState) {
            ArticlesRetrievalState.Loading -> {
                items(4, span = { GridItemSpan(2) }) {
                    ShimmeringArticle()
                }
            }

            ArticlesRetrievalState.Error -> {}
            is ArticlesRetrievalState.Success -> {
                items(minOf(articleUiState.articles.size, 4), span = { GridItemSpan(2) }) { index ->
                    Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                        CreateArticleRow(article = articleUiState.articles[index]) {
                            onArticleClick(
                                articleUiState.articles[index],
                                NavigationSource.TRENDING_ARTICLES
                            )
                        }
                    }
                }
            }
        }


        // Flyers
        when (val flyersState = uiState.featuredFlyers) {
            FlyersRetrievalState.Loading -> {
                items(2, span = { GridItemSpan(2) }) {
                    Column {
                        Row {
                            Spacer(modifier = Modifier.width(16.dp))
                            BigShimmeringFlyer(
                                imgWidth = LocalConfiguration.current.screenWidthDp - 32,
                                imgHeight = LocalConfiguration.current.screenWidthDp - 32
                            )

                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }

            FlyersRetrievalState.Error -> {

            }

            is FlyersRetrievalState.Success -> {
                val flyers = flyersState.flyers
                items(minOf(2, flyers.size), span = { GridItemSpan(2) }) { index ->
                    Column {
                        Row {
                            Spacer(modifier = Modifier.width(16.dp))
                            BigFlyer(
                                imgSize = screenWidthDp - 32.dp,
                                flyer = flyers[index],
                                onOrganizationNameClick = onOrganizationNameClick
                            )
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }

                }
            }
        }

        // Magazines
        when (val magazineUiState = uiState.featuredMagazinesRetrievalState) {
            MagazinesRetrievalState.Loading -> {
                items(4) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ShimmeringMagazine()
                    }
                }
            }

            MagazinesRetrievalState.Error -> {
            }

            is MagazinesRetrievalState.Success -> {
                val magazines = magazineUiState.magazines

                items(minOf(magazines.size, 4)) { index ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CreateMagazineColumn(
                            magazine = magazineUiState.magazines[index],
                            onMagazineClick = onMagazineClick
                        )
                    }
                }
            }
        }

        // Space
        item(span = { GridItemSpan(2) }) {
            Spacer(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
            )
        }
        // Ending text for organizations
        item(span = { GridItemSpan(2) }) {
            NothingToShowMessage(
                title = "Are you an organization?",
                message = "If you want to see your organization's media on Volume, email us at cornellappdev@gmail.com",
                showImage = true,
                modifier = Modifier.padding(bottom = 141.dp)
            )
        }

    }

}