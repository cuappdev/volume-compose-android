package com.cornellappdev.android.volume.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.analytics.NavigationSource
import com.cornellappdev.android.volume.ui.components.general.CreateArticleRow
import com.cornellappdev.android.volume.ui.components.general.CreateBigReadRow
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif

@Preview
@Composable
fun MagazinesScreen() {
    Box {
        Scaffold(
            topBar = {
                Image(
                    painter = painterResource(R.drawable.volume_title),
                    contentDescription = null,
                    modifier = Modifier
                        .scale(0.8f)
                )
            },
            content = { innerPadding ->
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, top = innerPadding.calculateTopPadding()),
                    content = {
                        item {
                            Column {
                                Text(
                                    text = "Featured",
                                    fontFamily = notoserif,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(top = 15.dp)
                                )
                                Image(
                                    painter = painterResource(R.drawable.ic_underline_featured),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .offset(y = (-5).dp)
                                        .padding(start = 2.dp)
                                        .scale(1.05F)
                                )
                            }
                            Spacer(modifier = Modifier.height(25.dp))
                        }
                    })
            },
        )
    }
}