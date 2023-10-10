package com.cornellappdev.android.volume.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.volume.ui.components.general.VolumeButton
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif

@Composable
fun FlyerSuccessScreen(onGoToFlyersClicked: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontFamily = notoserif,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Flyer Published!",
                fontSize = 20.sp,
                fontFamily = notoserif,
                modifier = Modifier.padding(top = 16.dp),
                fontWeight = FontWeight.Bold
            )
            Text(text = "Thank you for using Volume.", fontSize = 14.sp, fontFamily = lato)
            Spacer(Modifier.height(24.dp))
            VolumeButton(
                text = "Go to Flyers",
                onClick = onGoToFlyersClicked,
                enabled = true,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}