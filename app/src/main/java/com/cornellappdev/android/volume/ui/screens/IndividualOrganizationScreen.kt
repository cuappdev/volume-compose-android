package com.cornellappdev.android.volume.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.data.models.Social
import com.cornellappdev.android.volume.ui.components.general.CreateIndividualPartnerHeading
import com.cornellappdev.android.volume.ui.components.general.ErrorState
import com.cornellappdev.android.volume.ui.components.general.NothingToShowMessage
import com.cornellappdev.android.volume.ui.components.general.ShimmeringFlyer
import com.cornellappdev.android.volume.ui.components.general.SmallFlyer
import com.cornellappdev.android.volume.ui.components.general.VolumeLoading
import com.cornellappdev.android.volume.ui.states.ResponseState
import com.cornellappdev.android.volume.ui.viewmodels.IndividualOrganizationViewModel
import java.time.LocalDateTime

@Composable
fun IndividualOrganizationScreen(
    individualOrganizationViewModel: IndividualOrganizationViewModel = hiltViewModel(),
) {
    val isFollowed = individualOrganizationViewModel.isFollowingFlow.collectAsState().value
    val flyersState = individualOrganizationViewModel.orgFlyersFlow.collectAsState().value

    val statsText = when (flyersState) {
        ResponseState.Loading -> {
            "..."
        }

        is ResponseState.Error -> {
            "Error loading organization stats"
        }

        is ResponseState.Success -> {
            val flyers = flyersState.data
            val upcoming = flyers.count { it.endDateTime > LocalDateTime.now() }
            "${flyers.size} events • $upcoming upcoming"
        }
    }

    when (val organizationResult = individualOrganizationViewModel.orgFlow.collectAsState().value) {
        ResponseState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                VolumeLoading()
            }
        }

        is ResponseState.Error -> {
            ErrorState()
        }

        is ResponseState.Success -> {
            val organization = organizationResult.data
            LazyColumn {
                item {
                    CreateIndividualPartnerHeading(
                        followButton = if (isFollowed is ResponseState.Success) isFollowed.data else false,
                        followButtonClicked = { TODO() },
                        backgroundImageUrl = organization.backgroundImageURL,
                        profileImageURL = organization.profileImageURL,
                        partnerName = organization.name,
                        statsText = statsText,
                        bio = organization.bio,
                        socials = listOf(Social("web", organization.websiteURL)),
                        websiteURL = organization.websiteURL,
                    )
                }
                item {
                    HeadingText(text = "Flyers")
                }
                when (flyersState) {
                    ResponseState.Loading -> {
                        items(5) {
                            ShimmeringFlyer()
                        }
                    }

                    is ResponseState.Error -> {
                        item {
                            ErrorState()
                        }
                    }

                    is ResponseState.Success -> {
                        val upcomingFlyers =
                            flyersState.data.filter { it.endDateTime > LocalDateTime.now() }
                        if (upcomingFlyers.isEmpty()) {
                            item {
                                NothingToShowMessage(
                                    title = "There’s nothing here.",
                                    message = "It seems like this organization hasn't published any flyers yet. ",
                                    showImage = true,
                                )
                            }
                        } else {
                            items(flyersState.data) {
                                SmallFlyer(isExtraSmall = false, flyer = it)
                            }
                        }
                    }
                }
            }
        }
    }

}