package com.cornellappdev.volume.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.theme.notoserif
import com.cornellappdev.volume.ui.viewmodels.HomeTabViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    homeTabViewModel: HomeTabViewModel = hiltViewModel(),
    onArticleClick: (Article, NavigationSource) -> Unit
) {
    val articlesState by homeTabViewModel.articlesState.collectAsState()

    // TODO add scrollability to content
    Scaffold(topBar = {
        // TODO fix positioning, little weird on my phone not sure if that's the case universally
        Image(
            painter = painterResource(R.drawable.volume_title),
            contentDescription = null,
            modifier = Modifier
                .scale(0.7f)
        )
    }, content = {
        Column(
            modifier = Modifier
                .padding(start = 12.dp, top = 16.dp),
        ) {
            Text(
                text = "The Big Read",
                fontFamily = notoserif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(22.dp))

            when (val trendingArticles = articlesState.trendingArticlesState) {
                HomeTabViewModel.ArticleState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = VolumeOrange)
                    }
                }
                HomeTabViewModel.ArticleState.Error -> {
                    // TODO Prompt to try again, queryTrendingArticles manually (it's public). Could be that internet is down.
                }
                is HomeTabViewModel.ArticleState.Success -> {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        items(trendingArticles.articles) { article ->
                            CreateBigReadRow(article) {
                                onArticleClick(article, NavigationSource.TRENDING_ARTICLES)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Following",
                fontFamily = notoserif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (val followingArticles = articlesState.followingArticlesState) {
                HomeTabViewModel.ArticleState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = VolumeOrange)
                    }
                }
                HomeTabViewModel.ArticleState.Error -> {
                    // TODO Prompt to try again, queryFollowingArticles manually (it's public). Could be that internet is down.
                }
                is HomeTabViewModel.ArticleState.Success -> {
                    if (followingArticles.articles.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_bar_chart_2),
                                contentDescription = null,
                            )
                            Text(text = "Nothing to see here!")
                            Text(text = "Follow some student publications that you are interested in")
                        }
                    } else {
                        val lazyListState = rememberLazyListState()

                        // TODO adjust height of LazyColumn
                        Box {
                            LazyColumn(
                                modifier = Modifier
                                    .height(300.dp)
                                    .padding(end = 12.dp),
                                state = lazyListState,
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                items(followingArticles.articles) { article ->
                                    CreateHorizontalArticleRow(article) {
                                        onArticleClick(article, NavigationSource.FOLLOWING_ARTICLES)
                                    }
                                }
                            }

                            // Gradient overlay to the top of the following articles LazyColumn
                            androidx.compose.animation.AnimatedVisibility(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .height(30.dp)
                                    .fillMaxWidth(),
                                enter = fadeIn(),
                                exit = fadeOut(),
                                // The gradient overlay is only visible when the user is scrolled past the start
                                // so the gradient isn't blocking the first article
                                visible = !lazyListState.isScrolledToTheStart()
                            ) {
                                Spacer(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.White,
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                            }

                            // Gradient overlay to the bottom of the following articles LazyColumn
                            androidx.compose.animation.AnimatedVisibility(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .height(30.dp)
                                    .fillMaxWidth(),
                                enter = fadeIn(),
                                exit = fadeOut(),
                                // The gradient overlay is only visible when the user hasn't scrolled to the end
                                // so the gradient isn't blocking the final article
                                visible = !lazyListState.isScrolledToTheEnd()
                            ) {
                                Spacer(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.White
                                                )
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Other Articles",
                fontFamily = notoserif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            when (val otherArticles = articlesState.otherArticlesState) {
                HomeTabViewModel.ArticleState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = VolumeOrange)
                    }
                }
                HomeTabViewModel.ArticleState.Error -> {
                    // TODO Prompt to try again, queryAllArticles manually (it's public). Could be that internet is down.
                }
                is HomeTabViewModel.ArticleState.Success -> {
                    // TODO adjust height of LazyColumn
                    LazyColumn(
                        modifier = Modifier
                            .height(200.dp)
                            .padding(end = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        items(otherArticles.articles) { article ->
                            CreateHorizontalArticleRow(article) {
                                onArticleClick(article, NavigationSource.OTHER_ARTICLES)
                            }
                        }
                    }
                }
            }
        }
    })
}
