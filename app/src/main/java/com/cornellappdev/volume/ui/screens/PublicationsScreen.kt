package com.cornellappdev.volume.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.ui.components.general.CreateFollowPublicationRow
import com.cornellappdev.volume.ui.components.general.CreateHorizontalPublicationRowFollowing
import com.cornellappdev.volume.ui.states.PublicationRetrievalState
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
        Text(
            modifier = Modifier.padding(start = 20.dp, top = 20.dp),
            text = "Publications",
            fontFamily = notoserif,
            fontWeight = FontWeight.Medium,
            fontSize = 28.sp,
            textAlign = TextAlign.Left
        )
    }, content = { innerPadding ->
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
                when (val followingPublicationsState =
                    publicationsUiState.followedPublicationsState) {
                    PublicationsRetrievalState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = VolumeOrange)
                        }
                    }
                    PublicationsRetrievalState.Error -> {
                        Text("ERROR")
                        Text(PublicationRetrievalState.Error.toString())
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
                when (val morePublicationsState =
                    publicationsUiState.morePublicationsState) {
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
                        Column(
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(end = 12.dp)
                        ) {
                            morePublicationsState.publications.forEach { publication ->
                                CreateHorizontalPublicationRowFollowing(
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