package com.cornellappdev.volume.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.ui.states.PublicationsRetrievalState
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.viewmodels.PublicationsViewModel

@Composable
fun PublicationsScreen(
    publicationsViewModel: PublicationsViewModel = hiltViewModel(),
    navController: NavController,
    onPublicationClick: (Publication) -> Unit
) {
    val publicationsUiState = publicationsViewModel.publicationsUiState

    LazyColumn {
        item { publicationsTitle() }
        item { morePublications() }
        when (publicationsUiState.publicationsState) {
            PublicationsRetrievalState.Loading -> {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = VolumeOrange)
                    }
                }
            }
            PublicationsRetrievalState.Error -> {

            }
            is PublicationsRetrievalState.Success -> {
                items(publicationsUiState.publicationsState.publications) { publication ->
                    morePublicationItem(publication)
                }
            }
        }
    }

    val data =
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("fromIndividualPublication")
            ?.observeAsState()


}

@Composable
fun publicationsTitle() {
//    Row {
//        Image(
//            painter = painterResource(R.drawable.publications_title),
//            contentDescription = stringResource(R.string.publications_title)
//        )
//    }
}

@Composable
fun morePublications() {
//    Box {
//        Text(
//            stringResource(id = R.string.more_publicationa),
//            color = Color.Black,
//            style = MaterialTheme.typography.subtitle1
//        )
//        Image(
//            painter = painterResource(id = R.drawable.ic_more_pub_line),
//            contentDescription = stringResource(
//                id = R.string.underline
//            )
//        )
//
//    }
}

@Composable
fun morePublicationItem(data: Publication) {
//    Row(modifier = Modifier.height(89.dp)) {
//        if (data != null) {
//            AsyncImage(
//                model = data.profileImageURL, contentDescription = null, Modifier
//                    .height(50.dp)
//                    .width(50.dp)
//                    .clip(CircleShape), contentScale = ContentScale.Crop
//            )
//        }
//        Column(modifier = Modifier.width(237.dp)) {
//            if (data != null) {
//                Text(text = data.name)
//                Text(text = data.bio)
//                data.mostRecentArticle?.title?.let { Text(text = truncateTitle(it)) }
//            }
//        }
//        Button(
//            onClick = { }
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.follow_small),
//                contentDescription = null
//            )
//        }
//
//    }
}

fun truncateTitle(title: String): String =
    if (title.length > 57) (title.substring(0, 57) + "...") else title
