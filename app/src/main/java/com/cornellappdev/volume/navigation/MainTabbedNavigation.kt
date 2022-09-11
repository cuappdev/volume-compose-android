package com.cornellappdev.volume.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.volume.analytics.EventType
import com.cornellappdev.volume.analytics.VolumeEvent
import com.cornellappdev.volume.ui.screens.*
import com.cornellappdev.volume.ui.theme.DarkGray
import com.cornellappdev.volume.ui.theme.VolumeOrange

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TabbedNavigationSetup(onboardingCompleted: Boolean, notificationBundle: Bundle?) {
    val navController = rememberNavController()
    val (showBottomBar, setShowBottomBar) = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = expandVertically(expandFrom = Alignment.Bottom),
                exit = shrinkVertically()
            ) {
                BottomNavigationBar(navController, NavigationItem.bottomNavTabList)
            }
        }
    ) {
        MainScreenNavigationConfigurations(
            isOnboardingCompleted = onboardingCompleted,
            navController = navController,
            setShowBottomBar = setShowBottomBar,
        )
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun BottomNavigationBar(navController: NavHostController, tabItems: List<NavigationItem>) {
    BottomNavigation(
        elevation = 0.dp
    ) {
        val currentRoute = currentRoute(navController)
        tabItems.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = if (currentRoute == item.route) item.selectedIconId else item.unselectedIconId),
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) },
                selectedContentColor = VolumeOrange,
                unselectedContentColor = DarkGray,
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route)
                    }
                }
            )
        }
    }
}

@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController,
    setShowBottomBar: (Boolean) -> Unit,
    isOnboardingCompleted: Boolean,
) {
    // The starting destination switches to onboarding if it isn't completed.
    NavHost(
        navController = navController,
        startDestination = if (isOnboardingCompleted) Routes.HOME.route else Routes.ONBOARDING.route
    ) {
        composable(Routes.HOME.route) {
            setShowBottomBar(true)
            HomeScreen(
                onArticleClick = { article, navigationSource ->
                    navController.navigate("${Routes.OPEN_ARTICLE.route}/${article.id}/${navigationSource.name}")
                })
        }
        composable(Routes.ONBOARDING.route) {
            setShowBottomBar(false)
            OnboardingScreen(
                proceedHome = { navController.navigate(Routes.HOME.route) }
            )
        }
        // This route should be navigated with a valid publication ID, else the screen will not
        // populate.
        composable(
            "${Routes.INDIVIDUAL_PUBLICATION.route}/{publicationId}",
        ) {
            setShowBottomBar(true)
            IndividualPublicationScreen()
        }
        // This route should be navigated with a valid article ID.
        composable(
            "${Routes.OPEN_ARTICLE.route}/{articleId}/{navigationSourceName}",
        ) { backStackEntry ->
            setShowBottomBar(false)
            val navigationSourceName = backStackEntry.arguments?.getString("navigationSourceName")
            ArticleWebViewScreen(
                navigationSourceName = navigationSourceName,
                onArticleClose = { article, bookmarkStatus ->
                    VolumeEvent.logEvent(
                        EventType.ARTICLE,
                        VolumeEvent.CLOSE_ARTICLE,
                        id = article.id
                    )

                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "bookmarkStatus",
                        bookmarkStatus
                    )

                    navController.popBackStack()
                },
                seeMoreClicked = { article ->
                    navController.navigate("${Routes.INDIVIDUAL_PUBLICATION.route}/${article.publication.id}")
                }
            )
        }
        composable(Routes.MAGAZINES.route) {}
        composable(Routes.PUBLICATIONS.route) {
            setShowBottomBar(true)
            PublicationScreen(
                navController = navController,
                onPublicationClick =
                { publication ->
                    navController.navigate("${Routes.INDIVIDUAL_PUBLICATION.route}/${publication.id}")
                })
        }
        composable(Routes.BOOKMARKS.route) {
            setShowBottomBar(true)
            BookmarkScreen(
                navController = navController,
                onArticleClick = { article, navigationSource ->
                    navController.navigate("${Routes.OPEN_ARTICLE.route}/${article.id}/${navigationSource.name}")
                },
                onSettingsClick = { navController.navigate(Routes.SETTINGS.route) })
        }
        composable(Routes.SETTINGS.route) {
            setShowBottomBar(false)
            SettingsScreen {
                navController.navigate(Routes.ABOUT_US.route)
            }
        }
        composable(Routes.ABOUT_US.route) {
            setShowBottomBar(false)
            AboutUsScreen()
        }
    }
}
