package com.cornellappdev.volume.data.repositories

import androidx.datastore.core.DataStore
import com.cornellappdev.volume.UserPreferences
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

    suspend fun updateFollowedPublications(followedPublications: List<String>) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().clearFollowedPublications()
                .addAllFollowedPublications(followedPublications).build()
        }
    }

    suspend fun fetchOnboardingCompleted(): Boolean =
        userPreferencesFlow.first().onboardingCompleted

    suspend fun fetchUuid(): String =
        userPreferencesFlow.first().uuid

    suspend fun fetchFollowedPublications(): List<String> =
        userPreferencesFlow.first().followedPublicationsList

    suspend fun fetchDeviceToken(): String =
        userPreferencesFlow.first().deviceToken
}
