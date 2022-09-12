package com.cornellappdev.volume.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.cornellappdev.volume.UserPreferences
import com.cornellappdev.volume.util.userPreferencesStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(@ApplicationContext context: Context): DataStore<UserPreferences> =
        context.userPreferencesStore
}
