package com.cornellappdev.volume.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cornellappdev.volume.AppContainer
import com.cornellappdev.volume.ui.screens.HomeScreen
import com.cornellappdev.volume.ui.screens.OnboardingScreen
import com.cornellappdev.volume.ui.theme.DarkGray
import com.cornellappdev.volume.ui.theme.VolumeOrange
import com.cornellappdev.volume.ui.viewmodels.HomeTabViewModel
import com.cornellappdev.volume.ui.viewmodels.NavigationViewModel
import com.cornellappdev.volume.ui.viewmodels.OnboardingViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TabbedNavigationSetup(appContainer: AppContainer) {
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
            navigationViewModel = NavigationViewModel(appContainer.userPreferencesRepository),
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
    navigationViewModel: NavigationViewModel,
    setShowBottomBar: (Boolean) -> Unit,
) {
    val onboardingCompletedState = navigationViewModel.onboardingState.collectAsState().value

    when (onboardingCompletedState.state) {
        NavigationViewModel.OnboardingState.Pending -> {
            // Loads a progress animation while fetching is happening
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.size(200.dp), color = VolumeOrange)
            }
        }
        is NavigationViewModel.OnboardingState.Success -> {
            val onboardingIsCompleted = onboardingCompletedState.state.onboardingCompleted
            NavHost(
                navController = navController,
                startDestination = if (onboardingIsCompleted) Routes.HOME.route else Routes.ONBOARDING.route
            ) {
                composable(Routes.HOME.route) {
                    setShowBottomBar(true)
                    HomeScreen(HomeTabViewModel(articleRepository = appContainer.articleRepository))
                }
                composable(Routes.ONBOARDING.route) {
                    setShowBottomBar(false)
                    OnboardingScreen(
                        onboardingViewModel = OnboardingViewModel(
                            userRepository = appContainer.userRepository,
                            userPreferencesRepository = appContainer.userPreferencesRepository,
                            publicationRepository = appContainer.publicationRepository
                        )
                    ) {
                        navController.navigate(Routes.HOME.route)
                    }
                }
                composable(Routes.MAGAZINES.route) {}
                composable(Routes.PUBLICATIONS.route) {}
                composable(Routes.BOOKMARKS.route) {}
            }
        }
    }
}
