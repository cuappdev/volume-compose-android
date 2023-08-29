package com.cornellappdev.android.volume.ui.components.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.notoserif

@Composable
fun BigReadShimmeringArticle() {
    Column (modifier = Modifier.padding(end = 24.dp)) {
        Box(modifier = Modifier
            .shimmerEffect()
            .requiredSize(180.dp, 180.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier
            .shimmerEffect()
            .requiredSize(180.dp, 20.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier
            .shimmerEffect()
            .requiredSize(60.dp, 20.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier
            .shimmerEffect()
            .requiredSize(100.dp, 20.dp))
    }
}

@Composable
fun ShimmeringArticle(modifier: Modifier = Modifier) {
    Row (modifier = modifier
        .fillMaxWidth()
        .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,) {
        Column {
            ShimmeringBox(width = 112, height = 16)
            Spacer(modifier = Modifier.height(4.dp))
            ShimmeringBox(width = 230, height = 64)
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                ShimmeringBox(width = 40, height = 8)
                Spacer(modifier = Modifier.width(6.dp))
                ShimmeringBox(width = 72, height = 8)
            }
        }
        Spacer(modifier = Modifier.width(27.dp))
        ShimmeringBox(width = 104, height = 104)
    }
}
@Composable
fun BigShimmeringFlyer(imgWidth: Int, imgHeight: Int) {
    Column (verticalArrangement = Arrangement.spacedBy(4.dp)) {
        ShimmeringBox(width = imgWidth, height = imgHeight)
        ShimmeringBox(width = 104, height = 16)
        ShimmeringBox(width = 184, height = 24)
        ShimmeringBox(width = 168, height = 16)
        ShimmeringBox(width = 82, height = 16)
    }
}

@Composable
fun ShimmeringFlyer(inUpcoming: Boolean = true) {
    Row (modifier = Modifier.padding(end = if (inUpcoming) 84.dp else 0.dp)) {
        ShimmeringBox(width = 92, height = 92)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            ShimmeringBox(width = 104, height = 16)
            Spacer(modifier = Modifier.height(4.dp))
            ShimmeringBox(width = 184, height = 24)
            Spacer(modifier = Modifier.height(8.dp))
            ShimmeringBox(width = 168, height = 16)
            Spacer(modifier = Modifier.height(8.dp))
            ShimmeringBox(width = 82, height = 16)
        }
    }
}


@Composable
fun VolumeLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = VolumeOrange)
    }
}

@Composable
fun ShimmeringMagazine() {
    Column (Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        ShimmeringBox(width = 150, height = 220)
        Spacer(modifier = Modifier.height(12.dp))
        ShimmeringBox(width = 94, height = 15)
        Spacer(modifier = Modifier.height(4.dp))
        ShimmeringBox(width = 120, height = 24)
    }
}

// Implement component when internet is down
@Composable
fun VolumeError(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.ic_nowifi), contentDescription = null)
        Text(text = "No Connection", fontFamily = notoserif, modifier = Modifier.padding(top = 32.dp), fontSize = 24.sp)
        Text(text = "Please try again later", fontSize = 17.sp)
    }
}

@Composable
private fun ShimmeringBox(width: Int, height: Int) {
    Box(modifier = Modifier
        .shimmerEffect()
        .requiredSize(width.dp, height.dp))
}