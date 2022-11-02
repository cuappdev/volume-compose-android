package com.cornellappdev.volume.ui.theme

import android.annotation.SuppressLint
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val DarkGray = Color(0xFF979797)
val VolumeOrange = Color(0xFFD07000)
val GrayOne = Color(0xFF979797)
val VolumeOffWhite = Color(0xFFFAFAFA)
val GrayFour = Color(0xFFC4C4C4)
val GrayThree = Color(0xFFEEEEEE)


@SuppressLint("ConflictingOnColor")
val LightColors = lightColors(
    primary = VolumeOffWhite,
    onPrimary = Color.Black,
    background = VolumeOffWhite,
    surface = VolumeOffWhite,
)
