package com.cornellappdev.volume.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.cornellappdev.volume.R
import com.cornellappdev.volume.analytics.NavigationSource
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.ui.components.general.CreateHorizontalArticleRow
import com.cornellappdev.volume.ui.components.general.createIndividualPublicationHeading
import com.cornellappdev.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.volume.ui.states.PublicationRetrievalState
import com.cornellappdev.volume.ui.theme.GrayFour
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.viewmodels.IndividualPublicationViewModel
import com.google.firebase.inappmessaging.display.internal.Logging.logd

//"61980a202fef10d6b7f20747"
@Composable
fun IndividualPublicationScreen(individualPublicationViewModel: IndividualPublicationViewModel = hiltViewModel(), onArticleClick: (Article, NavigationSource) -> Unit) {

    val publicationUiState = individualPublicationViewModel.publicationUiState

    LazyColumn {
        item{
            when (val publicationState = publicationUiState.publicationState) {
                PublicationRetrievalState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = VolumeOrange)
                    }
                }
                PublicationRetrievalState.Error -> {

                }
                is PublicationRetrievalState.Success -> {
                     createIndividualPublicationHeading(publication = publicationState.publication){ isFollowing ->
                         if (isFollowing){
                             individualPublicationViewModel.followPublication()
                         }
                         else {
                             individualPublicationViewModel.unfollowPublication()
                         }
                     }
                }
            }
        }
        item{
            Divider(modifier=Modifier.padding(top=20.dp, start=100.dp, end=100.dp), color = GrayFour, thickness = 1.dp)
        }
        item{
            when (val articlesByPublicationState = publicationUiState.articlesByPublicationState) {
                ArticlesRetrievalState.Loading -> {
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(start = 12.dp, top = 12.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = VolumeOrange)
                    }
                }
                ArticlesRetrievalState.Error -> {

                }
                is ArticlesRetrievalState.Success -> {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(top= 20.dp, start=12.dp, end = 12.dp)
                    ) {
                        articlesByPublicationState.articles.forEach { article ->
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
}


