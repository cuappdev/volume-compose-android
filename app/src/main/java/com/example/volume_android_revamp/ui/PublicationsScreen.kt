package com.example.volume_android_revamp.ui

import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.volume_android_revamp.AllArticlesQuery
import com.example.volume_android_revamp.AllPublicationsQuery
import com.example.volume_android_revamp.R
import com.example.volume_android_revamp.viewmodels.PublicationTabViewModel
import com.example.volume_android_revamp.state.State


@Composable
fun PublicationScreen(publicationTabViewModel: PublicationTabViewModel){
    val allPublicationState = publicationTabViewModel.allPublicationsState.collectAsState().value
    LazyColumn{
        item { publicationsTitle() }
        item { morePublications() }
        allPublicationState.value?.getAllPublications?.let{
            items(it.size) {
                when (val allPublicationState =
                    publicationTabViewModel.allPublicationsState.collectAsState().value){
                    is State.Success<AllPublicationsQuery.Data> ->
                        morePublicationItem(data=allPublicationState.value?.getAllPublications?.get(it))
                    is State.Error<AllPublicationsQuery.Data> -> Log.d("PublicationsTab", "EROROROROROROR aa")
                    is State.Loading<AllPublicationsQuery.Data> -> Log.d("PublicationsTab", "loading aa")
                    is State.Empty<AllPublicationsQuery.Data> -> Log.d("PublicationsTab", "empty aa")
                }
            }
        }
//        item{ followingPubs() }
//        item{ morePublications() }

    }

}

@Composable
fun publicationsTitle(){
    Row {
        Image(painter = painterResource(R.drawable.publications_title), contentDescription = stringResource(R.string.publications_title))
    }
}

//@Composable
//fun followingPubs(){
//    Column {
//        Text(stringResource(id = R.string.following), color = Color.Black, style = MaterialTheme.typography.subtitle1)
//        Image(painter = painterResource(id = R.drawable.ic_following_pub_line), contentDescription = stringResource(
//            id = R.string.underline
//        ))
//        LazyRow{
//
//        }
//    }
//}

@Composable
fun morePublications(){
    Box {
        Text(stringResource(id = R.string.more_publicationa), color = Color.Black, style = MaterialTheme.typography.subtitle1)
        Image(painter = painterResource(id = R.drawable.ic_more_pub_line), contentDescription = stringResource(
            id = R.string.underline
        ))

    }
}

//@Composable
//fun followedPublicationItem(){
//    Column(modifier = Modifier.width(87.dp)) {
//        // image
//        // name
//
//    }
//}

@Composable
fun morePublicationItem(data : AllPublicationsQuery.GetAllPublication?){
    Row (modifier = Modifier.height(89.dp)){
        if (data != null){
            AsyncImage(model = data.profileImageURL, contentDescription = null, Modifier
                .height(50.dp)
                .width(50.dp)
                .clip(CircleShape), contentScale = ContentScale.Crop)
        }
        Column (modifier = Modifier.width(237.dp)){
            if (data !=null){
                Text(text = data.name)
                Text(text = data.bio)
                data.mostRecentArticle?.title?.let { Text(text = truncateTitle(it)) }
            }
        }
        Button(
            onClick = { }
        ){
            Image(painter = painterResource(id = R.drawable.follow_small), contentDescription = null)
        }

    }
}

fun truncateTitle(title: String):String = if (title.length > 57) (title.substring(0, 57) + "...") else title