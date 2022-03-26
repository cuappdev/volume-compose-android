package com.example.volume_android_revamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
//import androidx.navigation.Navigation
//import androidx.navigation.compose.rememberNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContent{
             MainScreen()
         }
    }

    @Composable
    fun MainScreen(){
//        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavigationBar() }
        ) {
            VolumeTitle()
//            Navigation(navController)
        }
    }

    @Composable
    fun VolumeTitle(){
        Row {
            Image(painter = painterResource(R.drawable.volume_title), contentDescription = getString(R.string.volume_title))
        }
    }

    @Composable
    fun BottomNavigationBar() {
        val items = listOf(
            NavigationItem.Home,
            NavigationItem.Magazines,
            NavigationItem.Publications,
            NavigationItem.Bookmarks
        )
        BottomNavigation(
            backgroundColor = Color.White
        ) {
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
                        /* Add code later */
                    }
                )
            }
        }
    }
}