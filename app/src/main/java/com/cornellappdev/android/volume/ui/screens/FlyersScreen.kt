package com.cornellappdev.android.volume.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.ui.components.general.BigFlyer
import com.cornellappdev.android.volume.ui.components.general.SmallFlyer
import com.cornellappdev.android.volume.ui.components.general.VolumeHeaderText
import com.cornellappdev.android.volume.ui.components.general.VolumePeriod
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.notoserif

@Composable
fun FlyersScreen() {
    var selectedIndex by remember { mutableStateOf(0) }
    val tags = listOf("All", "Cultural", "Dance", "Music", "Academic")
    var expanded by remember { mutableStateOf(false) }

    LazyColumn (modifier = Modifier.padding(start = 16.dp)) {
        item {
            Row {
                Text(text = "Flyers", fontFamily = notoserif, fontSize = 28.sp, fontWeight = FontWeight.Medium)
                VolumePeriod(modifier = Modifier.padding(top=25.dp, start = 7.dp))
            }
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
        // This week header
        item {
            VolumeHeaderText(text = "This Week", underline = R.drawable.ic_underline_this_week)
        }

        // Big flyer row
        item {
            LazyRow {
                items (10) {
                    BigFlyer()
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
        // Upcoming and dropdown menu
        item {
            Row {
                VolumeHeaderText(text = "Upcoming", underline = R.drawable.ic_underline_upcoming)

                Spacer(modifier = Modifier.weight(1F))

                // Dropdown menu
                Column(modifier = Modifier.padding(end = 54.3.dp)) {
                    Row(modifier = Modifier.drawWithContent {
                        drawContent()
                        drawRoundRect(
                            color = VolumeOrange,
                            style = Stroke(width = 1.5.dp.toPx()),
                            cornerRadius = CornerRadius(
                                x = 5.dp.toPx(),
                                y = 5.dp.toPx()
                            ),
                            size = Size(
                                width = 115.49.dp.toPx(),
                                height = 31.dp.toPx()
                            ),
                            topLeft = Offset(x = 8.dp.toPx(), y = 0.dp.toPx())
                        )
                    }) {
                        Text(
                            text = tags[selectedIndex],
                            color = VolumeOrange,
                            modifier = Modifier
                                .clickable {
                                    expanded = true
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .size(width = 72.dp, height = 16.dp),
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

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        tags.forEachIndexed { index, s ->
                            if (index != selectedIndex) {
                                DropdownMenuItem(onClick = {
                                    selectedIndex = index
                                    expanded = false
                                    if (tags[selectedIndex] == "All") {
                                        // Query all tags
                                    } else {
                                        // Query other tags
                                    }
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
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Small flyer display
        item {
            LazyHorizontalGrid(rows = GridCells.Fixed(3), modifier = Modifier.height(308.dp)) {
                items(20) {
                    SmallFlyer(inUpcoming = true)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
            VolumeHeaderText(text = "Past Flyers", underline = R.drawable.ic_underline_past_flyers)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items (10) {
            SmallFlyer(inUpcoming = false)
        }
    }
}