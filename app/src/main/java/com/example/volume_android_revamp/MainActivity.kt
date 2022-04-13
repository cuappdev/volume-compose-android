package com.example.volume_android_revamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable

class MainActivity : AppCompatActivity() {
    private val items = listOf(
        NavigationItem.Home,
        NavigationItem.Magazines,
        NavigationItem.Publications,
        NavigationItem.Bookmarks
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            MainScreen()
        }
    }

    @Composable
    fun MainScreen(){
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) {
            MainScreenNavigationConfigurations(
                navController = navController,
                homeTab = items[0],
                magazineTab = items[1] ,
                publicationsTab = items[2],
                bookmarksTab = items[3]
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
            bookmarksTab: NavigationItem
    ){

        NavHost(navController = navController, startDestination = homeTab.route){
            composable(homeTab.route){}
            composable(magazineTab.route){}
            composable(publicationsTab.route){}
            composable(bookmarksTab.route){}
        }
    }
}