package com.cornellappdev.android.volume.data.repositories

import com.cornellappdev.android.volume.data.NetworkApi
import com.cornellappdev.android.volume.data.models.Flyer
import javax.inject.Inject
import javax.inject.Singleton

// TODO entire class
@Singleton
class FlyerRepository @Inject constructor(private val networkApi: NetworkApi) {
    suspend fun fetchFlyersByCategorySlug(limit: Double, slug: String): List<Flyer> = listOf()

    suspend fun fetchTrendingFlyers(): List<Flyer> = listOf()

    suspend fun fetchFlyersAfterDate(date: String): List<Flyer> = listOf()
    suspend fun fetchFlyersByIds(ids: List<String>): List<Flyer> = listOf()

    suspend fun fetchPastFlyers(limit: Double): List<Flyer> = listOf()
}