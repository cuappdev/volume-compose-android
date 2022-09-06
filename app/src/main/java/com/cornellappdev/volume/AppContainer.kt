package com.cornellappdev.volume

import androidx.datastore.core.DataStore
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository

/**
 * Container for objects used throughout whole application. Any shared references/utility
 * classes should be put here.
 *
 * @param userPreferencesStore
 */
class AppContainer(userPreferencesStore: DataStore<UserPreferences>) {
    val userPreferencesRepository = UserPreferencesRepository(userPreferencesStore)
}
