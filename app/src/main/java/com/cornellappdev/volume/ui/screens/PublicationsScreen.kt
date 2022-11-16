package com.cornellappdev.volume.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.ui.components.general.CreatePublicationColumn
import com.cornellappdev.volume.ui.components.general.CreatePublicationRow
import com.cornellappdev.volume.ui.states.PublicationsRetrievalState
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.theme.notoserif
import com.cornellappdev.volume.ui.viewmodels.PublicationsViewModel

@Composable
fun PublicationsScreen(
    publicationsViewModel: PublicationsViewModel = hiltViewModel(),
    onPublicationClick: (Publication) -> Unit,
) {
    val publicationsUiState = publicationsViewModel.publicationsUiState

    Scaffold(topBar = {
        Row {
            Text(
                modifier = Modifier.padding(start = 12.dp, top = 20.dp),
                text = "Publications",
                fontFamily = notoserif,
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                textAlign = TextAlign.Left
            )
            Image(
                painter = painterResource(R.drawable.ic_period),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 3.dp, top = 43.5.dp)
                    .scale(1.05F)
            )
        }

    }, content = { innerPadding ->
        LazyColumn(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
        ) {
            item {
                Column(
                    modifier = Modifier.padding(start = 12.dp, top = 25.dp)
                ) {
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
                            .offset(y = (-5).dp)
                            .scale(1.05F)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
            item {
                when (val followingPublicationsState =
                    publicationsUiState.followedPublicationsState) {
                    PublicationsRetrievalState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = VolumeOrange)
                        }
                    }
                    PublicationsRetrievalState.Error -> {
                        // TODO Prompt to try again, queryFollowingPublications manually (it's public). Could be that internet is down.
                    }
                    is PublicationsRetrievalState.Success -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier.padding(start = 12.dp)
                        ) {

                            items(followingPublicationsState.publications) { publication ->
                                CreatePublicationColumn(publication) {
                                    onPublicationClick(publication)
                                }
                            }
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.padding(start = 12.dp, top = 30.dp)
                ) {
                    Text(
                        text = "More Publications",
                        fontFamily = notoserif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_underline_more_publications),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .offset(y = (-5).dp)
                            .scale(1.06F)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))
            }
            item {
                when (val morePublicationsState =
                    publicationsUiState.morePublicationsState) {
                    PublicationsRetrievalState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = VolumeOrange)
                        }
                    }
                    PublicationsRetrievalState.Error -> {
                        // TODO Prompt to try again, queryFollowingPublications manually (it's public). Could be that internet is down.
                    }
                    is PublicationsRetrievalState.Success -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(start = 12.dp, end = 12.dp)
                        ) {
                            morePublicationsState.publications.forEach { publication ->
                                CreatePublicationRow(
                                    publication = publication,
                                    onPublicationClick
                                ) { publicationFromCallback, isFollowing ->
                                    if (isFollowing) {
                                        publicationsViewModel.followPublication(
                                            publicationFromCallback.slug
                                        )
                                    } else {
                                        publicationsViewModel.unfollowPublication(
                                            publicationFromCallback.slug
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
