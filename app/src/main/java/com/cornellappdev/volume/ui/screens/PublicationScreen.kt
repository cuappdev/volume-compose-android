package com.cornellappdev.volume.ui.screens

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apollographql.apollo3.api.BooleanExpression
import com.cornellappdev.volume.R
import com.cornellappdev.volume.analytics.EventType
import com.cornellappdev.volume.analytics.NavigationSource
import com.cornellappdev.volume.analytics.VolumeEvent
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.ui.components.general.CreateFollowPublicationRow
import com.cornellappdev.volume.ui.components.general.CreateHorizontalPublicationRow
import com.cornellappdev.volume.ui.components.onboarding.isScrolledToTheEnd
import com.cornellappdev.volume.ui.states.PublicationsRetrievalState
import com.cornellappdev.volume.ui.theme.VolumeOffWhite
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.theme.notoserif
import com.cornellappdev.volume.ui.viewmodels.PublicationsViewModel

@Composable
fun PublicationsScreen(
    publicationsViewModel: PublicationsViewModel = hiltViewModel(),
    //followButtonClicked: (Publication, Boolean) -> Unit,
    onPublicationClick: (Publication) -> Unit
) {
    val publicationsUiState = publicationsViewModel.publicationsUiState
    val lazyListState = rememberLazyListState()
    val buttonClicked = rememberSaveable { mutableStateOf(false) }

       Scaffold (topBar = {
           // TODO fix positioning, little weird on my phone not sure if that's the case universally
           Text(
               modifier = Modifier.padding(start = 20.dp, top = 20.dp),
               text = "Publications",
               fontFamily = notoserif,
               fontWeight = FontWeight.Medium,
               fontSize = 28.sp,
               textAlign = TextAlign.Left
           )
       },content = { innerPadding ->
           LazyColumn(
               modifier =
               Modifier
                   .fillMaxSize()
                   .padding(start = 12.dp, top = innerPadding.calculateTopPadding()),
           ) {
               item {
                   Text(
                       modifier = Modifier.padding(top = 30.dp),
                       text = "Following",
                       fontFamily = notoserif,
                       fontSize = 20.sp,
                       fontWeight = FontWeight.Medium
                   )

                   Spacer(modifier = Modifier.height(10.dp))
               }
               item {
                   when (val followingPublicationsState = publicationsUiState.followedPublicationsState) {
                       PublicationsRetrievalState.Loading -> {
                           Column(
                               modifier = Modifier.fillMaxWidth(),
                               horizontalAlignment = Alignment.CenterHorizontally
                           ) {
                               CircularProgressIndicator(color = VolumeOrange)
                           }
                       }
                       PublicationsRetrievalState.Error -> {
                           // TODO Prompt to try again, queryFollowingPublications manually (it's public). Could be that internet is down.
                       }
                       is PublicationsRetrievalState.Success -> {
                           LazyRow(horizontalArrangement = Arrangement.spacedBy(24.dp)) {

                               items(followingPublicationsState.publications) { publication ->
                                   CreateFollowPublicationRow(publication) {
                                       onPublicationClick(publication)
                                   }
                               }

                           }
                       }
                   }
               }
               item {
                   Text(
                       modifier = Modifier.padding(top = 30.dp),
                       text = "More Publications",
                       fontFamily = notoserif,
                       fontSize = 20.sp,
                       fontWeight = FontWeight.Medium
                   )

                   Spacer(modifier = Modifier.height(22.dp))
               }
               item {
                   when (val unfollowingPublicationsState = publicationsUiState.unfollowedPublicationsState) {
                       PublicationsRetrievalState.Loading -> {
                           Column(
                               modifier = Modifier.fillMaxWidth(),
                               horizontalAlignment = Alignment.CenterHorizontally
                           ) {
                               CircularProgressIndicator(color = VolumeOrange)
                           }
                       }
                       PublicationsRetrievalState.Error -> {
                           // TODO Prompt to try again, queryFollowingPublications manually (it's public). Could be that internet is down.
                       }
                       is PublicationsRetrievalState.Success -> {
                           LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier
                               .height(500.dp)
                               .padding(end = 12.dp),
                               state = lazyListState) {

                               items(
                                   items = unfollowingPublicationsState.publications,
                                   key = { publication ->
                                       publication.id
                                   }) { publication ->
                                   CreateHorizontalPublicationRow(publication = publication) { publicationFromCallback, isFollowing ->
                                       if (isFollowing) {
                                           publicationsViewModel.followPublication(
                                               publicationFromCallback.id
                                           )
                                       } else {
                                           publicationsViewModel.unfollowPublication(
                                               publicationFromCallback.id
                                           )

                                       }

                                   }

                               }
                           }
                       }

                   }
               }

           }
       })
    }