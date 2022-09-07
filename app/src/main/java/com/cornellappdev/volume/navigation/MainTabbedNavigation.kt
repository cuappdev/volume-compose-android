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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cornellappdev.volume.AppContainer
import com.cornellappdev.volume.ui.screens.*
import com.cornellappdev.volume.ui.theme.DarkGray
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.viewmodels.*
import kotlinx.coroutines.runBlocking

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TabbedNavigationSetup(appContainer: AppContainer, notificationBundle: Bundle?) {
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
            appContainer = appContainer,
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
        backgroundColor = Color.White
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
    appContainer: AppContainer,
    setShowBottomBar: (Boolean) -> Unit,
) {
    val onboardingCompleted = runBlocking {
        return@runBlocking appContainer.userPreferencesRepository.fetchOnboardingCompleted()
    }

    // The starting destination switches to onboarding if it isn't completed.
    NavHost(
        navController = navController,
        startDestination = if (onboardingCompleted) Routes.HOME.route else Routes.ONBOARDING.route
    ) {
        composable(Routes.HOME.route) {
            setShowBottomBar(true)
            HomeScreen(
                homeTabViewModel = viewModel(factory = HomeTabViewModel.Factory(appContainer.userPreferencesRepository))
            ) { article ->
                navController.navigate("${Routes.OPEN_ARTICLE}/${article.id}")
            }
        }
        composable(Routes.ONBOARDING.route) {
            setShowBottomBar(false)
            OnboardingScreen(
                onboardingViewModel = viewModel(
                    factory = OnboardingViewModel.Factory(
                        appContainer.userPreferencesRepository
                    )
                )
            ) {
                navController.navigate(Routes.HOME.route)
            }
        }
        // This route should be navigated with a valid publication ID, else the screen will not
        // populate.
        composable(
            "${Routes.INDIVIDUAL_PUBLICATION}/{publicationId}",
            arguments = listOf(navArgument("publicationId") { type = NavType.StringType })
        ) { backStackEntry ->
            setShowBottomBar(true)
            val publicationId = backStackEntry.arguments?.getString("publicationId")
            IndividualPublicationScreen(individualPublicationViewModel = viewModel(
                factory = publicationId?.let {
                    IndividualPublicationViewModel.Factory(
                        publicationId = it,
                        userPreferencesRepository = appContainer.userPreferencesRepository
                    )
                }
            ))
        }
        // This route should be navigated with a valid article ID.
        composable(
            "${Routes.OPEN_ARTICLE}/{articleId}",
            arguments = listOf(navArgument("articleId") { type = NavType.StringType })
        ) { backStackEntry ->
            setShowBottomBar(false)
            val articleId = backStackEntry.arguments?.getString("articleId")
            ArticleWebViewScreen(
                articleWebViewModel = viewModel(factory = articleId?.let {
                    ArticleWebViewModel.Factory(
                        articleId = it,
                        userPreferencesRepository = appContainer.userPreferencesRepository
                    )
                }
                )) { article ->
                navController.navigate("${Routes.INDIVIDUAL_PUBLICATION}/${article.publication.id}")
            }
        }
        composable(Routes.MAGAZINES.route) {}
        composable(Routes.PUBLICATIONS.route) {
            setShowBottomBar(true)
            PublicationScreen(
                publicationTabViewModel = viewModel(
                    factory = PublicationTabViewModel.Factory(
                        userPreferencesRepository = appContainer.userPreferencesRepository
                    )
                ),
                navController = navController
            ) { publication ->
                navController.navigate("${Routes.INDIVIDUAL_PUBLICATION}/${publication.id}")
            }
        }
        composable(Routes.BOOKMARKS.route) {}
    }
}
