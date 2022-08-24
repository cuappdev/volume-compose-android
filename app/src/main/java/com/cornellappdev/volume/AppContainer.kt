package com.cornellappdev.volume

import androidx.datastore.core.DataStore
import com.cornellappdev.volume.data.NetworkingApi
import com.cornellappdev.volume.data.repositories.ArticleRepository
import com.cornellappdev.volume.data.repositories.PublicationRepository
import com.cornellappdev.volume.data.repositories.UserPreferencesRepository
import com.cornellappdev.volume.data.repositories.UserRepository

// Container of objects shared across the whole app
class AppContainer(userPreferencesStore: DataStore<UserPreferences>) {
    private val networkingApi = NetworkingApi()

    val articleRepository = ArticleRepository(networkingApi)
    val publicationRepository = PublicationRepository(networkingApi)
    val userRepository = UserRepository(networkingApi)
    val userPreferencesRepository = UserPreferencesRepository(userPreferencesStore)
}
