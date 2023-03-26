package com.cornellappdev.android.volume.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.analytics.NavigationSource
import com.cornellappdev.android.volume.data.models.Article
import com.cornellappdev.android.volume.ui.components.general.CreateArticleRow
import com.cornellappdev.android.volume.ui.components.general.CreateIndividualPublicationHeading
import com.cornellappdev.android.volume.ui.components.general.VolumeLoading
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.states.PublicationRetrievalState
import com.cornellappdev.android.volume.ui.theme.GrayThree
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.viewmodels.IndividualPublicationViewModel

private const val TAG = "IndividualPublicationScreen"

@Composable
fun IndividualPublicationScreen(
    individualPublicationViewModel: IndividualPublicationViewModel = hiltViewModel(),
    onArticleClick: (Article, NavigationSource) -> Unit
) {
    val publicationUiState = individualPublicationViewModel.publicationUiState

    LazyColumn {
        item {
            when (val publicationState = publicationUiState.publicationState) {
                PublicationRetrievalState.Loading -> {
                    Log.d(TAG, "IndividualPublicationScreen: Loading state reached")
                    VolumeLoading()
                }
                PublicationRetrievalState.Error -> {
                    Log.d(TAG, "IndividualPublicationScreen: Error state reached")
                }
                is PublicationRetrievalState.Success -> {
                    Log.d(TAG, "IndividualPublicationScreen: Success state reached")
                    CreateIndividualPublicationHeading(
                        publication = publicationState.publication,
                        publicationUiState.isFollowed
                    ) { isFollowing ->
                        if (isFollowing) {
                            individualPublicationViewModel.followPublication()
                        } else {
                            individualPublicationViewModel.unfollowPublication()
                        }
                    }
                }
            }
        }
        item {
            Row {
                Spacer(Modifier.weight(1f, true))
                Divider(Modifier.weight(1f, true), color = GrayThree, thickness = 2.dp)
                Spacer(Modifier.weight(1f, true))
            }
        }
        when (val articlesByPublicationState = publicationUiState.articlesByPublicationState) {
            ArticlesRetrievalState.Loading -> {
                item {
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
            }
            ArticlesRetrievalState.Error -> {

            }
            is ArticlesRetrievalState.Success -> {
                itemsIndexed(articlesByPublicationState.articles) { index, article ->
                    Box(
                        Modifier.padding(
                            start = 12.dp,
                            end = 12.dp,
                            // Handles the padding between items
                            top = if (index != 0) 20.dp else 20.dp
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
}


