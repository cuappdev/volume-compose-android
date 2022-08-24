package com.cornellappdev.volume.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cornellappdev.volume.R
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.ui.components.general.CreateHorizontalArticleRow
import com.cornellappdev.volume.ui.theme.GrayOne
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.theme.lato
import com.cornellappdev.volume.ui.theme.notoserif
import com.cornellappdev.volume.ui.viewmodels.HomeTabViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(homeTabViewModel: HomeTabViewModel) {
    val trendingArticleUiState = homeTabViewModel.trendingArticlesState.collectAsState().value
    val otherArticleState = homeTabViewModel.allArticlesState.collectAsState().value

    Scaffold(topBar = {
        Image(
            painter = painterResource(R.drawable.volume_title),
            contentDescription = null,
            alignment = Alignment.CenterStart,
            modifier = Modifier
                .scale(0.7f)
                .padding(start = (10).dp)
        )
    }, content = {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 12.dp, top = 16.dp),
        ) {
            Text(
                text = "The Big Read",
                fontFamily = notoserif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(22.dp))

            when (trendingArticleUiState.articleState) {
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
                        items(trendingArticleUiState.articleState.article) { article ->
                            TrendingArticleItem(article = article)
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_bar_chart_2),
                    contentDescription = null,
                )
                Text(text = "Nothing to see here!")
                Text(text = "Follow some student publications that you are interested in")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Other Articles",
                fontFamily = notoserif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )


            when (otherArticleState.articleState) {
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
                    LazyColumn(
                        modifier = Modifier
                            .height(600.dp)
                            .padding(end = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        items(otherArticleState.articleState.article) { article ->
                            CreateHorizontalArticleRow(article)
                        }
                    }
                }
            }
        }
    })
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TrendingArticleItem(article: Article) {
    val timeSincePublished = article.getTimeSinceArticlePublished()
    val shoutouts = article.shoutouts.toInt()

    Column(modifier = Modifier.wrapContentSize()) {
        AsyncImage(
            model = article.imageURL, modifier = Modifier
                .height(180.dp)
                .width(180.dp), contentDescription = null, contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 2.dp),
            text = article.publication.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = notoserif,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
        Text(
            text = article.title,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            fontFamily = lato,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(13.dp))
        Text(
            text = "$timeSincePublished · ${
                pluralStringResource(
                    R.plurals.shoutout_count,
                    shoutouts,
                    shoutouts
                )
            }",
            fontFamily = lato,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            color = GrayOne
        )
    }
}
