package com.cornellappdev.android.volume.data.repositories

import com.cornellappdev.android.volume.FlyersByIDsQuery
import com.cornellappdev.android.volume.data.NetworkApi
import com.cornellappdev.android.volume.data.models.Flyer
import com.cornellappdev.android.volume.data.models.Organization
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "FlyerRepository"
@Singleton
class FlyerRepository @Inject constructor(private val networkApi: NetworkApi) {
    suspend fun fetchFlyersByCategorySlug(limit: Double, slug: String): List<Flyer> = listOf()

    suspend fun fetchTrendingFlyers(): List<Flyer> = listOf()

    suspend fun fetchFlyersAfterDate(date: String): List<Flyer> = listOf()
    suspend fun fetchFlyersByIds(ids: List<String>): List<Flyer> =
        networkApi.fetchFlyersByIds(ids).dataAssertNoErrors.mapDataToFlyers()
    suspend fun fetchTodayFlyers(): List<Flyer>? {
        return fetchFlyersFromUrl("http://34.86.84.49/api/flyers/daily/")
    }
    suspend fun fetchPastFlyers(limit: Double): List<Flyer>? {
        return fetchFlyersFromUrl("http://34.86.84.49/api/flyers/past/")
    }

    suspend fun fetchWeeklyFlyers(): List<Flyer>? {
        return fetchFlyersFromUrl("http://34.86.84.49/api/flyers/weekly/") // weekly endpoint
    }

    suspend fun fetchUpcomingFlyers(): List<Flyer>? {
        return fetchFlyersFromUrl("http://34.86.84.49/api/flyers/upcoming/")
    }

    private suspend fun fetchFlyersFromUrl(url: String): List<Flyer>? {
        val weeklyFlyersString = fetchUrlContents(url)
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val flyerListType = Types.newParameterizedType(List::class.java, Flyer::class.java)
        val jsonAdapter: JsonAdapter<List<Flyer>> = moshi.adapter(flyerListType)
        return jsonAdapter.fromJson(weeklyFlyersString)
    }
    private suspend fun fetchUrlContents(url: String): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        return suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val json: String = response.body?.string() ?: ""
                    continuation.resume(json)
                }

                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }
            })
        }
    }
    private fun FlyersByIDsQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.getFlyersByIDs.map { flyer ->
            Flyer (
                id = flyer.id,
                title = flyer.title,
                organizations = flyer.organizations.mapDataToOrganizations(),
                flyerURL = flyer.flyerURL,
                startDate = flyer.startDate as String,
                endDate = flyer.endDate as String,
                imageURL = flyer.imageURL,
                location = flyer.location
            )
    }
    
    private fun List<FlyersByIDsQuery.Organization>.mapDataToOrganizations(): List<Organization> =
        this.map { organization -> 
            Organization (
                id = organization.id,
                name = organization.name,
                slug = organization.slug,
                type = organization.categorySlug
            )
        }
}

