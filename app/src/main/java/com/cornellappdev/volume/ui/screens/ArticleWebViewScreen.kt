package com.cornellappdev.volume.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.cornellappdev.volume.R
import com.cornellappdev.volume.analytics.EventType
import com.cornellappdev.volume.analytics.NavigationSource
import com.cornellappdev.volume.analytics.VolumeEvent
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.navigation.Routes
import com.cornellappdev.volume.ui.states.ArticleRetrievalState
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.theme.lato
import com.cornellappdev.volume.ui.theme.notoserif
import com.cornellappdev.volume.ui.viewmodels.ArticleWebViewModel
import com.cornellappdev.volume.util.BookmarkStatus
import com.cornellappdev.volume.util.FinalBookmarkStatus
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleWebViewScreen(
    articleWebViewModel: ArticleWebViewModel = hiltViewModel(),
    navigationSourceName: String,
    onArticleClose: (BookmarkStatus?) -> Unit,
    seeMoreClicked: (Article) -> Unit
) {
    val context = LocalContext.current
    val webViewUiState = articleWebViewModel.webViewUiState

    when (val articleState = webViewUiState.articleState) {
        ArticleRetrievalState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = VolumeOrange)
            }
        }
        ArticleRetrievalState.Error -> {
            // TODO
        }
        is ArticleRetrievalState.Success -> {
            val state = rememberWebViewState(articleState.article.articleURL)
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                fontSize = 15.sp,
                                fontFamily = notoserif,
                                fontWeight = FontWeight.Medium,
                                text = articleState.article.publication.name,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                color = Color(0xFF979797),
                                fontSize = 10.sp,
                                fontFamily = lato,
                                fontWeight = FontWeight.Normal,
                                text = "Reading in Volume"
                            )
                        }
                    }, modifier = Modifier.background(Color(0xFFF9F9F9)))
                },
                bottomBar = {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFF9F9F9))
                            .height(55.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .background(Color.Transparent)
                                .clickable
                                { seeMoreClicked(articleState.article) },
                        ) {
                            AsyncImage(
                                model = articleState.article.publication.profileImageURL,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentDescription = null
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                modifier = Modifier.align(CenterVertically),
                                text = "See more",
                                fontFamily = lato,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                        }

                        Spacer(Modifier.weight(1f, true))

                        IconButton(onClick = { articleWebViewModel.bookmarkArticle() }) {
                            Crossfade(targetState = webViewUiState.isBookmarked) { isBookmarked ->
                                if (isBookmarked) {
                                    Icon(
                                        Icons.Filled.Bookmark,
                                        contentDescription = "Bookmark article",
                                        tint = VolumeOrange
                                    )
                                } else {
                                    Icon(
                                        Icons.Outlined.BookmarkBorder,
                                        contentDescription = "Bookmark article",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                        IconButton(onClick = {
                            VolumeEvent.logEvent(
                                EventType.ARTICLE,
                                VolumeEvent.SHARE_ARTICLE,
                                id = articleState.article.id
                            )
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Look at this article I found on Volume: volume://${Routes.OPEN_ARTICLE.route}/${articleState.article.id}"
                                )
                            }

                            context.startActivity(Intent.createChooser(intent, "Share To:"))
                        }) {
                            Icon(Icons.Filled.Share, contentDescription = "Localized description")
                        }

                        Row(
                            modifier = Modifier
                                .padding(ButtonDefaults.ContentPadding)
                                .clickable
                                { articleWebViewModel.shoutoutArticle() },
                        ) {
                            Crossfade(targetState = webViewUiState.isMaxedShoutout) { isMaxShoutout ->
                                if (isMaxShoutout) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_shoutout_filled),
                                        contentDescription = "Shoutout article"
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_shoutout),
                                        contentDescription = "Shoutout article"
                                    )
                                }
                            }
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(
                                modifier = Modifier.align(CenterVertically),
                                text = webViewUiState.shoutoutCount.toString(),
                                fontFamily = lato,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                        }
                    }
                },
                content = { innerPadding ->
                    WebView(
                        modifier = Modifier.padding(innerPadding),
                        state = state,
                        onCreated = { webView ->
                            webView.settings.javaScriptEnabled = true
                            VolumeEvent.logEvent(
                                EventType.ARTICLE,
                                VolumeEvent.OPEN_ARTICLE,
                                NavigationSource.valueOf(navigationSourceName),
                                articleState.article.id
                            )
                        }
                    )
                })
        }
    }

    BackHandler(enabled = true) {
        val bookmarkStatus = if (webViewUiState.articleState is ArticleRetrievalState.Success) {
            val article = webViewUiState.articleState.article
            if (webViewUiState.initialBookmarkState && !webViewUiState.isBookmarked) {
                BookmarkStatus(
                    FinalBookmarkStatus.BOOKMARKED_TO_UNBOOKMARKED,
                    article.id
                )
            } else if (!webViewUiState.initialBookmarkState && webViewUiState.isBookmarked) {
                BookmarkStatus(
                    FinalBookmarkStatus.UNBOOKMARKED_TO_BOOKMARKED,
                    article.id
                )
            } else {
                BookmarkStatus(FinalBookmarkStatus.UNCHANGED, article.id)
            }
        } else {
            null
        }

        onArticleClose(
            bookmarkStatus
        )
    }
}
