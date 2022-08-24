package com.cornellappdev.volume

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cornellappdev.volume.navigation.TabbedNavigationSetup
import com.cornellappdev.volume.util.userPreferencesStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = AppContainer(userPreferencesStore = userPreferencesStore)

        setContent {
            TabbedNavigationSetup(appContainer)
        }
    }
}
