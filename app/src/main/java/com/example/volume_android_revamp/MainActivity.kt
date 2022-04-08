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
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable

class MainActivity : AppCompatActivity() {
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
            VolumeTitle()

        }
    }

    @Composable
    fun VolumeTitle(){
        Row {
            Image(painter = painterResource(R.drawable.volume_title), contentDescription = getString(R.string.volume_title))
        }
    }

    @Composable
    private fun currentRoute(navController: NavHostController): String? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route
    }

    @Composable
    fun BottomNavigationBar(navController: NavHostController) {
        val items = listOf(
            NavigationItem.Home,
            NavigationItem.Magazines,
            NavigationItem.Publications,
            NavigationItem.Bookmarks
        )
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
        NavHost(navController = navController, startDestination = homeTab.route) {
            composable(homeTab.route){
                HomeTabController()
            }
            composable(magazineTab.route){
                MagazineTabController()
            }
            composable(publicationsTab.route){
                PublicationsTabController()
            }
            composable(bookmarksTab.route){
                BookmarksTabController()
            }
        }
    }
}