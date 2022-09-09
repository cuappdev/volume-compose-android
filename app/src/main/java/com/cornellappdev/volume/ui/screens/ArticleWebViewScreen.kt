package com.cornellappdev.volume.ui.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.volume.analytics.EventType
import com.cornellappdev.volume.analytics.NavigationSource
import com.cornellappdev.volume.analytics.VolumeEvent
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.theme.lato
import com.cornellappdev.volume.ui.theme.notoserif
import com.cornellappdev.volume.ui.viewmodels.ArticleWebViewModel
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

data class BookmarkStatus(
    val status: FinalBookmarkStatus,
    val articleId: String
)

enum class FinalBookmarkStatus {
    BOOKMARKED_TO_UNBOOKMARKED, UNBOOKMARKED_TO_BOOKMARKED, UNCHANGED
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ArticleWebViewScreen(
    articleWebViewModel: ArticleWebViewModel = hiltViewModel(),
    navigationSourceName: String?,
    onArticleClose: (Article, BookmarkStatus) -> Unit,
    seeMoreClicked: (Article) -> Unit
) {
    val webState by articleWebViewModel.webState.collectAsState()
    val initialBookmarkState = remember { mutableStateOf(false) }
    val finalBookmarkState = remember { mutableStateOf(false) }

    when (val articleState = webState.articleState) {
        ArticleWebViewModel.ArticleState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = VolumeOrange)
            }
        }
        ArticleWebViewModel.ArticleState.Error -> {
        }
        is ArticleWebViewModel.ArticleState.Success -> {
            val state = rememberWebViewState(articleState.article.articleURL)
            val shoutoutCount = remember { articleState.article.shoutouts }
            initialBookmarkState.value = webState.isBookmarked
            finalBookmarkState.value = webState.isBookmarked

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                fontSize = 15.sp,
                                fontFamily = notoserif,
                                fontWeight = FontWeight.Medium,
                                text = articleState.article.publication.name
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
                // TODO finish
                bottomBar = {
                    Column(
                        modifier = Modifier
                            .height(50.dp)
                            .padding(end = 20.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            // Publication logo see more button using seeMoreClicked
                        }

                        Column {
//                            VolumeEvent.logEvent(EventType.ARTICLE, VolumeEvent.SHARE_ARTICLE, id = article.id)

//                            val intent = Intent()
//                            intent.action = Intent.ACTION_SEND
//                            intent.putExtra(
//                                Intent.EXTRA_TEXT,
//                                "Look at this article I found on Volume: ${article.articleURL}"
//                            )
//                            intent.type = "text/plain"
//                            LocalContext.current.startActivity(Intent.createChooser(intent, "Share To:"))

                            // Bookmark/Unbookmark and shoutout maintained by ViewModel
                            // Bookmark Icon, Share Icon, Shoutout
                        }
                    }
                },
                content = {
                    WebView(
                        state = state,
                        onCreated = { webView ->
                            webView.settings.javaScriptEnabled = true
                            navigationSourceName?.let {
                                VolumeEvent.logEvent(
                                    EventType.ARTICLE,
                                    VolumeEvent.OPEN_ARTICLE,
                                    NavigationSource.valueOf(it),
                                    articleState.article.id
                                )
                            }
                        }
                    )
                })

            BackHandler(enabled = webState.articleState is ArticleWebViewModel.ArticleState.Success) {
                val bookmarkStatus = if (initialBookmarkState.value && finalBookmarkState.value) {
                    BookmarkStatus(FinalBookmarkStatus.UNCHANGED, articleState.article.id)
                } else if (initialBookmarkState.value && !finalBookmarkState.value) {
                    BookmarkStatus(
                        FinalBookmarkStatus.BOOKMARKED_TO_UNBOOKMARKED,
                        articleState.article.id
                    )
                } else {
                    BookmarkStatus(
                        FinalBookmarkStatus.BOOKMARKED_TO_UNBOOKMARKED,
                        articleState.article.id
                    )
                }
                onArticleClose(
                    articleState.article,
                    bookmarkStatus
                )
            }
        }
    }
}
