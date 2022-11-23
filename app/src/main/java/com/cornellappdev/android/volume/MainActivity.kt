package com.cornellappdev.android.volume

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import com.cornellappdev.android.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.android.volume.navigation.TabbedNavigationSetup
import com.cornellappdev.android.volume.ui.theme.LightColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userPreferences: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onboardingCompleted = runBlocking {
            return@runBlocking userPreferences.fetchOnboardingCompleted()
        }

        val channelId = getString((R.string.default_notification_channel_id))
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            NotificationChannel(
                channelId,
                packageName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        setContent {
            MaterialTheme(
                colors = LightColors,
                content = { TabbedNavigationSetup(onboardingCompleted) }
            )
        }
    }
}
