package com.cornellappdev.android.volume.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.ui.components.general.BigFlyer
import com.cornellappdev.android.volume.ui.components.general.BigShimmeringFlyer
import com.cornellappdev.android.volume.ui.components.general.ErrorState
import com.cornellappdev.android.volume.ui.components.general.NothingToShowMessage
import com.cornellappdev.android.volume.ui.components.general.SearchBar
import com.cornellappdev.android.volume.ui.components.general.ShimmeringFlyer
import com.cornellappdev.android.volume.ui.components.general.SmallFlyer
import com.cornellappdev.android.volume.ui.components.general.VolumeHeaderText
import com.cornellappdev.android.volume.ui.components.general.VolumePeriod
import com.cornellappdev.android.volume.ui.states.FlyersRetrievalState
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.notoserif
import com.cornellappdev.android.volume.ui.viewmodels.FlyersViewModel
import com.cornellappdev.android.volume.ui.viewmodels.OrganizationsViewModel
import com.cornellappdev.android.volume.util.FlyerConstants
import kotlinx.coroutines.launch

private const val NO_FLYERS_MESSAGE =
    "If you want to see your organization's events on Volume, email us at volumeappdev@gmail.com"

@Composable
fun FlyersScreen(
    flyersViewModel: FlyersViewModel = hiltViewModel(),
    organizationsViewModel: OrganizationsViewModel = hiltViewModel(),
    onSearchClick: (Int) -> Unit,
    onOrganizationNameClick: (slug: String) -> Unit,
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val tags = FlyerConstants.CATEGORY_SLUGS.split(",")
    // Maps the category slugs to strings that are viewable on the app.
    val formattedTags = tags.map { s ->
        // If the slug is just a single word, capitalize it.
        if (s == s.lowercase()) {
            s.replaceFirstChar { c -> c.uppercase() }
            // If the slug is multiple words, split it on uppercase characters and join them, capitalizing each word.
        } else {
            s.split(Regex("(?<!^)(?=[A-Z])"))
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        }
    }
    var expanded by remember { mutableStateOf(false) }
    val uiState = flyersViewModel.flyersUiState

    val drawerClosed = scaffoldState.drawerState.isOpen
    LaunchedEffect(key1 = drawerClosed, block = {
        organizationsViewModel.reload()
    })

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    OrganizationsMenu(onOrganizationSlugClick = onOrganizationNameClick)
                }
            },
            content = { innerPadding ->
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    LazyColumn(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            top = innerPadding.calculateTopPadding()
                        )
                    ) {
                        // Page title
                        item {
                            Row {
                                Text(
                                    text = "Flyers",
                                    fontFamily = notoserif,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                                VolumePeriod(modifier = Modifier.padding(top = 39.dp, start = 7.dp))
                            }
                        }
                        // Search bar
                        item {
                            Row(
                                modifier = Modifier
                                    .padding(end = 16.dp, top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(6F)) {
                                    SearchBar(
                                        value = "",
                                        onValueChange = {},
                                        onClick = { onSearchClick(2) },
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
                                }
                                Image(painter = painterResource(id = R.drawable.ic_hamburger),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .clickable {
                                            coroutineScope.launch {
                                                scaffoldState.drawerState.open()
                                            }
                                        }
                                        .weight(1F)
                                )
                            }

                        }
                        // Today header
                        item {
                            Spacer(
                                modifier = Modifier
                                    .height(16.dp)
                                    .fillMaxWidth()
                            )
                            VolumeHeaderText(
                                text = "Today",
                                underline = R.drawable.ic_today_underline
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .fillMaxWidth()
                            )
                        }

                        // Big flyer row
                        item {
                            when (val todayFlyersState = uiState.todayFlyersState) {
                                FlyersRetrievalState.Loading -> {
                                    LazyRow(content = {
                                        items(5) {
                                            BigShimmeringFlyer(imgWidth = 340, imgHeight = 340)
                                            Spacer(modifier = Modifier.width(16.dp))
                                        }
                                    })
                                }

                                FlyersRetrievalState.Error -> {
                                    ErrorState()
                                }

                                is FlyersRetrievalState.Success -> {
                                    val flyers = todayFlyersState.flyers
                                    if (flyers.isEmpty()) {
                                        NothingToShowMessage(
                                            title = "No flyers today",
                                            message = NO_FLYERS_MESSAGE
                                        )
                                    } else {
                                        LazyRow {
                                            items(flyers) {
                                                BigFlyer(
                                                    340.dp,
                                                    it,
                                                    onOrganizationNameClick = onOrganizationNameClick
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }


                        // This week header
                        item {
                            Spacer(
                                modifier = Modifier
                                    .height(32.dp)
                                    .fillMaxWidth()
                            )
                            VolumeHeaderText(
                                text = "This Week",
                                underline = R.drawable.ic_underline_this_week
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .fillMaxWidth()
                            )
                        }

                        // Big flyer row
                        item {
                            when (val weeklyFlyersState = uiState.weeklyFlyersState) {
                                FlyersRetrievalState.Loading -> {
                                    LazyRow {
                                        items(5) {
                                            BigShimmeringFlyer(imgWidth = 256, imgHeight = 256)
                                            Spacer(modifier = Modifier.width(16.dp))
                                        }
                                    }
                                }

                                FlyersRetrievalState.Error -> {
                                    ErrorState()
                                }

                                is FlyersRetrievalState.Success -> {
                                    val flyers = weeklyFlyersState.flyers
                                    if (flyers.isEmpty()) {
                                        NothingToShowMessage(
                                            title = "No flyers this week",
                                            message = NO_FLYERS_MESSAGE
                                        )
                                    } else {
                                        LazyRow {
                                            items(flyers) {
                                                BigFlyer(
                                                    256.dp,
                                                    it,
                                                    onOrganizationNameClick = onOrganizationNameClick
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Upcoming and dropdown menu
                        item {
                            Spacer(modifier = Modifier.height(40.dp))

                            Row {
                                VolumeHeaderText(
                                    text = "Upcoming",
                                    underline = R.drawable.ic_underline_upcoming
                                )

                                Spacer(modifier = Modifier.weight(1F))

                                // Dropdown menu
                                Column(modifier = Modifier.padding(end = 24.dp /* this is 16 because offset */)) {
                                    Box(modifier = Modifier.drawWithContent {
                                        drawContent()
                                        drawRoundRect(
                                            color = VolumeOrange,
                                            style = Stroke(width = 1.5.dp.toPx()),
                                            cornerRadius = CornerRadius(
                                                x = 5.dp.toPx(),
                                                y = 5.dp.toPx()
                                            ),
                                            size = Size(
                                                width = 128.dp.toPx(),
                                                height = 31.dp.toPx()
                                            ),
                                            topLeft = Offset(x = 8.dp.toPx(), y = 0.dp.toPx())
                                        )
                                    }) {
                                        Row {
                                            Text(
                                                text = formattedTags[selectedIndex],
                                                color = VolumeOrange,
                                                modifier = Modifier
                                                    .clickable {
                                                        expanded = true
                                                    }
                                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                                    .height(16.dp)
                                                    .defaultMinSize(minWidth = 82.dp),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                            )
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_dropdown),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(top = 12.dp)
                                                    .clickable {
                                                        expanded = true
                                                    }
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }) {
                                        formattedTags.forEachIndexed { index, s ->
                                            if (index != selectedIndex) {
                                                DropdownMenuItem(onClick = {
                                                    selectedIndex = index
                                                    expanded = false
                                                    flyersViewModel.queryUpcomingFlyers(tags[selectedIndex])
                                                }) {
                                                    Text(
                                                        text = s,
                                                        color = VolumeOrange,
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 12.sp,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        // Upcoming flyer display
                        when (val upcomingFlyerState = uiState.upcomingFlyersState) {
                            FlyersRetrievalState.Loading -> {
                                items(5) {
                                    ShimmeringFlyer()
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            FlyersRetrievalState.Error -> {
                                item {
                                    ErrorState()
                                }
                            }

                            is FlyersRetrievalState.Success -> {
                                val flyers = upcomingFlyerState.flyers
                                if (flyers.isEmpty()) {
                                    item {
                                        NothingToShowMessage(
                                            title = "No upcoming flyers",
                                            message = "If you want to see your organization’s events on Volume, email us at volumeappdev@gmail.com"
                                        )
                                    }
                                } else {
                                    items(flyers) {
                                        Box(modifier = Modifier.padding(end = 16.dp)) {
                                            SmallFlyer(
                                                isExtraSmall = false,
                                                it,
                                                onOrganizationNameClick = onOrganizationNameClick
                                            )
                                        }
                                    }
                                }
                            }
                        }


                        item {
                            Spacer(modifier = Modifier.height(40.dp))
                            VolumeHeaderText(
                                text = "Past Flyers",
                                underline = R.drawable.ic_underline_past_flyers
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        when (val pastFlyersState = uiState.pastFlyersState) {
                            FlyersRetrievalState.Loading -> {
                                items(5) {
                                    ShimmeringFlyer()
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            FlyersRetrievalState.Error -> {
                                item {
                                    ErrorState()
                                }
                            }

                            is FlyersRetrievalState.Success -> {
                                val flyers = pastFlyersState.flyers
                                if (flyers.isEmpty()) {
                                    item {
                                        NothingToShowMessage(
                                            title = "No past flyers",
                                            message = "If you want to see your organization’s events on Volume, email us at volumeappdev@gmail.com"
                                        )
                                    }
                                } else {
                                    items(flyers) {
                                        Box(modifier = Modifier.padding(end = 16.dp)) {
                                            SmallFlyer(
                                                isExtraSmall = false,
                                                it,
                                                onOrganizationNameClick = onOrganizationNameClick
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            NothingToShowMessage(
                                title = "Are you an organization?",
                                message = "If you want to see your organization’s events on Volume, email us at volumeappdev@gmail.com",
                                showImage = true,
                                modifier = Modifier.padding(bottom = 141.dp)
                            )
                        }
                    }
                }
            }
        )
    }


}