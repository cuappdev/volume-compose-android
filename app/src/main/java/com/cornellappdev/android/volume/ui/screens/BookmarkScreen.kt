package com.cornellappdev.android.volume.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.cornellappdev.android.volume.ui.components.general.ErrorState
import com.cornellappdev.android.volume.ui.components.general.NothingToShowMessage
import com.cornellappdev.android.volume.ui.components.general.OldNothingToShowMessage
import com.cornellappdev.android.volume.ui.components.general.ShimmeringFlyer
import com.cornellappdev.android.volume.ui.components.general.SmallFlyer
import com.cornellappdev.android.volume.ui.components.general.VolumeHeaderText
import com.cornellappdev.android.volume.ui.components.general.VolumeLoading
import com.cornellappdev.android.volume.ui.states.ArticlesRetrievalState
import com.cornellappdev.android.volume.ui.states.FlyersRetrievalState
import com.cornellappdev.android.volume.ui.states.MagazinesRetrievalState
import com.cornellappdev.android.volume.ui.theme.VolumeOffWhite
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif
import com.cornellappdev.android.volume.ui.viewmodels.BookmarkViewModel
import com.cornellappdev.android.volume.util.BookmarkStatus
import com.cornellappdev.android.volume.util.FinalBookmarkStatus
import com.cornellappdev.android.volume.util.FlyerConstants

private const val TAG = "BookmarkScreen"

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun BookmarkScreen(
    bookmarkViewModel: BookmarkViewModel = hiltViewModel(),
    savedStateHandle: SavedStateHandle,
    onArticleClick: (Article, NavigationSource) -> Unit,
    onMagazineClick: (Magazine) -> Unit,
    onSettingsClick: () -> Unit,
    onOrganizationNameClick: (slug: String) -> Unit,
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
            TabbedBookmarksView(
                onArticleClick = onArticleClick,
                onMagazineClick = onMagazineClick,
                bookmarkUiState = bookmarkUiState,
                bookmarkViewModel = bookmarkViewModel,
                onOrganizationNameClick = onOrganizationNameClick
            )
        }
    })
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun TabbedBookmarksView(
    onArticleClick: (Article, NavigationSource) -> Unit,
    bookmarkUiState: BookmarkViewModel.BookmarkUiState,
    bookmarkViewModel: BookmarkViewModel,
    onMagazineClick: (Magazine) -> Unit,
    onOrganizationNameClick: (slug: String) -> Unit,
) {
    var tabIndex by remember { mutableStateOf(0) };

    val tabs = listOf("Flyers", "Articles", "Magazines")

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex, contentColor = VolumeOrange) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title, fontFamily = lato, fontSize = 18.sp) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    selectedContentColor = VolumeOrange,
                    unselectedContentColor = Color.Black,
                )
            }
        }
        when (tabIndex) {
            0 -> BookmarkedFlyersView(
                bookmarkUiState = bookmarkUiState,
                bookmarkViewModel = bookmarkViewModel,
                onOrganizationNameClick = onOrganizationNameClick,
            )

            1 -> BookmarkedArticlesView(
                onArticleClick = onArticleClick,
                bookmarkUiState = bookmarkUiState,
                bookmarkViewModel = bookmarkViewModel
            )

            2 -> BookmarkedMagazinesView(
                onMagazineClick = onMagazineClick,
                bookmarkUiState = bookmarkUiState
            )
        }
    }
}

