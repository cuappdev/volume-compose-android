package com.cornellappdev.android.volume.navigation

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.cornellappdev.android.volume.data.models.Magazine
import com.cornellappdev.android.volume.ui.screens.*
import com.cornellappdev.android.volume.ui.theme.DarkGray
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@RequiresApi(Build.VERSION_CODES.P)
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

        "${Routes.OPEN_MAGAZINE.route}/{magazineId}/{navigationSourceName}" -> {
            showBottomBar.value = false
        }

        "${Routes.SEARCH.route}/{tabIndex}" -> {
            showBottomBar.value = true
        }

        Routes.READS.route -> {
            showBottomBar.value = true
        }

        Routes.FLYERS.route -> {
            showBottomBar.value = true
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

@RequiresApi(Build.VERSION_CODES.P)
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
            TrendingScreen(onArticleClick = { article, navigationSource ->
                FirstTimeShown.firstTimeShown = false
                navController.navigate("${Routes.OPEN_ARTICLE.route}/${article.id}/${navigationSource}")
            }, onMagazineClick = { magazine ->
                navController.navigate("${Routes.OPEN_MAGAZINE.route}/${magazine.id}/${Routes.HOME.route}")
            })
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
            }, onMagazineClick = { magazine ->
                navController.navigate("${Routes.OPEN_MAGAZINE.route}/${magazine.id}/${Routes.INDIVIDUAL_PUBLICATION.route}")
            })
        }

        // This route should be navigated with a valid organization slug, else the screen will not
        // populate.
        composable("${Routes.INDIVIDUAL_ORGANIZATION.route}/{organizationSlug}") {
            IndividualOrganizationScreen()
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
        composable(route = Routes.READS.route,
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
            }) {
            ReadsScreen(
                onMagazineClick = { magazine: Magazine ->
                    navController.navigate("${Routes.OPEN_MAGAZINE.route}/${magazine.id}/${Routes.READS.route}")
                },
                onArticleClick = { article, navigationSource ->
                    FirstTimeShown.firstTimeShown = false
                    navController.navigate("${Routes.OPEN_ARTICLE.route}/${article.id}/${navigationSource.name}")
                },
                showBottomBar = showBottomBar,
                onPublicationClick =
                { publication ->
                    navController.navigate("${Routes.INDIVIDUAL_PUBLICATION.route}/${publication.slug}")
                },
                onSearchClick = {
                    navController.navigate("${Routes.SEARCH.route}/$it")
                },
                onOrganizationClick = {
                    TODO()
                }
            )
        }
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
            }) { entry ->
            val navSource = entry.arguments?.getString("navigationSourceName") ?: Routes.READS.route

            IndividualMagazineScreen(
                magazineId = entry.arguments?.getString("magazineId") ?: "",
                navController = navController,
                navSource = navSource
            )
        }

        composable(Routes.FLYERS.route) {
            FlyersScreen(onSearchClick = {
                navController.navigate("${Routes.SEARCH.route}/$it")
            })
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
                onSettingsClick = { navController.navigate(Routes.SETTINGS.route) },
                onMagazineClick = { magazine ->
                    navController.navigate("${Routes.OPEN_MAGAZINE.route}/${magazine.id}/${Routes.BOOKMARKS.route}")
                })
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
            SettingsScreen(
                onAboutUsClicked = {
                    navController.navigate(Routes.ABOUT_US.route)
                },
                onOrganizationLoginClicked = { navController.navigate(Routes.ORGANIZATION_LOGIN.route) })
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
        composable(route = "${Routes.SEARCH.route}/{tabIndex}",
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
            }) { entry ->
            val tabIndex = entry.arguments?.getString("tabIndex")?.toInt()

            SearchScreen(onArticleClick = { article, navigationSource ->
                FirstTimeShown.firstTimeShown = false
                navController.navigate("${Routes.OPEN_ARTICLE.route}/${article.id}/${navigationSource.name}")
            }, onMagazineClick = { magazine ->
                navController.navigate("${Routes.OPEN_MAGAZINE.route}/${magazine.id}/${Routes.SEARCH.route}")
            }, defaultTab = tabIndex ?: 0)
        }
        composable(route = Routes.ORGANIZATION_LOGIN.route,
            enterTransition = {
                fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 1000)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 1000)
                )
            }) {
            OrganizationsLoginScreen({ navController.navigate("${Routes.ORGANIZATION_HOME.route}/$it") })
        }
        composable(route = "${Routes.ORGANIZATION_HOME.route}/{organizationSlug}") { entry ->
            val orgSlug = entry.arguments?.getString("organizationSlug")
            OrganizationHomeScreen(
                organizationSlug = orgSlug ?: "",
                onFlyerUploadClicked = { navController.navigate("${Routes.UPLOAD_FLYER.route}/$orgSlug") },
                onFlyerEditClicked = { flyerId ->
                    navController.navigate(
                        "${Routes.UPLOAD_FLYER.route}/$orgSlug/$flyerId"
                    )
                }
            )
        }
        composable(
            route = "${Routes.UPLOAD_FLYER.route}/{organizationSlug}/{flyerId}",
        ) { entry ->
            val orgSlug = entry.arguments?.getString("organizationSlug")
            val flyerId = entry.arguments?.getString("flyerId")
            FlyerUploadScreen(
                organizationSlug = orgSlug ?: "",
                onFlyerChangeSuccess = { navController.navigate("${Routes.ORGANIZATION_HOME.route}/$orgSlug") },
                editingFlyerId = flyerId
            )
        }
        composable(
            route = "${Routes.UPLOAD_FLYER.route}/{organizationSlug}",
        ) { entry ->
            val orgSlug = entry.arguments?.getString("organizationSlug")
            val flyerId: String? = entry.arguments?.getString("flyerId")
            FlyerUploadScreen(
                organizationSlug = orgSlug ?: "",
                onFlyerChangeSuccess = { navController.navigate("${Routes.ORGANIZATION_HOME.route}/$orgSlug") },
                editingFlyerId = flyerId
            )
        }
        composable(route = Routes.FLYER_SUCCESS.route, enterTransition = {
            fadeIn(
                initialAlpha = 0f,
                animationSpec = tween(durationMillis = 1000)
            )
        },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(durationMillis = 1000)
                )
            }) {
            FlyerSuccessScreen {
                navController.navigate(Routes.FLYERS.route)
            }
        }
    }
}
