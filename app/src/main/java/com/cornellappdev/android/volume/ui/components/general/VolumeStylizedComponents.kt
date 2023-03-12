package com.cornellappdev.android.volume.ui.components.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.notoserif
// TODO apply abstractions to files other than magazine screen.

@Composable
fun VolumeLogo(modifier: Modifier=Modifier) {
    Image(
        painter = painterResource(R.drawable.volume_title),
        contentDescription = null,
        modifier = modifier
            .scale(0.8f)
    )
}

@Composable
fun VolumeHeaderText(text: String, underline: Int, modifier: Modifier=Modifier) {
    Column (modifier = modifier) {
        Text(
            text = text,
            fontFamily = notoserif,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
        )
        Image(
            painter = painterResource(underline),
            contentDescription = null,
            modifier = Modifier
                .offset(y = (-5).dp)
                .padding(start = 2.dp)
                .scale(1.05F)
        )
    }
}

// TODO implement abstraction in more areas
@Composable
fun VolumeLoading() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = VolumeOrange)
    }
}