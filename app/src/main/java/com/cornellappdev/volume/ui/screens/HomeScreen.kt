package com.cornellappdev.volume.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.volume.R
import com.cornellappdev.volume.analytics.NavigationSource
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.ui.components.general.CreateBigReadRow
import com.cornellappdev.volume.ui.components.general.CreateHorizontalArticleRow
import com.cornellappdev.volume.ui.components.onboarding.isScrolledToTheEnd
import com.cornellappdev.volume.ui.components.onboarding.isScrolledToTheStart
import com.cornellappdev.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.volume.ui.theme.VolumeOffWhite
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.theme.lato
import com.cornellappdev.volume.ui.theme.notoserif
import com.cornellappdev.volume.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onArticleClick: (Article, NavigationSource) -> Unit
) {
    val homeUiState = homeViewModel.homeUiState
    var showPageBreak by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        // TODO fix positioning, little weird on my phone not sure if that's the case universally
        Image(
            painter = painterResource(R.drawable.volume_title),
            contentDescription = null,
            modifier = Modifier
                .scale(0.8f)
        )
    }, content = { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, top = innerPadding.calculateTopPadding()),
        ) {
            item {
                Text(
                    text = "The Big Read",
                    fontFamily = notoserif,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(22.dp))
            }

            item {
                when (val trendingArticlesState = homeUiState.trendingArticlesState) {
                    ArticlesRetrievalState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = VolumeOrange)
                        }
                    }
                    ArticlesRetrievalState.Error -> {
                        // TODO Prompt to try again, queryTrendingArticles manually (it's public). Could be that internet is down.
                    }
                    is ArticlesRetrievalState.Success -> {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            items(trendingArticlesState.articles) { article ->
                                CreateBigReadRow(article) {
                                    onArticleClick(article, NavigationSource.TRENDING_ARTICLES)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }

            item {
                Text(
                    text = "Following",
                    fontFamily = notoserif,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            item {
                when (val followingArticlesState = homeUiState.followingArticlesState) {
                    ArticlesRetrievalState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = VolumeOrange,
                                modifier = Modifier.padding(vertical = 50.dp)
                            )
                        }
                    }
                    ArticlesRetrievalState.Error -> {
                        // TODO Prompt to try again, queryFollowingArticles manually (it's public). Could be that internet is down.
                    }
                    is ArticlesRetrievalState.Success -> {
                        Box(modifier = Modifier.padding(top = 10.dp)) {
                            Column(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .padding(end = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                followingArticlesState.articles.forEach { article ->
                                    CreateHorizontalArticleRow(
                                        article
                                    ) {
                                        onArticleClick(
                                            article,
                                            NavigationSource.FOLLOWING_ARTICLES
                                        )
                                    }
                                }
                            }
                            showPageBreak = true
                        }
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = showPageBreak,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (homeUiState.isFollowingEmpty) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 40.dp, horizontal = 16.dp)
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_volume_bars_orange_large),
                                contentDescription = null,
                            )
                            Text(
                                text = "Nothing to see here!",
                                fontFamily = notoserif,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Follow some student publications that you are interested in.",
                                fontFamily = lato,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 70.dp, horizontal = 16.dp)
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_volume_bars_orange),
                                contentDescription = null,
                            )
                            Text(
                                text = "You're up to date!",
                                fontFamily = notoserif,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "You've seen all new articles from the publications you are following.",
                                fontFamily = lato,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Other Articles",
                    fontFamily = notoserif,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            item {
                when (val otherArticlesState = homeUiState.otherArticlesState) {
                    ArticlesRetrievalState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = VolumeOrange)
                        }
                    }
                    ArticlesRetrievalState.Error -> {
                        // TODO Prompt to try again, queryAllArticles manually (it's public). Could be that internet is down.
                    }
                    is ArticlesRetrievalState.Success -> {
                        Column(
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(end = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            otherArticlesState.articles.forEach { article ->
                                CreateHorizontalArticleRow(
                                    article
                                ) {
                                    onArticleClick(
                                        article,
                                        NavigationSource.OTHER_ARTICLES
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    })
}
