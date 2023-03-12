package com.cornellappdev.android.volume.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.ui.components.general.CreateMagazineColumn
import com.cornellappdev.android.volume.ui.components.general.VolumeHeaderText
import com.cornellappdev.android.volume.ui.components.general.VolumeLoading
import com.cornellappdev.android.volume.ui.components.general.VolumeLogo
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.viewmodels.MagazinesViewModel
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
interface ExposedDropdownMenuBoxScope
private const val TAG = "MagazinesScreen"
@Preview
@Composable
fun MagazinesScreen(
    magazinesViewModel: MagazinesViewModel = hiltViewModel()
) {
    // Dropdown menu variables
    var expanded by remember { mutableStateOf(false) }
    val semesters = arrayListOf<String>()
    populateSemesterList(semesters)
    var selectedIndex by remember { mutableStateOf(0) }
    val magazineUiState = magazinesViewModel.magazineUiState

    Box {
        Scaffold(
            // Volume Logo
            topBar = {
                VolumeLogo()
            },

            content = { innerPadding ->
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, top = innerPadding.calculateTopPadding()),
                    content = {

                        // Featured header
                        item {
                            VolumeHeaderText(
                                text = "Featured",
                                underline = R.drawable.ic_underline_featured,
                                modifier = Modifier.padding(top = 15.dp)
                            )
                        }

                        // Featured magazines row
                        item {
                            FillFeaturedMagazinesRow(
                                magazineUiState = magazineUiState,
                                forFeatured = true
                            )
                        }

                        // Semester magazines text and dropdown menu
                        item {
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
                                    underline = R.drawable.ic_underline_more_magazines
                                )

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
                                        semesters.forEachIndexed { index, s ->
                                            if (index != selectedIndex) {
                                                DropdownMenuItem(onClick = {
                                                    selectedIndex = index
                                                    expanded = false
                                                    magazinesViewModel.querySemesterMagazines(
                                                        semester = formatSemester(semesters[selectedIndex])
                                                    )
                                                }) {
                                                    Text(
                                                        text = s,
                                                        color = VolumeOrange,
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 12.sp,
                                                        modifier = Modifier.drawWithContent {
                                                            drawContent()
                                                            // TODO use this to stylize better?
                                                            /*drawRoundRect(
                                                                color = VolumeOrange,
                                                                style = Stroke(width = 1.5.dp.toPx()),
                                                                cornerRadius = CornerRadius(
                                                                    x = 5.dp.toPx(),
                                                                    y = 5.dp.toPx()
                                                                ),
                                                                size = Size(
                                                                    width = 120.49.dp.toPx(),
                                                                    height = 1200.dp.toPx()
                                                                ),
                                                                topLeft = Offset(x=-15.dp.toPx(), y=-16.dp.toPx())
                                                            )*/
                                                        },
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Semester magazines row
                        item {
                            FillFeaturedMagazinesRow(
                                magazineUiState = magazineUiState,
                                forFeatured = false
                            )
                        }
                    })
            },
        )
    }
}
@Composable
fun FillFeaturedMagazinesRow(magazineUiState: MagazinesViewModel.MagazinesUiState, forFeatured: Boolean) {
    when (
        val magazinesState = if (forFeatured) magazineUiState.featuredMagazinesState
                                else magazineUiState.semesterMagazinesState
    ) {
        MagazinesRetrievalState.Loading -> {
            VolumeLoading()
        }
        MagazinesRetrievalState.Error -> {
            // TODO Retry prompt
        }
        is MagazinesRetrievalState.Success -> {
            LazyRow {
                items(magazinesState.magazines.size) {
                    magazinesState.magazines.forEach {
                        CreateMagazineColumn(magazine = it)
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

fun formatSemester(semester: String) =
    semester.substring(0, 2).lowercase() + semester.substring(semester.length-2, semester.length)

