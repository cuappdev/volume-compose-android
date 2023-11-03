package com.cornellappdev.android.volume.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.ui.components.general.ErrorState
import com.cornellappdev.android.volume.ui.components.general.FlyerWithContextDropdown
import com.cornellappdev.android.volume.ui.components.general.ShimmeringFlyer
import com.cornellappdev.android.volume.ui.states.ResponseState
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.notoserif
import com.cornellappdev.android.volume.ui.viewmodels.OrganizationsHomeViewModel

@Composable
fun OrganizationHome(
    organizationSlug: String,
    organizationsHomeViewModel: OrganizationsHomeViewModel = hiltViewModel(),
    onFlyerUploadClicked: () -> Unit,
    onFlyerEditClicked: (flyerId: String) -> Unit,
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var alertDialogShowing by remember { mutableStateOf(false) }
    var mostRecentlyClickedFlyerId by remember { mutableStateOf("") }
    val tabs = listOf("Current", "Past")
    val context = LocalContext.current

    LaunchedEffect(key1 = "launch", block = {
        organizationsHomeViewModel.initViewModel(organizationSlug)
        organizationsHomeViewModel.deletionResponseFlow.collect {
            when (it) {
                is ResponseState.Error -> {
                    Toast.makeText(
                        context,
                        "Deletion failed: ${it.errors.firstOrNull()?.message ?: "unknown error"}",
                        Toast.LENGTH_LONG
                    )
                }

                else -> {}
            }
        }
    })

    val currentFlyers = organizationsHomeViewModel.currentFlyersFlow.collectAsState().value
    val pastFlyers = organizationsHomeViewModel.pastFlyersFlow.collectAsState().value
    val currentOrg = organizationsHomeViewModel.orgFlow.collectAsState().value

    if (alertDialogShowing) {
        AlertDialog(
            onDismissRequest = { alertDialogShowing = false },
            confirmButton = {
                TextButton(onClick = {
                    alertDialogShowing = false
                    organizationsHomeViewModel.deleteFlyer(flyerId = mostRecentlyClickedFlyerId)
                }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { alertDialogShowing = false }) {
                    Text(text = "Dismiss")
                }
            },
            title = { Text(text = "Remove Flyer?") },
            icon = { Icon(imageVector = Icons.Outlined.Cancel, contentDescription = "Cancel") },
            text = {
                Text(text = "Removing a flyer will delete it from Volume’s feed, but you can always repost the flyer later.")
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Page header
        Text(
            text = "Organization Home",
            fontFamily = notoserif,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(38.dp))

        // Organization name
        Text(
            text = when (currentOrg) {
                ResponseState.Loading -> "Loading..."
                is ResponseState.Error -> "Error loading organization, reload the app"
                is ResponseState.Success -> currentOrg.data.name
            }, fontSize = 24.sp, fontFamily = notoserif
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Upload Flyer button
        Box(
            modifier = Modifier
                .requiredHeight(128.dp)
                .fillMaxWidth()
                .border(
                    BorderStroke(width = 4.dp, color = Color(208, 112, 0, 52)),
                    shape = RoundedCornerShape(4.dp)
                )
                .background(Color(208, 112, 0, 13))
                .clickable {
                    onFlyerUploadClicked()
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.CloudUpload,
                    contentDescription = "Upload",
                    tint = VolumeOrange,
                    modifier = Modifier.requiredSize(24.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Upload a New Flyer", color = VolumeOrange, fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Tabbed Flyers view
        TabRow(
            selectedTabIndex = selectedTabIndex,
            contentColor = Color.Black,
        ) {
            tabs.forEachIndexed { index, text ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = text,
                            fontSize = 16.sp,
                            fontFamily = notoserif,
                            color = Color.Black
                        )
                    },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.White,
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        when (selectedTabIndex) {
            0 -> {
                Text(text = "Current Flyers", fontSize = 20.sp, fontFamily = notoserif)
            }

            1 -> {
                Text(text = "Past Flyers", fontSize = 20.sp, fontFamily = notoserif)
            }
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            when (selectedTabIndex) {
                // Current Flyers
                0 -> {
                    when (currentFlyers) {
                        is ResponseState.Loading -> {
                            items(5) {
                                ShimmeringFlyer()
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        is ResponseState.Success -> {
                            items(currentFlyers.data) {
                                FlyerWithContextDropdown(
                                    flyer = it,
                                    onEditClick = {
                                        Toast.makeText(
                                            context,
                                            "Editing is coming soon!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    },
                                    onRemoveClick = {
                                        mostRecentlyClickedFlyerId = it.id
                                        alertDialogShowing = true
                                    }
                                )
                            }
                        }

                        is ResponseState.Error -> {
                            item {
                                ErrorState()
                            }
                        }
                    }
                }
                // Past Flyers
                1 -> {
                    when (pastFlyers) {
                        is ResponseState.Loading -> {
                            items(5) {
                                ShimmeringFlyer()
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        is ResponseState.Success -> {
                            items(pastFlyers.data) {
                                FlyerWithContextDropdown(
                                    flyer = it,
                                    onEditClick = {
                                        Toast.makeText(
                                            context,
                                            "Editing is coming soon!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    },
                                    onRemoveClick = {
                                        mostRecentlyClickedFlyerId = it.id
                                        alertDialogShowing = true
                                    })
                            }
                        }

                        is ResponseState.Error -> {
                            item {
                                ErrorState()
                            }
                        }
                    }
                }
            }
        }
    }
}
