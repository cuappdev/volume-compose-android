package com.cornellappdev.android.volume.data.repositories

import androidx.datastore.core.DataStore
import com.cornellappdev.android.volume.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val userPreferencesStore: DataStore<UserPreferences>,
) {
    companion object {
        const val MAX_SHOUTOUT = 5
    }

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

    suspend fun updateNotificationFlowStatus(value: Boolean) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setNotificationFlowCompleted(value).build()
        }
    }

    suspend fun addBookmarkedArticle(articleId: String) {
        userPreferencesStore.updateData { currentPreferences ->
            val currentBookmarks = currentPreferences.bookmarkedArticlesList.toHashSet()
            if (!currentBookmarks.contains(articleId)) {
                currentPreferences.toBuilder().addBookmarkedArticles(articleId).build()
            } else {
                currentPreferences
            }
        }
    }

    suspend fun addBookmarkedMagazine(magazineId: String) {
        userPreferencesStore.updateData { currentPreferences ->
            val currentBookmarks = currentPreferences.bookmarkedMagazinesList.toHashSet()
            if (!currentBookmarks.contains(magazineId)) {
                currentPreferences.toBuilder().addBookmarkedMagazines(magazineId).build()
            } else {
                currentPreferences
            }
        }
    }

    suspend fun addBookmarkedFlyer(flyerId: String) {
        userPreferencesStore.updateData { currentPreferences ->
            val currentBookmarks = currentPreferences.bookmarkedFlyersList.toHashSet()
            if (!currentBookmarks.contains(flyerId)) {
                currentPreferences.toBuilder().addBookmarkedFlyers(flyerId).build()
            } else {
                currentPreferences
            }
        }
    }

    suspend fun removeBookmarkedFlyer(flyerId: String) {
        userPreferencesStore.updateData { currentPreferences ->
            val currentBookmarks = currentPreferences.bookmarkedFlyersList.toHashSet()
            if (currentBookmarks.contains(flyerId)) {
                currentBookmarks.remove(flyerId)
                currentPreferences.toBuilder().clearBookmarkedFlyers()
                    .addAllBookmarkedFlyers(currentBookmarks).build()
            } else {
                currentPreferences
            }
        }
    }

    suspend fun removeBookmarkedMagazine(magazineId: String) {
        userPreferencesStore.updateData { currentPreferences ->
            val currentBookmarks = currentPreferences.bookmarkedMagazinesList.toHashSet()
            if (currentBookmarks.contains(magazineId)) {
                currentBookmarks.remove(magazineId)
                currentPreferences.toBuilder().clearBookmarkedMagazines()
                    .addAllBookmarkedMagazines(currentBookmarks).build()
            } else {
                currentPreferences
            }
        }
    }

    suspend fun removeBookmarkedArticle(articleId: String) {
        userPreferencesStore.updateData { currentPreferences ->
            val currentBookmarks = currentPreferences.bookmarkedArticlesList.toHashSet()
            if (currentBookmarks.contains(articleId)) {
                currentBookmarks.remove(articleId)
                currentPreferences.toBuilder().clearBookmarkedArticles()
                    .addAllBookmarkedArticles(currentBookmarks).build()
            } else {
                currentPreferences
            }
        }
    }

    suspend fun increaseShoutoutCount(articleId: String) {
        userPreferencesStore.updateData { currentPreferences ->
            val shoutoutCount = currentPreferences.getShoutoutOrDefault(articleId, 0)
            if (shoutoutCount < MAX_SHOUTOUT) {
                currentPreferences.toBuilder().putShoutout(articleId, shoutoutCount + 1).build()
            } else {
                currentPreferences
            }
        }
    }
    suspend fun fetchBookmarkedMagazineIds(): List<String> =
        userPreferencesFlow.first().bookmarkedMagazinesList
    suspend fun fetchBookmarkedArticleIds(): List<String> =
        userPreferencesFlow.first().bookmarkedArticlesList

    suspend fun fetchBookmarkedFlyerIds(): List<String> =
        userPreferencesFlow.first().bookmarkedFlyersList

    suspend fun fetchOnboardingCompleted(): Boolean =
        userPreferencesFlow.first().onboardingCompleted

    suspend fun fetchUuid(): String =
        userPreferencesFlow.first().uuid

    suspend fun fetchDeviceToken(): String =
        userPreferencesFlow.first().deviceToken

    suspend fun fetchShoutoutCount(articleId: String): Int =
        userPreferencesFlow.first().shoutoutMap.getOrDefault(articleId, 0)

    suspend fun fetchNotificationFlowStatus(): Boolean =
        userPreferencesFlow.first().notificationFlowCompleted
}
