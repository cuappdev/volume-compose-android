package com.cornellappdev.android.volume.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif


@Composable
fun AboutUsScreen() {
    Scaffold(topBar = {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "About Us",
                fontFamily = notoserif,
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                color = Color.Black
            )
        }
    }, content = { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    Text(
                        text = "Our Mission",
                        fontFamily = notoserif,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_long_underline),
                        contentDescription = null,
                        modifier = Modifier.offset(y = (-7).dp)
                    )
                    Text(
                        text = "Created by Cornell AppDev, Volume aims to better connect student "
                                + "publications with student readers by making it easy and accessible "
                                + "to digest student created content. \n" +
                                "\n" +
                                "Volume believes in the unique power that lies in the sharing and "
                                + "distribution of content by students and for students to spark" +
                                " conversation, experiences, and connections that improve the collegiate"
                                + " experience.",
                        fontFamily = lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
                item {
                    Text(
                        text = "The Team",
                        fontFamily = notoserif,
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 30.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_medium_underline),
                        contentDescription = null,
                        modifier = Modifier.offset(y = (-7).dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Column {
                        SemesterPodLeadRow(semester = "Spring 2023", podLead = "Liam Du")
                        Column (verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            CategoryMembersPair(category = "Android", members = "Zach Seidner")
                            CategoryMembersPair(category = "Backend", members = "Isaac Han, Sasha Loayza")
                            CategoryMembersPair(category = "iOS", members = "Vin Bui, Vivian Nguyen")
                            CategoryMembersPair(category = "Design", members = "Liam Du, Amy Ge")
                            CategoryMembersPair(category = "Marketing", members = "Jane Lee, Sanjana Kaicker")
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                        BodyText(text = "Previous Semesters")
                        Spacer(modifier = Modifier.height(16.dp))
                        Column (verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            CategoryMembersPair(category = "Android", members = "Chris Desir, Aastha Shah, Justin Jiang, Emily Hu, Ben Harris")
                            CategoryMembersPair(category = "Backend", members = "Tedi Mitiku, Orko Sinha")
                            CategoryMembersPair(category = "Design", members = "Amanda He, Zixian Jia, Maggie Ying")
                            CategoryMembersPair(category = "iOS", members = "Sergio Diaz, Cameron Russell, Daniel Vebman")
                            CategoryMembersPair(category = "Marketing", members = "Jonna Chen, Monan Modi, Yi Hsin Wei")
                        }
                    }
                }
            }
        }
    })
}

@Composable
fun SemesterPodLeadRow(semester: String, podLead: String) {
    Row {
        BodyText(text = semester)
        Column {
            HeadingText(text = "Pod Lead", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
            BodyText(text = "Liam Du", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
        }
    }
}

@Composable
fun CategoryMembersPair (category: String, members: String) {
    Column {
        HeadingText(text = category)
        Spacer(modifier = Modifier.height(8.dp))
        BodyText(text = members)
    }
}
@Composable
fun HeadingText(text: String, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Start) {
    Text(text = text, modifier = modifier, fontFamily = notoserif, fontSize = 20.sp, fontWeight = FontWeight.Medium, textAlign = textAlign)
}

@Composable
fun BodyText(text: String, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Start) {
    Text(text = text, modifier = modifier, fontFamily = notoserif, fontSize = 16.sp, fontWeight = FontWeight.Normal, textAlign = textAlign)
}