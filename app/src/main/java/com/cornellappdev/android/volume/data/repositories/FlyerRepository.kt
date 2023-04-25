package com.cornellappdev.android.volume.data.repositories

import com.cornellappdev.android.volume.data.NetworkApi
import com.cornellappdev.android.volume.data.models.Flyer
import com.cornellappdev.android.volume.data.models.Magazine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlyerRepository @Inject constructor(private val networkApi: NetworkApi) {
    suspend fun getFlyersByCategorySlug(slug: String): List<Magazine> = TODO()

    fun CreateDummyFlyer() {
        Flyer(
            id = "",
            flyerURL = "",

        )
    }
}