package com.cornellappdev.android.volume.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.data.models.Magazine
import com.cornellappdev.android.volume.ui.components.general.CreateMagazineColumn
import com.cornellappdev.android.volume.ui.components.general.NothingToShowMessage
import com.cornellappdev.android.volume.ui.components.general.SearchBar
import com.cornellappdev.android.volume.ui.components.general.ShimmeringMagazine
import com.cornellappdev.android.volume.ui.components.general.VolumeHeaderText
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.viewmodels.MagazinesViewModel
import java.util.*

private const val TAG = "MagazinesScreen"

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MagazinesViewer(
    magazinesViewModel: MagazinesViewModel = hiltViewModel(),
    onMagazineClick: (magazine: Magazine) -> Unit,
) {
    // Dropdown menu variables
    var expanded by remember { mutableStateOf(false) }
    val semesters = arrayListOf<String>()
    semesters.add("View all")
    populateSemesterList(semesters)

    var selectedIndex by remember { mutableStateOf(0) }
    val magazineUiState = magazinesViewModel.magazineUiState

    Box {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            columns = GridCells.Fixed(2)
        ) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 12.dp, end = 16.dp)
                        .clickable { /* TODO take to search screen */ }) {
                    SearchBar(value = "", onChangeValue = {})
                }
            }
            // Featured header
            item(span = { GridItemSpan(2) }) {
                VolumeHeaderText(
                    text = "Featured",
                    underline = com.cornellappdev.android.volume.R.drawable.ic_underline_featured,
                    modifier = Modifier.padding(top = 15.dp, start = 16.dp)
                )
            }

            // Featured magazines row
            item(span = { GridItemSpan(2) }) {
                FillFeaturedMagazinesRow(
                    magazineUiState = magazineUiState,
                    onMagazineClick = onMagazineClick
                )
            }

            // Semester magazines text and dropdown menu
            item(span = { GridItemSpan(2) }) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    // More magazines text
                    VolumeHeaderText(
                        text = "More magazines",
                        underline = com.cornellappdev.android.volume.R.drawable.ic_underline_more_magazines,
                        modifier = Modifier.padding(start = 16.dp)
                    )

                    // Dropdown menu
                    Column(modifier = Modifier.padding(end = 16.dp)) {
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
                                text = semesters[selectedIndex],
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
                                painter = painterResource(id = com.cornellappdev.android.volume.R.drawable.ic_dropdown),
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
                            semesters.forEachIndexed { index, s ->
                                if (index != selectedIndex) {
                                    DropdownMenuItem(onClick = {
                                        selectedIndex = index
                                        expanded = false
                                        if (semesters[selectedIndex] == "View all") {
                                            magazinesViewModel.queryMoreMagazines("View all")
                                        } else {
                                            magazinesViewModel.queryMoreMagazines(
                                                query = formatSemester(semesters[selectedIndex])
                                            )
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

            // Semester magazines view
            when (val magazinesState = magazineUiState.moreMagazinesState) {
                MagazinesRetrievalState.Loading -> {
                    items(5) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            ShimmeringMagazine()
                        }
                    }
                }

                MagazinesRetrievalState.Error -> { /* TODO */
                }

                is MagazinesRetrievalState.Success -> {
                    items(magazinesState.magazines) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CreateMagazineColumn(magazine = it, onMagazineClick = onMagazineClick)
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun FillFeaturedMagazinesRow(
    magazineUiState: MagazinesViewModel.MagazinesUiState,
    onMagazineClick: (magazine: Magazine) -> Unit,
) {
    when (val magazinesState = magazineUiState.featuredMagazinesState) {
        MagazinesRetrievalState.Loading -> {
            LazyRow {
                items(5) {
                    ShimmeringMagazine()
                }
            }
        }

        MagazinesRetrievalState.Error -> {
        }

        is MagazinesRetrievalState.Success -> {
            if (magazinesState.magazines.isEmpty()) {
                NothingToShowMessage(
                    title = "No featured magazines",
                    message = "If you want to see your publication’s magazines on Volume, email us at volumeappdev@gmail.com"
                )
            } else {
                LazyRow {
                    items(magazinesState.magazines) {
                        CreateMagazineColumn(
                            magazine = it,
                            onMagazineClick = onMagazineClick
                        )
                    }
                }
            }
        }
    }
}

/**
 * Populates an array list of Strings dynamically with the current and previous semesters.
 * @param semesters The list to be populated with semester strings.
 */
fun populateSemesterList(semesters: ArrayList<String>) {
    val calendar = Calendar.getInstance()
    var currentYear: Int = calendar.get(Calendar.YEAR)
    var season: String = if (calendar.get(Calendar.MONTH) <= 9) "Spring" else "Fall"

    repeat(5) {
        semesters.add("$season $currentYear")
        if (season == "Spring") {
            currentYear -= 1
            season = "Fall"
        } else {
            season = "Spring"
        }
    }
}

/**
 * Formats a given String to fit the backend semester format of "fa" or "sp" and then the year.
 * Requires: Semester string is at minimum length 2, and the first two letters are some form of
 * "fa" or "sp", and the last two characters are the digits of the year.
 */
fun formatSemester(semester: String) =
    semester.substring(0, 2).lowercase() + semester.substring(semester.length - 2, semester.length)