@Composable
fun BookmarkedFlyersView(
    bookmarkUiState: BookmarkViewModel.BookmarkUiState,
    bookmarkViewModel: BookmarkViewModel,
    onOrganizationNameClick: (slug: String) -> Unit,
) {
    var upcomingSelectedIndex by remember { mutableStateOf(0) }
    var upcomingExpanded by remember { mutableStateOf(false) }
    var pastSelectedIndex by remember { mutableStateOf(0) }
    var pastExpanded by remember { mutableStateOf(false) }

    val tags: List<String> = FlyerConstants.CATEGORY_SLUGS.split(",")
    val formattedTags = FlyerConstants.FORMATTED_TAGS

    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Upcoming and dropdown menu
        item {
            Spacer(modifier = Modifier.height(40.dp))

            Row {
                VolumeHeaderText(text = "Upcoming", underline = R.drawable.ic_underline_upcoming)

                Spacer(modifier = Modifier.weight(1F))

                // Dropdown menu
                Column(modifier = Modifier.padding(end = 24.dp /* this is 16 because offset */)) {
                    Box(modifier = Modifier.drawWithContent {
                        drawContent()
                        drawRoundRect(
                            color = VolumeOrange,
                            style = Stroke(width = 1.5.dp.toPx()),
                            cornerRadius = CornerRadius(
                                x = 5.dp.toPx(),
                                y = 5.dp.toPx()
                            ),
                            size = Size(
                                width = 128.dp.toPx(),
                                height = 31.dp.toPx()
                            ),
                            topLeft = Offset(x = 8.dp.toPx(), y = 0.dp.toPx())
                        )
                    }) {
                        Row {
                            Text(
                                text = formattedTags[upcomingSelectedIndex],
                                color = VolumeOrange,
                                modifier = Modifier
                                    .clickable {
                                        upcomingExpanded = true
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .height(16.dp)
                                    .defaultMinSize(minWidth = 82.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_dropdown),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .clickable {
                                        upcomingExpanded = true
                                    }
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = upcomingExpanded,
                        onDismissRequest = { upcomingExpanded = false }) {
                        formattedTags.forEachIndexed { index, s ->
                            if (index != upcomingSelectedIndex) {
                                DropdownMenuItem(onClick = {
                                    upcomingSelectedIndex = index
                                    upcomingExpanded = false
                                    bookmarkViewModel.applyQuery(
                                        tags[upcomingSelectedIndex],
                                        isUpcoming = true
                                    )
                                }) {
                                    Text(
                                        text = s,
                                        color = VolumeOrange,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        // Upcoming flyers
        when (val upcomingState = bookmarkUiState.upcomingFlyersState) {
            FlyersRetrievalState.Loading -> {
                item {
                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth()
                    )
                }
                item {
                    LazyHorizontalGrid(
                        rows = GridCells.Fixed(3),
                        modifier = Modifier.height(308.dp)
                    ) {
                        items(9) {
                            ShimmeringFlyer()
                        }
                    }
                }
            }

            FlyersRetrievalState.Error -> {
                item {
                    ErrorState()
                }

            }

            is FlyersRetrievalState.Success -> {
                if (upcomingState.flyers.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(312.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            NothingToShowMessage(
                                title = "No bookmarked upcoming flyers",
                                message = "You can bookmark them from the trending page or the flyers page"
                            )
                        }
                    }
                } else {
                    item {
                        LazyHorizontalGrid(
                            rows = GridCells.Fixed(3),
                            modifier = Modifier.height(308.dp)
                        ) {
                            items(upcomingState.flyers) {
                                SmallFlyer(
                                    isExtraSmall = true,
                                    flyer = it,
                                    onOrganizationNameClick = onOrganizationNameClick
                                )
                            }
                        }
                    }
                }
            }
        }

        // Past and dropdown menu
        item {
            Spacer(modifier = Modifier.height(32.dp))

            Row {
                VolumeHeaderText(text = "Past Flyers", underline = R.drawable.ic_underline_upcoming)

                Spacer(modifier = Modifier.weight(1F))

                // Dropdown menu
                Column(modifier = Modifier.padding(end = 24.dp /* this is 16 because offset */)) {
                    Box(modifier = Modifier.drawWithContent {
                        drawContent()
                        drawRoundRect(
                            color = VolumeOrange,
                            style = Stroke(width = 1.5.dp.toPx()),
                            cornerRadius = CornerRadius(
                                x = 5.dp.toPx(),
                                y = 5.dp.toPx()
                            ),
                            size = Size(
                                width = 128.dp.toPx(),
                                height = 31.dp.toPx()
                            ),
                            topLeft = Offset(x = 8.dp.toPx(), y = 0.dp.toPx())
                        )
                    }) {
                        Row {
                            Text(
                                text = formattedTags[pastSelectedIndex],
                                color = VolumeOrange,
                                modifier = Modifier
                                    .clickable {
                                        pastExpanded = true
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .height(16.dp)
                                    .defaultMinSize(minWidth = 82.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_dropdown),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .clickable {
                                        pastExpanded = true
                                    }
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = pastExpanded,
                        onDismissRequest = { pastExpanded = false }) {
                        formattedTags.forEachIndexed { index, s ->
                            if (index != pastSelectedIndex) {
                                DropdownMenuItem(onClick = {
                                    pastSelectedIndex = index
                                    pastExpanded = false
                                    bookmarkViewModel.applyQuery(
                                        tags[pastSelectedIndex],
                                        isUpcoming = false
                                    )
                                }) {
                                    Text(
                                        text = s,
                                        color = VolumeOrange,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        // Past flyers
        when (val pastState = bookmarkUiState.pastFlyersState) {
            FlyersRetrievalState.Error -> {
                item {
                    ErrorState()
                }
            }

            FlyersRetrievalState.Loading -> {
                item {
                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth()
                    )
                }
                items(9) {
                    ShimmeringFlyer()
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth()
                    )
                }
            }

            is FlyersRetrievalState.Success -> {
                if (pastState.flyers.isEmpty()) {
                    item {
                        NothingToShowMessage(
                            title = "You have no past flyers",
                            message = "Your expired flyers will show up here"
                        )
                    }
                } else {
                    item {
                        Spacer(
                            modifier = Modifier
                                .height(8.dp)
                                .fillMaxWidth()
                        )
                    }
                    items(pastState.flyers) {
                        SmallFlyer(
                            isExtraSmall = false,
                            flyer = it,
                            showTag = false,
                            onOrganizationNameClick = onOrganizationNameClick
                        )
                        Spacer(
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun BookmarkedMagazinesView(
    bookmarkUiState: BookmarkViewModel.BookmarkUiState,
    onMagazineClick: (Magazine) -> Unit,
) {
    when (val magazinesState = bookmarkUiState.magazinesState) {
        MagazinesRetrievalState.Loading -> {
            Box(modifier = Modifier.padding(top = 20.dp)) {
                VolumeLoading()
            }
        }

        MagazinesRetrievalState.Error -> {
            ErrorState()
        }

        is MagazinesRetrievalState.Success -> {
            if (magazinesState.magazines.isEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    OldNothingToShowMessage(message = "You have no saved magazines.")
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(magazinesState.magazines) {
                        CreateMagazineColumn(
                            magazine = it,
                            onMagazineClick = onMagazineClick,
                            isBookmarked = true
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookmarkedArticlesView(
    onArticleClick: (Article, NavigationSource) -> Unit,
    bookmarkUiState: BookmarkViewModel.BookmarkUiState,
    bookmarkViewModel: BookmarkViewModel,
) {
    when (val articleState = bookmarkUiState.articlesState) {
        ArticlesRetrievalState.Loading -> {
            Box(modifier = Modifier.padding(top = 20.dp)) {
                VolumeLoading()
            }
        }

        ArticlesRetrievalState.Error -> {
            ErrorState()
        }

        is ArticlesRetrievalState.Success -> {
            Column {
                if (articleState.articles.isEmpty()) {
                    OldNothingToShowMessage(message = "You have no saved articles.")
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
fun BookmarkedFlyersScreen(
    bookmarkUiState: BookmarkViewModel.BookmarkUiState,
    bookmarkViewModel: BookmarkViewModel,
) {

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
