package com.example.volume_android_revamp.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.volume_android_revamp.R
import com.example.volume_android_revamp.viewmodels.PublicationTabViewModel

@Composable
fun PublicationScreen(publicationTabViewModel: PublicationTabViewModel){
    val context = LocalContext.current
    Column{
        PublicationsTitle()
        FollowingPubs()
    }

}

@Composable
fun PublicationsTitle(){
    Row {
        Image(painter = painterResource(R.drawable.publications_title), contentDescription = stringResource(R.string.publications_title))
    }
}

@Composable
fun FollowingPubs(){
    Column {
        Text(stringResource(id = R.string.following), color = Color.Black, style = MaterialTheme.typography.subtitle1)
        Image(painter = painterResource(id = R.drawable.ic_following_line), contentDescription = stringResource(
            id = R.string.underline
        ))
        LazyRow{

        }
    }
}

//@Composable
//fun followedPublicationItem(data: )