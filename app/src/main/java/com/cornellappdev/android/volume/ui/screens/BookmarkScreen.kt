package com.cornellappdev.android.volume.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.analytics.NavigationSource
import com.cornellappdev.android.volume.data.models.Article
import com.cornellappdev.android.volume.data.models.Magazine
import com.cornellappdev.android.volume.ui.components.general.CreateArticleRow
import com.cornellappdev.android.volume.ui.components.general.CreateMagazineColumn
import com.cornellappdev.android.volume.ui.components.general.NothingToShowText
import com.cornellappdev.android.volume.ui.components.general.VolumeLoading
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import com.cornellappdev.android.volume.ui.theme.VolumeOffWhite
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif
import com.cornellappdev.android.volume.ui.viewmodels.BookmarkViewModel
import com.cornellappdev.android.volume.util.BookmarkStatus
import com.cornellappdev.android.volume.util.FinalBookmarkStatus

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookmarkScreen(
    bookmarkViewModel: BookmarkViewModel = hiltViewModel(),
    savedStateHandle: SavedStateHandle,
    onArticleClick: (Article, NavigationSource) -> Unit,
    onMagazineClick: (Magazine) -> Unit = TODO(),
    onSettingsClick: () -> Unit
) {
    val bookmarkUiState = bookmarkViewModel.bookmarkUiState

    // The ArticleWebViewScreen sends a BookmarkStatus to the BookmarkScreen if the bookmark state
    // of any of the articles change. Since you can open articles from the bookmark screen and change
    // the bookmark state from there, it's important that the changes are reflected)
    val bookmarkStatus = savedStateHandle.getLiveData<BookmarkStatus>("bookmarkStatus")
        .observeAsState()

    if (bookmarkStatus.value?.status == FinalBookmarkStatus.BOOKMARKED_TO_UNBOOKMARKED) {
        bookmarkStatus.value?.articleId?.let { bookmarkViewModel.removeArticle(it) }
    }

    Scaffold(topBar = {
        BookmarksTopBar(onSettingsClick)
    }, content = { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            TabbedBooksmarksView(
                onArticleClick = onArticleClick,
                onMagazineClick = onMagazineClick,
                bookmarkUiState = bookmarkUiState,
                bookmarkViewModel = bookmarkViewModel,
            )
        }
    })
}
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun TabbedBooksmarksView(onArticleClick: (Article, NavigationSource) -> Unit,
                         bookmarkUiState: BookmarkViewModel.BookmarkUiState,
                         bookmarkViewModel: BookmarkViewModel,
                         onMagazineClick: (Magazine) -> Unit,
) {
    var tabIndex by remember { mutableStateOf(0) };

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
        when (tabIndex) {
            0 -> BookmarkedArticlesScreen(onArticleClick =  onArticleClick, bookmarkUiState =  bookmarkUiState, bookmarkViewModel = bookmarkViewModel)
            1 -> BookmarkedMagazinesScreen(onMagazineClick = onMagazineClick, bookmarkUiState = bookmarkUiState)
        }
    }
}
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun BookmarkedMagazinesScreen(bookmarkUiState: BookmarkViewModel.BookmarkUiState, onMagazineClick: (Magazine) -> Unit) {
    when (val magazinesState = bookmarkUiState.magazinesState) {
        MagazinesRetrievalState.Loading -> {
            Box (modifier = Modifier.padding(top=20.dp)) {
                VolumeLoading()
            }
        }
        MagazinesRetrievalState.Error -> {  TODO()  }
        is MagazinesRetrievalState.Success -> {
            if (magazinesState.magazines.isEmpty()) {
                Column (verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                    NothingToShowText(message = "You have no saved magazines.")
                }
            }
            else {
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(magazinesState.magazines) {
                        CreateMagazineColumn(magazine = it, onMagazineClick = onMagazineClick, isBookmarked = true)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookmarkedArticlesScreen(onArticleClick: (Article, NavigationSource) -> Unit, bookmarkUiState: BookmarkViewModel.BookmarkUiState, bookmarkViewModel: BookmarkViewModel) {
    when (val articleState = bookmarkUiState.articlesState) {
        ArticlesRetrievalState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = VolumeOrange)
            }
        }
        ArticlesRetrievalState.Error -> {
            // TODO
        }
        is ArticlesRetrievalState.Success -> {
            Column {
                if (articleState.articles.isEmpty()) {
                    NothingToShowText(message = "You have no saved articles.")
                } else {
                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp, end = 12.dp, top = 20.dp),
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            items(articleState.articles) { article ->
                                val dismissState = rememberDismissState()
                                if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                                    bookmarkViewModel.removeArticle(article.id)
                                }

                                SwipeToDismiss(
                                    state = dismissState,
                                    directions = setOf(
                                        DismissDirection.EndToStart
                                    ),

                                    background = {
                                        val backgroundColor by animateColorAsState(
                                            when (dismissState.targetValue) {
                                                DismissValue.Default -> VolumeOffWhite
                                                else -> VolumeOrange
                                            }
                                        )

                                        val iconColor by animateColorAsState(
                                            when (dismissState.targetValue) {
                                                DismissValue.Default -> VolumeOrange
                                                else -> VolumeOffWhite
                                            }
                                        )

                                        val size by animateDpAsState(targetValue = if (dismissState.targetValue == DismissValue.Default) 24.dp else 48.dp)

                                        Box(
                                            Modifier
                                                .fillMaxSize()
                                                .background(backgroundColor)
                                                .padding(horizontal = Dp(20f)),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            Icon(
                                                Icons.Filled.Bookmark,
                                                contentDescription = "Unbookmark",
                                                modifier = Modifier.size(size),
                                                tint = iconColor
                                            )
                                        }
                                    },
                                    dismissContent = {
                                        CreateArticleRow(
                                            article = article,
                                            isABookmarkedArticle = true
                                        ) {
                                            onArticleClick(
                                                article,
                                                NavigationSource.BOOKMARK_ARTICLES
                                            )
                                        }
                                    })
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun BookmarksTopBar(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(start = 12.dp, top = 20.dp),
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "Bookmarks",
                fontFamily = notoserif,
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                textAlign = TextAlign.Left
            )
            Image(
                painter = painterResource(R.drawable.ic_period),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 3.dp, bottom = 10.dp)
                    .scale(1.05F)
            )
        }
        Spacer(Modifier.weight(1f, true))
        IconButton(onClick = onSettingsClick) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = Color(0xFF838383)
            )
        }
    }
}
