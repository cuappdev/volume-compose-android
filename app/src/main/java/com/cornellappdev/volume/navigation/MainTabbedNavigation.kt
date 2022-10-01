package com.cornellappdev.volume.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.cornellappdev.volume.analytics.EventType
import com.cornellappdev.volume.analytics.NavigationSource
import com.cornellappdev.volume.analytics.VolumeEvent
import com.cornellappdev.volume.ui.screens.*
import com.cornellappdev.volume.ui.theme.DarkGray
import com.cornellappdev.volume.ui.theme.VolumeOrange

@Composable
fun TabbedNavigationSetup(onboardingCompleted: Boolean) {
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
    ) { innerPadding ->
        MainScreenNavigationConfigurations(
            modifier = Modifier.padding(innerPadding),
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
    modifier: Modifier = Modifier,
    navController: NavHostController,
    setShowBottomBar: (Boolean) -> Unit,
    isOnboardingCompleted: Boolean,
) {
    // The starting destination switches to onboarding if it isn't completed.
    NavHost(
        modifier = modifier,
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
        composable(route = Routes.WEEKLY_DEBRIEF.route, deepLinks = listOf(
            navDeepLink { uriPattern = "volume://${Routes.WEEKLY_DEBRIEF.route}" }
        )) {
            setShowBottomBar(true)
            // I believe WeeklyDebrief is a bottom sheet attached to the HomeScreen

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
            "${Routes.INDIVIDUAL_PUBLICATION.route}/{publicationSlug}",
        ) {
            setShowBottomBar(true)
            IndividualPublicationScreen()
        }
        // This route should be navigated with a valid article ID.
        composable(
            route = "${Routes.OPEN_ARTICLE.route}/{articleId}/{navigationSourceName}",
            deepLinks = listOf(
                navDeepLink { uriPattern = "volume://${Routes.OPEN_ARTICLE.route}/{articleId}" }
            )
        ) { backStackEntry ->
            setShowBottomBar(false)
            val articleId = backStackEntry.arguments?.getString("articleId")!!
            val navigationSourceName = backStackEntry.arguments?.getString("navigationSourceName")
                ?: NavigationSource.UNSPECIFIED.name
            ArticleWebViewScreen(
                navigationSourceName = navigationSourceName,
                onArticleClose = { bookmarkStatus ->
                    VolumeEvent.logEvent(
                        EventType.ARTICLE,
                        VolumeEvent.CLOSE_ARTICLE,
                        id = articleId
                    )

                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "bookmarkStatus",
                        bookmarkStatus
                    )

                    navController.popBackStack()
                },
                seeMoreClicked = { article ->
                    navController.navigate("${Routes.INDIVIDUAL_PUBLICATION.route}/${article.publication.slug}")
                }
            )
        }
        composable(Routes.MAGAZINES.route) {}
        composable(Routes.PUBLICATIONS.route) {
            setShowBottomBar(true)
            PublicationsScreen(
                navController = navController,
                onPublicationClick =
                { publication ->
                    navController.navigate("${Routes.INDIVIDUAL_PUBLICATION.route}/${publication.slug}")
                })
        }
        composable(Routes.BOOKMARKS.route) {
            setShowBottomBar(true)
            BookmarkScreen(
                savedStateHandle = it.savedStateHandle,
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