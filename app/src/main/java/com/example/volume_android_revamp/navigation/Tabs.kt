package com.example.volume_android_revamp.navigation

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.volume_android_revamp.HomeScreen
import com.example.volume_android_revamp.R
import com.example.volume_android_revamp.viewmodels.HomeTabViewModel

private val items = listOf(
    NavigationItem.Home,
    NavigationItem.Magazines,
    NavigationItem.Publications,
    NavigationItem.Bookmarks
)

@Composable
fun TabbedNavigationSetup(homeTabViewModel: HomeTabViewModel){
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        MainScreenNavigationConfigurations(
            navController = navController,
            homeTab = items[0],
            magazineTab = items[1] ,
            publicationsTab = items[2],
            bookmarksTab = items[3],
            homeTabViewModel = homeTabViewModel
        )
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomNavigation(
        backgroundColor = Color.White
    ) {
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) },
                selectedContentColor = Color(R.color.volumeOrange) ,
                unselectedContentColor = Color(R.color.gray),
                alwaysShowLabel = true,
                selected = false,
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
    homeTab: NavigationItem,
    magazineTab: NavigationItem,
    publicationsTab: NavigationItem,
    bookmarksTab: NavigationItem,
    homeTabViewModel: HomeTabViewModel
){

    NavHost(navController = navController, startDestination = homeTab.route){
        composable(homeTab.route){
            HomeScreen(homeTabViewModel)
        }
        composable(magazineTab.route){}
        composable(publicationsTab.route){}
        composable(bookmarksTab.route){}
    }
}
