package com.cornellappdev.android.volume.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.cornellappdev.android.volume.analytics.NavigationSource
import com.cornellappdev.android.volume.data.models.Article
import com.cornellappdev.android.volume.ui.components.general.*
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif
import com.cornellappdev.android.volume.ui.viewmodels.HomeViewModel

@Composable
fun ArticlesScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onArticleClick: (Article, NavigationSource) -> Unit,
    showBottomBar: MutableState<Boolean>,
) {
    val homeUiState = homeViewModel.homeUiState
    var showPageBreak by remember { mutableStateOf(false) }

    Box {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp),
            ) {
                item {
                    VolumeHeaderText(
                        text = "The Big Read",
                        underline = com.cornellappdev.android.volume.R.drawable.ic_underline_big_read,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                }

                item {
                    when (val trendingArticlesState = homeUiState.trendingArticlesState) {
                        ArticlesRetrievalState.Loading -> {
                            VolumeLoading()
                        }
                        ArticlesRetrievalState.Error -> {
                            // TODO Prompt to try again, queryTrendingArticles manually (it's public). Could be that internet is down.
                        }
                        is ArticlesRetrievalState.Success -> {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                items(trendingArticlesState.articles) { article ->
                                    CreateBigReadRow(article) {
                                        onArticleClick(
                                            article,
                                            NavigationSource.TRENDING_ARTICLES
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                }

                item {
                    VolumeHeaderText(
                        text = "Following",
                        underline = com.cornellappdev.android.volume.R.drawable.ic_underline_following
                    )
                }

                when (val followingArticlesState = homeUiState.followingArticlesState) {
                    ArticlesRetrievalState.Loading -> {
                        item {
                            VolumeLoading()
                        }
                    }
                    ArticlesRetrievalState.Error -> {
                        // TODO Prompt to try again, queryFollowingArticles manually (it's public). Could be that internet is down.
                    }
                    is ArticlesRetrievalState.Success -> {
                        itemsIndexed(followingArticlesState.articles) { index, article ->
                            Box(
                                Modifier.padding(
                                    end = 12.dp,
                                    // Handles the padding between items
                                    top = if (index != 0) 20.dp else 10.dp
                                )
                            ) {
                                CreateArticleRow(
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
                                    painter = painterResource(id = com.cornellappdev.android.volume.R.drawable.ic_volume_bars_orange),
                                    contentDescription = null,
                                )
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    Text(
                                        text = "Nothing to see here!",
                                        fontFamily = notoserif,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center
                                    )
                                    Image(
                                        painter = painterResource(com.cornellappdev.android.volume.R.drawable.ic_underline_nothing_new),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(start = 5.dp)
                                            .offset(y = (-5).dp)
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
                                    painter = painterResource(id = com.cornellappdev.android.volume.R.drawable.ic_volume_bars_orange),
                                    contentDescription = null,
                                )
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    Text(
                                        text = "You're up to date!",
                                        fontFamily = notoserif,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Image(
                                        painter = painterResource(com.cornellappdev.android.volume.R.drawable.ic_underline_up_to_date),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(start = 1.dp)
                                            .offset(y = (-3).dp)
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
                    VolumeHeaderText(
                        text = "Other Articles",
                        underline = com.cornellappdev.android.volume.R.drawable.ic_underline_other_article
                    )
                }

                when (val otherArticlesState = homeUiState.otherArticlesState) {
                    ArticlesRetrievalState.Loading -> {
                        item {
                            VolumeLoading()
                        }
                    }
                    ArticlesRetrievalState.Error -> {
                        // TODO Prompt to try again, queryAllArticles manually (it's public). Could be that internet is down.
                    }
                    is ArticlesRetrievalState.Success -> {
                        itemsIndexed(otherArticlesState.articles) { index, article ->
                            Box(
                                Modifier.padding(
                                    end = 12.dp,
                                    // Handles the padding between items
                                    top = if (index != 0) 20.dp else 25.dp
                                )
                            ) {
                                CreateArticleRow(
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


        if (FirstTimeShown.firstTimeShown) {
            PermissionRequestDialog(
                showBottomBar = showBottomBar,
                notificationFlowStatus = homeViewModel.getNotificationPermissionFlowStatus(),
                updateNotificationFlowStatus = {
                    homeViewModel.updateNotificationPermissionFlowStatus(it)
                })
        }
    }
}

/**
 * Keeps track of when app navigates away from HomeScreen so PermissionRequestDialog
 * only occurs when the app FIRST is navigated to the HomeScreen.
 */
object FirstTimeShown {
    var firstTimeShown = true
}
