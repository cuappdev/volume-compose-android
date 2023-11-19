package com.cornellappdev.android.volume.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.volume.R
import com.cornellappdev.android.volume.analytics.NavigationSource
import com.cornellappdev.android.volume.data.models.Article
import com.cornellappdev.android.volume.data.models.Magazine
import com.cornellappdev.android.volume.data.models.Organization
import com.cornellappdev.android.volume.ui.components.general.SearchBar
import com.cornellappdev.android.volume.ui.components.general.VolumePeriod
import com.cornellappdev.android.volume.ui.theme.VolumeOffWhite
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif
import kotlinx.coroutines.launch

private const val TAG = "ReadsScreen"

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ReadsScreen(
    onArticleClick: (Article, NavigationSource) -> Unit,
    showBottomBar: MutableState<Boolean>,
    onMagazineClick: (Magazine) -> Unit,
    onPublicationClick: (String) -> Unit,
    onSearchClick: (Int) -> Unit,
    onOrganizationClick: (Organization) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    PublicationsMenu(onPublicationClick = onPublicationClick)
                }
            },
            content = { innerPadding ->
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Column(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                        // Reads title
                        Row(modifier = Modifier.padding(top = 20.dp)) {
                            Text(
                                modifier = Modifier.padding(start = 15.dp),
                                text = "Reads",
                                fontFamily = notoserif,
                                fontWeight = FontWeight.Medium,
                                fontSize = 28.sp,
                                textAlign = TextAlign.Left
                            )
                            VolumePeriod(modifier = Modifier.padding(start = 10.dp, top = 23.dp))
                        }

                        // Articles and Magazines Tab tabs.
                        var tabIndex by remember { mutableStateOf(0) };
                        val tabs = listOf("Articles", "Magazines")
                        Row {
                            Spacer(modifier = Modifier.weight(1F))
                            Column(modifier = Modifier.width(screenWidthDp * 3 / 4)) {

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
                            Spacer(modifier = Modifier.weight(8F))
                            Image(painter = painterResource(id = R.drawable.ic_hamburger),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(top = 20.dp, end = 32.dp)
                                    .clickable {
                                        coroutineScope.launch {
                                            scaffoldState.drawerState.open()
                                        }
                                    })
                            Spacer(modifier = Modifier.weight(2F))
                        }
                        Box(modifier = Modifier.padding(start = 16.dp)) {
                            SearchBar(value = "",
                                onValueChange = {},
                                modifier = Modifier
                                    .padding(end = 16.dp, top = 16.dp),
                                onClick = { onSearchClick(tabIndex) })
                        }


                        when (tabIndex) {
                            0 -> {
                                ArticlesViewer(
                                    onArticleClick = onArticleClick,
                                    showBottomBar = showBottomBar
                                )
                            }

                            1 -> {
                                MagazinesViewer(onMagazineClick = onMagazineClick)
                            }
                        }
                    }
                }
            }
        )
    }
}
