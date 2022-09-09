package com.cornellappdev.volume

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.navigation.TabbedNavigationSetup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userPreferences: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Holds potential bundle of notification data payload
        val notificationBundle = intent.extras

        val onboardingCompleted = runBlocking {
            return@runBlocking userPreferences.fetchOnboardingCompleted()
        }

        setContent {
            TabbedNavigationSetup(onboardingCompleted, notificationBundle)
        }
    }
}
