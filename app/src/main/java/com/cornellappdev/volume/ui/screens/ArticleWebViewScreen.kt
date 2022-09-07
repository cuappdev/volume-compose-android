package com.cornellappdev.volume.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.theme.lato
import com.cornellappdev.volume.ui.theme.notoserif
import com.cornellappdev.volume.ui.viewmodels.ArticleWebViewModel
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ArticleWebViewScreen(
    articleWebViewModel: ArticleWebViewModel,
    seeMoreClicked: (Article) -> Unit
) {
    val webState by articleWebViewModel.webState.collectAsState()

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
                            // Bookmark Icon, Share Icon, Shoutout
                        }
                    }
                },
                content = {
                    WebView(
                        state = state,
                        onCreated = { webView ->
                            webView.settings.javaScriptEnabled = true
                        }
                    )
                })
        }
    }
}
