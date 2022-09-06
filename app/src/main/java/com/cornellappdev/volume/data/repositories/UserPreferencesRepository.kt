package com.cornellappdev.volume.data.repositories

import androidx.datastore.core.DataStore
import com.cornellappdev.volume.UserPreferences
import com.cornellappdev.volume.util.userPreferencesStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserPreferencesRepository(
    private val userPreferencesStore: DataStore<UserPreferences>,
) {
    private val userPreferencesFlow: Flow<UserPreferences> = userPreferencesStore.data

    suspend fun updateOnboardingCompleted(completed: Boolean) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setOnboardingCompleted(completed).build()
        }
    }

    suspend fun updateUuid(uuid: String) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setUuid(uuid).build()
        }
    }

    suspend fun fetchOnboardingCompleted(): Boolean =
        userPreferencesFlow.first().onboardingCompleted

    suspend fun fetchUuid(): String =
        userPreferencesFlow.first().uuid

    suspend fun fetchDeviceToken(): String =
        userPreferencesFlow.first().deviceToken
}
