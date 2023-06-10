package com.cornellappdev.android.volume.ui.screens

import android.util.Log
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.ui.components.general.BigFlyer
import com.cornellappdev.android.volume.ui.components.general.BigShimmeringFlyer
import com.cornellappdev.android.volume.ui.components.general.NothingToShowMessage
import com.cornellappdev.android.volume.ui.components.general.ShimmeringFlyer
import com.cornellappdev.android.volume.ui.components.general.SmallFlyer
import com.cornellappdev.android.volume.ui.components.general.VolumeHeaderText
import com.cornellappdev.android.volume.ui.components.general.VolumePeriod
import com.cornellappdev.android.volume.ui.states.FlyersRetrievalState
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.notoserif
import com.cornellappdev.android.volume.ui.viewmodels.FlyersViewModel
import com.cornellappdev.android.volume.util.FlyerConstants

@Composable
fun FlyersScreen(
    flyersViewModel: FlyersViewModel = hiltViewModel(),
) {
    var selectedIndex by remember { mutableStateOf(0) }
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
    val NO_FLYERS_MESSAGE =
        "If you want to see your organization's events on Volume, email us at volumeappdev@gmail.com"

    LazyColumn(modifier = Modifier.padding(start = 16.dp)) {
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
        // Today header
        item {
            Spacer(
                modifier = Modifier
                    .height(32.dp)
                    .fillMaxWidth()
            )
            VolumeHeaderText(text = "Today", underline = R.drawable.ic_today_underline)
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

                FlyersRetrievalState.Error -> {}
                is FlyersRetrievalState.Success -> {
                    val flyers = todayFlyersState.flyers
                    if (flyers.isEmpty()) {
                        NothingToShowMessage(title = "No flyers today", message = NO_FLYERS_MESSAGE)
                    } else {
                        LazyRow {
                            items(flyers) {
                                BigFlyer(340.dp, it)
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
            VolumeHeaderText(text = "This Week", underline = R.drawable.ic_underline_this_week)
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
                    // TODO
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
                                BigFlyer(256.dp, it)
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
                VolumeHeaderText(text = "Upcoming", underline = R.drawable.ic_underline_upcoming)

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
                                    Log.d(
                                        "TAG",
                                        "FlyersScreen: Querying for tag ${tags[selectedIndex]} "
                                    )
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
        // Small flyer display
        item {
            when (val upcomingFlyerState = uiState.upcomingFlyersState) {
                FlyersRetrievalState.Loading -> {
                    LazyHorizontalGrid(
                        rows = GridCells.Fixed(3),
                        modifier = Modifier.height(308.dp)
                    ) {
                        items(9) {
                            ShimmeringFlyer()
                        }
                    }
                }

                FlyersRetrievalState.Error -> {
                    // TODO
                }

                is FlyersRetrievalState.Success -> {
                    val flyers = upcomingFlyerState.flyers
                    if (flyers.isEmpty()) {
                        NothingToShowMessage(
                            title = "No upcoming flyers for this category",
                            message = NO_FLYERS_MESSAGE
                        )
                    } else {
                        LazyHorizontalGrid(
                            rows = GridCells.Fixed(3),
                            modifier = Modifier.height(308.dp)
                        ) {
                            items(flyers) {
                                SmallFlyer(inUpcoming = true, it)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
            VolumeHeaderText(text = "Past Flyers", underline = R.drawable.ic_underline_past_flyers)
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
                // TODO
            }

            is FlyersRetrievalState.Success -> {
                val flyers = pastFlyersState.flyers
                if (flyers.isEmpty()) {
                    item {
                        NoMoreText(text = "No past flyers")
                    }
                } else {
                    items(flyers) {
                        SmallFlyer(inUpcoming = false, it)
                    }
                }
            }
        }
    }
}

@Composable
fun NoMoreText(text: String) {
    Row {
        Spacer(modifier = Modifier.weight(1f))
        Column(modifier = Modifier.width(219.dp)) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontFamily = notoserif,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "If you want to see your organization’s events on Volume, email us at volumeappdev@gmail.com",
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}