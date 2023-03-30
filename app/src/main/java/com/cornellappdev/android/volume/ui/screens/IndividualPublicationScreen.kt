package com.cornellappdev.android.volume.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.analytics.NavigationSource
import com.cornellappdev.android.volume.data.models.Article
import com.cornellappdev.android.volume.data.models.Magazine
import com.cornellappdev.android.volume.ui.components.general.*
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import com.cornellappdev.android.volume.ui.states.PublicationRetrievalState
import com.cornellappdev.android.volume.ui.theme.GrayThree
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.viewmodels.IndividualPublicationViewModel


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun IndividualPublicationScreen(
    individualPublicationViewModel: IndividualPublicationViewModel = hiltViewModel(),
    onArticleClick: (Article, NavigationSource) -> Unit,
    onMagazineClick: (Magazine) -> Unit
) {
    val publicationUiState = individualPublicationViewModel.publicationUiState

    var tabIndex by remember { mutableStateOf(0) };

    // Publication heading
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        item (span = { GridItemSpan(2)}){
            when (val publicationState = publicationUiState.publicationState) {
                PublicationRetrievalState.Loading -> {
                    VolumeLoading()
                }
                PublicationRetrievalState.Error -> {
                }
                is PublicationRetrievalState.Success -> {
                    CreateIndividualPublicationHeading(
                        publication = publicationState.publication,
                        publicationUiState.isFollowed
                    ) { isFollowing ->
                        if (isFollowing) {
                            individualPublicationViewModel.followPublication()
                        } else {
                            individualPublicationViewModel.unfollowPublication()
                        }
                    }
                }
            }
        }
        item (span = { GridItemSpan(2)}){
            Row {
                Spacer(Modifier.weight(1f, true))
                Divider(Modifier.weight(1f, true), color = GrayThree, thickness = 2.dp)
                Spacer(Modifier.weight(1f, true))
            }
        }
        item (span = { GridItemSpan(2) }) {
            // Tab view for viewing articles or magazines
            val tabs = listOf("Articles", "Magazines")

            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(selectedTabIndex = tabIndex, contentColor = VolumeOrange) {
                    tabs.forEachIndexed { index, title ->
                        Tab(text = { Text(title, fontFamily = lato, fontSize = 18.sp) },
                            selected = tabIndex == index,
                            onClick = { tabIndex = index },
                            selectedContentColor = VolumeOrange,
                            unselectedContentColor = Color.Black,
                        )
                    }
                }
            }
        }
        when (tabIndex) {
            // Case: They are viewing the articles of the publication
            0 -> {
                when (val articlesByPublicationState = publicationUiState.articlesByPublicationState) {
                    ArticlesRetrievalState.Loading -> {
                        item ( span = { GridItemSpan(2) } ) {
                            VolumeLoading(modifier = Modifier.padding(top = 20.dp))
                        }
                    }
                    ArticlesRetrievalState.Error -> {

                    }
                    is ArticlesRetrievalState.Success -> {
                        articlesByPublicationState.articles.forEachIndexed { index, article ->
                            item (span = { GridItemSpan(2)}) {
                                Box(
                                    Modifier.padding(
                                        start = 12.dp,
                                        end = 12.dp,
                                        // Handles the padding between items
                                        top = if (index != 0) 20.dp else 20.dp
                                    )
                                ) {
                                    CreateArticleRow(
                                        article
                                    ) {
                                        onArticleClick(
                                            article,
                                            NavigationSource.OTHER_ARTICLES
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Case: They are viewing the magazines of the publication
            1 -> {
                when (val magazinesState = publicationUiState.magazinesByPublicationState) {
                    MagazinesRetrievalState.Loading -> {
                        item ( span = { GridItemSpan(2) } ) {
                            Box (modifier = Modifier.padding(top=20.dp)) {
                                VolumeLoading()
                            }
                        }
                    }
                    MagazinesRetrievalState.Error -> {  TODO()  }
                    is MagazinesRetrievalState.Success -> {
                        if (magazinesState.magazines.isEmpty()) {
                            item ( span = { GridItemSpan(2) } ) {
                                Column (verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                                    Box (modifier = Modifier.padding(top = 20.dp)) {
                                        NothingToShowText(message = "This publication has no magazines.")
                                    }
                                }
                            }
                        }
                        else {
                            items(magazinesState.magazines) {
                                CreateMagazineColumn(magazine = it, onMagazineClick = onMagazineClick)
                            }
                        }
                    }
                }
            }
        }
    }
}