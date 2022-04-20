package com.example.volume_android_revamp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.volume_android_revamp.R
import com.example.volume_android_revamp.viewmodels.PublicationTabViewModel

@Composable
fun PublicationScreen(publicationTabViewModel: PublicationTabViewModel){
    val context = LocalContext.current
    Column{
        publicationsTitle()
        LazyColumn{
            item{ followingPubs() }
            item{ morePublications() }
        }
    }

}

@Composable
fun publicationsTitle(){
    Row {
        Image(painter = painterResource(R.drawable.publications_title), contentDescription = stringResource(R.string.publications_title))
    }
}

@Composable
fun followingPubs(){
    Column {
        Text(stringResource(id = R.string.following), color = Color.Black, style = MaterialTheme.typography.subtitle1)
        Image(painter = painterResource(id = R.drawable.ic_following_pub_line), contentDescription = stringResource(
            id = R.string.underline
        ))
        LazyRow{

        }
    }
}

@Composable
fun morePublications(){
    Column {
        Text(stringResource(id = R.string.more_publicationa), color = Color.Black, style = MaterialTheme.typography.subtitle1)
        Image(painter = painterResource(id = R.drawable.ic_more_pub_line), contentDescription = stringResource(
            id = R.string.underline
        ))
        LazyColumn{

        }
    }
}

@Composable
fun followedPublicationItem(){
    Column(modifier = Modifier.width(87.dp)) {
        // image
        // name
        
    }
}

@Composable
fun morePublicationItem(){
    Row (modifier = Modifier.height(89.dp)){
        // image
        Column (modifier = Modifier.width(237.dp)){
            // name
            // description
            // title of most recent article
        }
        // follow button

    }
}