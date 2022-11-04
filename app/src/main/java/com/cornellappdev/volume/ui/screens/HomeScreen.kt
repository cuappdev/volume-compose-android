package com.cornellappdev.volume.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
import com.cornellappdev.volume.ui.states.ArticlesRetrievalState
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
                Box {
                    Text(
                        text = "The Big Read",
                        fontFamily = notoserif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_underline_big_read),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 2.dp, top = 40.dp)
                            .scale(1.05F)
                    )
                }
                Spacer(modifier = Modifier.height(25.dp))
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

                Spacer(modifier = Modifier.height(25.dp))
            }

            item {
                Box {
                    Text(
                        text = "Following",
                        fontFamily = notoserif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_underline_following),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 0.dp, top = 25.dp)
                            .scale(1.05F)
                    )
                }
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
                                painter = painterResource(id = R.drawable.ic_volume_bars_orange),
                                contentDescription = null,
                            )
                            Box(modifier = Modifier.padding(top = 10.dp)) {
                                Text(
                                    text = "Nothing to see here!",
                                    fontFamily = notoserif,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                                Image(
                                    painter = painterResource(R.drawable.ic_underline_nothing_new),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(start = 5.dp, top = 20.dp)
                                        .scale(1.05F)
                                )
                            }

                            Text(
                                text = "Follow some student publications that you are interested in.",
                                fontFamily = lato,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 10.dp)
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
                            Box(modifier = Modifier.padding(top = 10.dp)) {
                                Text(
                                    text = "You're up to date!",
                                    fontFamily = notoserif,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Image(
                                    painter = painterResource(R.drawable.ic_underline_up_to_date),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(start = 1.dp, top = 16.dp)
                                        .scale(1.05F)
                                )
                            }
                            Text(
                                text = "You've seen all new articles from the publications you are following.",
                                fontFamily = lato,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }
                }
            }

            item {
                Box {
                    Text(
                        text = "Other Articles",
                        fontFamily = notoserif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_underline_other_article),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 2.dp, top = 25.dp)
                            .scale(1.05F)
                    )
                }
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
                                .padding(end = 12.dp, top = 25.dp),
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
