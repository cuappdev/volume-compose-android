package com.cornellappdev.android.volume.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.data.models.Publication
import com.cornellappdev.android.volume.ui.components.general.CreatePublicationColumn
import com.cornellappdev.android.volume.ui.components.general.CreatePublicationRow
import com.cornellappdev.android.volume.ui.components.general.ErrorState
import com.cornellappdev.android.volume.ui.components.general.VolumeHeaderText
import com.cornellappdev.android.volume.ui.components.general.VolumeLoading
import com.cornellappdev.android.volume.ui.states.PublicationsRetrievalState
import com.cornellappdev.android.volume.ui.viewmodels.PublicationsViewModel

private const val TAG = "PublicationsMenu"

@Composable
fun PublicationsMenu(
    publicationsViewModel: PublicationsViewModel = hiltViewModel(),
    onPublicationClick: (Publication) -> Unit,
) {
    val publicationsUiState = publicationsViewModel.publicationsUiState

    LazyColumn(
        modifier =
        Modifier
            .fillMaxSize()
    ) {
        item {
            VolumeHeaderText(
                text = "Following",
                underline = R.drawable.ic_underline_following,
                modifier = Modifier.padding(start = 12.dp, top = 25.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            when (val followingPublicationsState =
                publicationsUiState.followedPublicationsState) {
                PublicationsRetrievalState.Loading -> {
                    VolumeLoading()
                }

                PublicationsRetrievalState.Error -> {
                    ErrorState()
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
            VolumeHeaderText(
                text = "More Publications",
                underline = R.drawable.ic_underline_more_publications,
                modifier = Modifier.padding(start = 12.dp, top = 30.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))
        }
        when (val morePublicationsState =
            publicationsUiState.morePublicationsState) {
            PublicationsRetrievalState.Loading -> {
                item {
                    VolumeLoading()
                }
            }

            PublicationsRetrievalState.Error -> {
                item {
                    ErrorState()
                }
            }

            is PublicationsRetrievalState.Success -> {
                itemsIndexed(morePublicationsState.publications) { index, publication ->
                    Box(
                        Modifier.padding(
                            end = 12.dp,
                            start = 12.dp,
                            // Handles the padding between items
                            top = if (index != 0) 24.dp else 0.dp
                        )
                    ) {
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
