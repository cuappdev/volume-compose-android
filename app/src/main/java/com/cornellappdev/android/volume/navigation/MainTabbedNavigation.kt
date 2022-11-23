package com.cornellappdev.android.volume.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navDeepLink
import com.cornellappdev.android.volume.analytics.EventType
import com.cornellappdev.android.volume.analytics.NavigationSource
import com.cornellappdev.android.volume.analytics.VolumeEvent
import com.cornellappdev.android.volume.ui.screens.*
import com.cornellappdev.android.volume.ui.theme.DarkGray
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TabbedNavigationSetup(onboardingCompleted: Boolean) {
    val navController = rememberAnimatedNavController()
    val showBottomBar = rememberSaveable { mutableStateOf(false) }

    // Subscribe to navBackStackEntry, required to get current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    when (navBackStackEntry?.destination?.route) {
        Routes.HOME.route -> {
            showBottomBar.value = true
        }
        Routes.WEEKLY_DEBRIEF.route -> {
            showBottomBar.value = true
        }
        Routes.ONBOARDING.route -> {
            showBottomBar.value = false
        }
        Routes.PUBLICATIONS.route -> {
            showBottomBar.value = true
        }
        Routes.BOOKMARKS.route -> {
            showBottomBar.value = true
        }
        Routes.SETTINGS.route -> {
            showBottomBar.value = false
        }
        Routes.ABOUT_US.route -> {
            showBottomBar.value = false
        }
        "${Routes.OPEN_ARTICLE.route}/{articleId}/{navigationSourceName}" -> {
            showBottomBar.value = false
        }
        "${Routes.INDIVIDUAL_PUBLICATION.route}/{publicationSlug}" -> {
            showBottomBar.value = true
        }
        "${Routes.OPEN_ARTICLE.route}/{articleId}/{navigationSourceName}" -> {
            showBottomBar.value = false
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedContent(
                targetState = showBottomBar.value,
                transitionSpec = {
                    expandVertically(
                        animationSpec = tween(durationMillis = 2500),
                        expandFrom = Alignment.Bottom
                    ) with shrinkVertically(animationSpec = tween(durationMillis = 2500))
                }
            ) { state ->
                if (state) {
                    BottomNavigationBar(navController, NavigationItem.bottomNavTabList)
                }
            }
        }
    ) { innerPadding ->
        MainScreenNavigationConfigurations(
            modifier = Modifier.padding(innerPadding),
            isOnboardingCompleted = onboardingCompleted,
            navController = navController,
            showBottomBar = showBottomBar
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
                        painter = painterResource(
                            id = if (item.selectedRoutes.contains(currentRoute)) item.selectedIconId else item.unselectedIconId
                        ),
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title, overflow = TextOverflow.Ellipsis, maxLines = 1) },
                selectedContentColor = VolumeOrange,
                unselectedContentColor = DarkGray,
                alwaysShowLabel = true,
                selected = item.selectedRoutes.contains(currentRoute),
                onClick = {
                    if (currentRoute != item.route) {
                        FirstTimeShown.firstTimeShown = false
                        navController.navigate(item.route)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun MainScreenNavigationConfigurations(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    isOnboardingCompleted: Boolean,
    showBottomBar: MutableState<Boolean>,
) {
    // The starting destination switches to onboarding if it isn't completed.
    AnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = if (isOnboardingCompleted) Routes.HOME.route else Routes.ONBOARDING.route
    ) {
        composable(Routes.HOME.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 2500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 2500)
                )
            }) {
            HomeScreen(
                onArticleClick = { article, navigationSource ->
                    FirstTimeShown.firstTimeShown = false
                    navController.navigate("${Routes.OPEN_ARTICLE.route}/${article.id}/${navigationSource.name}")
                },
                showBottomBar = showBottomBar,
            )
        }
        composable(route = Routes.WEEKLY_DEBRIEF.route, deepLinks = listOf(
            navDeepLink { uriPattern = "volume://${Routes.WEEKLY_DEBRIEF.route}" }
        )) {
            // I believe WeeklyDebrief is a bottom sheet attached to the HomeScreen

        }
        composable(Routes.ONBOARDING.route) {
            OnboardingScreen(
                proceedHome = { navController.navigate(Routes.HOME.route) }
            )
        }
        // This route should be navigated with a valid publication slug, else the screen will not
        // populate.
        composable(
            "${Routes.INDIVIDUAL_PUBLICATION.route}/{publicationSlug}",
        ) {
            IndividualPublicationScreen(onArticleClick = { article, navigationSource ->
                navController.navigate("${Routes.OPEN_ARTICLE.route}/${article.id}/${navigationSource.name}")
            })
        }
        // This route should be navigated with a valid article id.
        composable(
            route = "${Routes.OPEN_ARTICLE.route}/{articleId}/{navigationSourceName}",
            deepLinks = listOf(
                navDeepLink { uriPattern = "volume://${Routes.OPEN_ARTICLE.route}/{articleId}" }
            ),
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 1500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 1500)
                )
            }
        ) { backStackEntry ->
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
        composable(
            route = "${Routes.OPEN_MAGAZINE.route}/{magazineId}/{navigationSourceName}",
            deepLinks = listOf(
                navDeepLink { uriPattern = "volume://${Routes.OPEN_MAGAZINE.route}/{magazineId}" }
            ),
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 1500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 1500)
                )
            }) {}
        composable(Routes.PUBLICATIONS.route) {
            PublicationsScreen(
                onPublicationClick =
                { publication ->
                    navController.navigate("${Routes.INDIVIDUAL_PUBLICATION.route}/${publication.slug}")
                }
            )

        }
        composable(
            Routes.BOOKMARKS.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 2500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 2500)
                )
            }) {
            BookmarkScreen(
                savedStateHandle = it.savedStateHandle,
                onArticleClick = { article, navigationSource ->
                    navController.navigate("${Routes.OPEN_ARTICLE.route}/${article.id}/${navigationSource.name}")
                },
                onSettingsClick = { navController.navigate(Routes.SETTINGS.route) })
        }
        composable(Routes.SETTINGS.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 2500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 2500)
                )
            }) {
            SettingsScreen {
                navController.navigate(Routes.ABOUT_US.route)
            }
        }
        composable(Routes.ABOUT_US.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 2500)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 2500)
                )
            }) {
            AboutUsScreen()
        }
    }
}
