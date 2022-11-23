package com.cornellappdev.android.volume.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
                    Text(
                        text = "The founding members that together made Volume a reality ✨",
                        fontFamily = lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
                item {

                    Text(
                        text = "\uD83D\uDCE3  Android",
                        fontFamily = lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                    Text(
                        text = "Chris Desir, Aastha Shah, Justin Jiang, Emily Hu, Ben Harris",
                        fontFamily = lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(
                            top = 4.dp,
                            start = 25.dp
                        )
                    )
                    Text(
                        text = "\uD83D\uDCE3  Backend",
                        fontFamily = lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                    Text(
                        text = "Tedi Mitiku, Orko Sinha",
                        fontFamily = lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(
                            top = 4.dp,
                            start = 25.dp
                        )
                    )
                    Text(
                        text = "\uD83D\uDCE3  Design",
                        fontFamily = lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                    Text(
                        text = "Amanda He, Zixian Jia, Maggie Ying",
                        fontFamily = lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(
                            top = 4.dp,
                            start = 25.dp
                        )
                    )
                    Text(
                        text = "\uD83D\uDCE3  iOS",
                        fontFamily = lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                    Text(
                        text = "Sergio Diaz, Cameron Russell, Daniel Vebman",
                        fontFamily = lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(
                            top = 4.dp,
                            start = 25.dp
                        )
                    )
                    Text(
                        text = "\uD83D\uDCE3  Marketing",
                        fontFamily = lato,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                    Text(
                        text = "Jonna Chen, Monan Modi, Yi Hsin Wei",
                        fontFamily = lato,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(
                            top = 4.dp,
                            start = 25.dp
                        )
                    )
                }
            }
        }
    })
}

@Preview
@Composable
fun ComposablePreview() {
    AboutUsScreen()
}
