package com.example.volume_android_revamp.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.volume_android_revamp.AllArticlesQuery
import com.example.volume_android_revamp.PublicationByIDQuery
import com.example.volume_android_revamp.TrendingArticlesQuery
import com.example.volume_android_revamp.otherArticleItem
import com.example.volume_android_revamp.state.State
import com.example.volume_android_revamp.type.Article
import com.example.volume_android_revamp.type.Publication
import com.example.volume_android_revamp.viewmodels.HomeTabViewModel
import com.example.volume_android_revamp.viewmodels.IndividualPublicationViewModel

@Composable
fun IndividualPublicationScreen(individualPublicationViewModel: IndividualPublicationViewModel) {
    val publicationByIDState =
        individualPublicationViewModel.publicationByIDState.collectAsState().value
    LazyColumn() {
        item {
            publicationByIDState.value?.getPublicationByID?.let {

                when (val publicationByIDState =
                    individualPublicationViewModel.publicationByIDState.collectAsState().value) {
                    //is State.Success<PublicationByIDQuery.Data> ->
                        //publicationByIDState.value!!.getPublicationByID?.let { it1 -> PublicationBackground(data = it1) }
                    is State.Error<PublicationByIDQuery.Data> -> Log.d(
                        "HomeTab",
                        "EROROROROROROR aa"
                    )
                    is State.Loading<PublicationByIDQuery.Data> -> Log.d("HomeTab", "loading aa")
                    is State.Empty<PublicationByIDQuery.Data> -> Log.d("HomeTab", "empty aa")
                }

            }
        }
    }
}
//PublicationByIDQuery.GetPublicationByID
//.backgroundImageURL
@Composable
fun PublicationBackground(data: String) {
    Column {
        AsyncImage(
            model = data, modifier = Modifier
                .height(180.dp)
                .width(180.dp), contentDescription = null, contentScale = ContentScale.Crop
        )
    }
}

@Preview
@Composable
fun previewPage(){
    PublicationBackground("61980a202fef10d6b7f20747");
}


