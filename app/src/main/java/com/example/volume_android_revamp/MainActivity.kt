package com.example.volume_android_revamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.volume_android_revamp.navigation.NavigationItem
import com.example.volume_android_revamp.navigation.TabbedNavigationSetup
import com.example.volume_android_revamp.networking.DataRepository
import com.example.volume_android_revamp.networking.NetworkingApi
import com.example.volume_android_revamp.viewmodels.HomeTabViewModel


class MainActivity : AppCompatActivity() {
    private val webService = NetworkingApi()
    private val repository = DataRepository(webService)
    private val items = listOf(
        NavigationItem.Home,
        NavigationItem.Magazines,
        NavigationItem.Publications,
        NavigationItem.Bookmarks
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            TabbedNavigationSetup(HomeTabViewModel(repository))
        }
    }

}