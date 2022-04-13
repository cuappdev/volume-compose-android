package com.example.volume_android_revamp

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
//    homeViewModel: HomeViewModel
){
  val context = LocalContext.current
  VolumeTitle(context)
}

@Composable
fun VolumeTitle(context:Context){
    Row {
        Image(painter = painterResource(R.drawable.volume_title), contentDescription = context.getString(R.string.volume_title))
    }
}
@Preview
@Composable
fun PreviewHomeScreen(
//    homeViewModel: HomeViewModel
){
    HomeScreen()
}