package com.cornellappdev.android.volume.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.ui.components.general.CreatePartnerColumn
import com.cornellappdev.android.volume.ui.components.general.CreatePartnerRow
import com.cornellappdev.android.volume.ui.components.general.ErrorState
import com.cornellappdev.android.volume.ui.components.general.VolumeHeaderText
import com.cornellappdev.android.volume.ui.components.general.VolumeLoading
import com.cornellappdev.android.volume.ui.states.ResponseState
import com.cornellappdev.android.volume.ui.viewmodels.OrganizationsViewModel

@Composable
fun OrganizationsMenu(
    organizationsViewModel: OrganizationsViewModel = hiltViewModel(),
    onOrganizationSlugClick: (String) -> Unit,
) {
    val partitionedOrganizationsRes =
        organizationsViewModel.partitionedOrgsFlow.collectAsState().value

    when (partitionedOrganizationsRes) {
        is ResponseState.Error -> {
            ErrorState()
        }

        ResponseState.Loading -> {
            VolumeLoading()
        }

        is ResponseState.Success -> {
            val (followedOrganizations, unfollowedOrganizations) = partitionedOrganizationsRes.data
            Row(modifier = Modifier.padding(top = 25.dp)) {
                LazyColumn(modifier = Modifier.padding(horizontal = 12.dp)) {
                    item {
                        VolumeHeaderText(
                            text = "Following",
                            underline = R.drawable.ic_underline_following,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                        ) {
                            items(followedOrganizations) {
                                CreatePartnerColumn(
                                    profileImageURL = it.profileImageURL,
                                    slug = it.slug,
                                    title = it.name,
                                    onPartnerClick = onOrganizationSlugClick
                                )
                            }
                        }
                    }
                    item {
                        VolumeHeaderText(
                            text = "More Organizations",
                            underline = R.drawable.ic_underline_more_publications,
                            modifier = Modifier.padding(top = 30.dp)
                        )

                        Spacer(modifier = Modifier.height(22.dp))
                    }
                    items(unfollowedOrganizations) {
                        CreatePartnerRow(
                            profileImageURL = it.profileImageURL,
                            name = it.name,
                            slug = it.slug,
                            bio = it.bio,
                            onPartnerClick = onOrganizationSlugClick,
                            isFollowed = it.slug in followedOrganizations.map { org -> org.slug },
                            followButtonClicked = { partnerSlug, isFollowed ->
                                if (isFollowed) {
                                    organizationsViewModel.unfollowOrganization(partnerSlug)
                                } else {
                                    organizationsViewModel.followOrganization(partnerSlug)
                                }
                            },
                        )
                    }
                }
            }
        }
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            VolumeHeaderText(
                text = "Following",
                underline = R.drawable.ic_underline_following,
                modifier = Modifier.padding(start = 12.dp, top = 25.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}