package com.cornellappdev.android.volume.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.volume.ui.components.general.VolumeTextField
import com.cornellappdev.android.volume.ui.theme.notoserif

@Composable
@Preview
fun OrganizationsLoginScreen() {
    var slug by remember { mutableStateOf("") }
    var accessCode by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            fontSize = 20.sp,
            fontFamily = notoserif,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Organization Slug",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 38.dp),
            fontFamily = notoserif,
            fontSize = 16.sp
        )
        VolumeTextField(
            value = slug, onValueChange = { slug = it }, modifier = Modifier.height(50.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Organization Slug",
            modifier = Modifier
                .fillMaxWidth(),
            fontFamily = notoserif,
            fontSize = 16.sp
        )
        VolumeTextField(
            value = accessCode,
            onValueChange = { accessCode = it },
            modifier = Modifier.height(50.dp)
        )
    }
}