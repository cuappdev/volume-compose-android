package com.cornellappdev.android.volume.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.ui.components.general.VolumePeriod
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif



@Composable
@Preview
fun ReadsScreen() {
    Column {
        Row (modifier = Modifier.padding(top = 20.dp)) {
            Text(
                modifier = Modifier.padding(start = 15.dp),
                text = "Reads",
                fontFamily = notoserif,
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                textAlign = TextAlign.Left
            )
            VolumePeriod(modifier = Modifier.padding(start = 10.dp, top=23.dp))
        }

        // Bookmarks and magazine tabs.
        var tabIndex by remember { mutableStateOf(0) };
        val tabs = listOf("Articles", "Magazines")
        Row {
            Spacer(modifier = Modifier.weight(1F))
        Column(modifier = Modifier.width(255.dp)) {

                TabRow(selectedTabIndex = tabIndex, contentColor = VolumeOrange, backgroundColor = Color.White) {
                    tabs.forEachIndexed { index, title ->
                        Tab(text = { Text(title, fontFamily = lato, fontSize = 18.sp) },
                            selected = tabIndex == index,
                            onClick = { tabIndex = index },
                            selectedContentColor = VolumeOrange,
                            unselectedContentColor = Color.Black,
                        )
                    }
                }
            }

//            when (tabIndex) {
//                TODO()
//            }
        Spacer(modifier = Modifier.weight(7F))
        Image(painter = painterResource(id = R.drawable.ic_hamburger), contentDescription = null, modifier = Modifier.padding(top = 20.dp))
            Spacer(modifier = Modifier.weight(1F))
        }
    }

}