package com.cornellappdev.android.volume.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.analytics.NavigationSource
import com.cornellappdev.android.volume.data.models.Article
import com.cornellappdev.android.volume.data.models.Magazine
import com.cornellappdev.android.volume.ui.components.general.CreateArticleRow
import com.cornellappdev.android.volume.ui.components.general.CreateMagazineColumn
import com.cornellappdev.android.volume.ui.components.general.ErrorState
import com.cornellappdev.android.volume.ui.components.general.NothingToShowMessage
import com.cornellappdev.android.volume.ui.components.general.SearchBar
import com.cornellappdev.android.volume.ui.components.general.ShimmeringArticle
import com.cornellappdev.android.volume.ui.components.general.ShimmeringFlyer
import com.cornellappdev.android.volume.ui.components.general.ShimmeringMagazine
import com.cornellappdev.android.volume.ui.components.general.SmallFlyer
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.states.FlyersRetrievalState
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import com.cornellappdev.android.volume.ui.theme.VolumeOffWhite
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.viewmodels.SearchViewModel

private const val TAG = "SearchScreen"

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    onMagazineClick: (Magazine) -> Unit,
    onArticleClick: (Article, NavigationSource) -> Unit,
    defaultTab: Int = 0,
) {
    var search by remember { mutableStateOf("") }
    val tabs = listOf("Articles", "Magazines", "Flyers")
    var tabIndex by remember { mutableStateOf(defaultTab) }
    val uiState = searchViewModel.searchUiState

    DisposableEffect(key1 = tabIndex, effect = {
        when (tabIndex) {
            0 -> searchViewModel.searchArticles(search)
            1 -> searchViewModel.searchMagazines(search)
            2 -> searchViewModel.searchFlyers(search)
        }
        onDispose { }
    })
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),


        ) {
        item(span = { GridItemSpan(2) }) {
            SearchBar(
                value = search,
                onValueChange = {
                    search = it
                    when (tabIndex) {
                        0 -> searchViewModel.searchArticles(it)
                        1 -> searchViewModel.searchMagazines(it)
                        2 -> searchViewModel.searchFlyers(it)
                    }
                },
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                autoFocus = true,
            )
        }
        item(span = { GridItemSpan(2) }) {
            TabRow(
                selectedTabIndex = tabIndex,
                contentColor = VolumeOrange,
                backgroundColor = VolumeOffWhite
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                title,
                                fontFamily = lato,
                                fontSize = 18.sp,
                                maxLines = 1,
                            )
                        },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        selectedContentColor = VolumeOrange,
                        unselectedContentColor = Color.Black,
                    )
                }
            }
        }
        when (tabIndex) {
            0 -> {
                // Show articles
                when (val articlesState = uiState.searchedArticlesState) {
                    ArticlesRetrievalState.Loading -> {
                        items(4, span = { GridItemSpan(2) }) {
                            ShimmeringArticle()
                        }
                    }

                    ArticlesRetrievalState.Error -> {
                        item(span = { GridItemSpan(2) }) {
                            ErrorState(modifier = Modifier.padding(top = 100.dp))
                        }
                    }

                    is ArticlesRetrievalState.Success -> {
                        if (articlesState.articles.isEmpty()) {
                            val recentSearch = search
                            item(span = { GridItemSpan(2) }) {
                                SearchEmptyState(type = "articles", recentSearch = recentSearch)
                            }
                        } else {
                            items(
                                articlesState.articles,
                                span = { GridItemSpan(2) }) { article ->
                                Box(
                                    modifier = Modifier.padding(
                                        vertical = 10.dp,
                                        horizontal = 16.dp
                                    )
                                ) {
                                    CreateArticleRow(
                                        article
                                    ) {
                                        onArticleClick(
                                            article,
                                            NavigationSource.SEARCH
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Show magazines
                when (val magazinesState = uiState.searchedMagazinesState) {
                    MagazinesRetrievalState.Loading -> {
                        item(span = { GridItemSpan(2) }) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        items(4) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                ShimmeringMagazine()
                            }
                        }
                    }

                    MagazinesRetrievalState.Error -> {
                        item(span = { GridItemSpan(2) }) {
                            ErrorState(modifier = Modifier.padding(top = 100.dp))
                        }
                    }

                    is MagazinesRetrievalState.Success -> {
                        if (magazinesState.magazines.isEmpty()) {
                            val recentSearch = search
                            item(span = { GridItemSpan(2) }) {
                                SearchEmptyState(
                                    type = "magazines",
                                    recentSearch = recentSearch
                                )
                            }
                        } else {
                            item(span = { GridItemSpan(2) }) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            items(magazinesState.magazines) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CreateMagazineColumn(
                                        magazine = it,
                                        onMagazineClick = onMagazineClick
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // show Flyers
                when (val searchedFlyersState = uiState.searchedFlyersState) {
                    FlyersRetrievalState.Loading -> {
                        items(10, span = { GridItemSpan(2) }) {
                            if (it % 2 == 1) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    ShimmeringFlyer(inUpcoming = false)

                                }
                            } else {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }

                    FlyersRetrievalState.Error -> {
                        item(span = { GridItemSpan(2) }) {
                            ErrorState()
                        }
                    }

                    is FlyersRetrievalState.Success -> {
                        val flyers = searchedFlyersState.flyers
                        val recentSearch = search
                        if (flyers.isEmpty()) {
                            item(span = { GridItemSpan(2) }) {
                                SearchEmptyState(type = "flyers", recentSearch = recentSearch)
                            }
                        } else {
                            item(span = { GridItemSpan(2) }) {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            items(
                                flyers,
                                span = { GridItemSpan(2) }) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                        SmallFlyer(isExtraSmall = false, it)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SearchEmptyState(type: String, recentSearch: String) {
    Column {
        Spacer(modifier = Modifier.height(100.dp))
        if (recentSearch.trim() == "") {
            NothingToShowMessage(
                title = "Not showing $type",
                message = "Try typing in the search bar to search for $type.",
            )
        } else {
            NothingToShowMessage(
                title = "No $type for this query.",
                message = "No $type match the query \"$recentSearch\". Try a more general query to see $type."
            )
        }
    }
}