package com.cornellappdev.android.volume.data.repositories

import com.cornellappdev.android.volume.FlyersAfterDateQuery
import com.cornellappdev.android.volume.FlyersBeforeDateQuery
import com.cornellappdev.android.volume.FlyersByIDsQuery
import com.cornellappdev.android.volume.FlyersByOrganizationIDsQuery
import com.cornellappdev.android.volume.OrganizationsByCategoryQuery
import com.cornellappdev.android.volume.TrendingFlyersQuery
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
    suspend fun fetchFlyersAfterDate(date: String): List<Flyer> =
        networkApi.fetchFlyersAfterDate(date).dataAssertNoErrors.mapDataToFlyers()

    suspend fun fetchFlyersBeforeDate(date: String): List<Flyer> =
        networkApi.fetchFlyersBeforeDate(date).dataAssertNoErrors.mapDataToFlyers()

    suspend fun fetchOrganizationsByCategorySlug(slug: String): List<Organization> =
        networkApi.fetchOrganizationsByCategory(category = slug).dataAssertNoErrors.mapOrganizationCategoryDataToOrganizations()

    suspend fun fetchFlyersByOrganizationIds(ids: List<String>): List<Flyer> =
        networkApi.fetchFlyersByOrganizationIds(ids).dataAssertNoErrors.mapDataToFlyers()

    suspend fun fetchFlyersByIds(ids: List<String>): List<Flyer> =
        networkApi.fetchFlyersByIds(ids).dataAssertNoErrors.mapDataToFlyers()

    suspend fun fetchPastFlyers(limit: Double): List<Flyer>? {
        return fetchFlyersFromUrl("http://34.86.84.49/api/flyers/past/")
    }

    suspend fun fetchWeeklyFlyers(): List<Flyer>? {
        return fetchFlyersFromUrl("http://34.86.84.49/api/flyers/weekly/") // weekly endpoint
    }

    suspend fun fetchUpcomingFlyers(): List<Flyer>? {
        return fetchFlyersFromUrl("http://34.86.84.49/api/flyers/upcoming/")
    }

    suspend fun incrementTimesClicked(id: String) {
        networkApi.incrementTimesClicked(id)
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

    // These functions map the apollo query types to types of the models that are in place.

    private fun FlyersByIDsQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.getFlyersByIDs.map { flyer ->
            Flyer(
                id = flyer.id,
                title = flyer.title,
                organizations = flyer.organizations.mapIdsDataToOrganizations(),
                flyerURL = flyer.flyerURL,
                startDate = flyer.startDate as String,
                endDate = flyer.endDate as String,
                imageURL = flyer.imageURL,
                location = flyer.location
            )
        }

    private fun FlyersByOrganizationIDsQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.getFlyersByOrganizationIDs.map { flyer ->
            Flyer(
                id = flyer.id,
                title = flyer.title,
                organizations = flyer.organizations.mapOrganizationIdDataToOrganization(),
                flyerURL = flyer.flyerURL,
                startDate = flyer.startDate as String,
                endDate = flyer.endDate as String,
                imageURL = flyer.imageURL,
                location = flyer.location
            )
        }

    private fun TrendingFlyersQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.getTrendingFlyers.map { flyer ->
            Flyer(
                id = flyer.id,
                title = flyer.title,
                organizations = flyer.organizations.mapTrendsDataToOrganizations(),
                flyerURL = flyer.flyerURL,
                startDate = flyer.startDate as String,
                endDate = flyer.endDate as String,
                imageURL = flyer.imageURL,
                location = flyer.location
            )
        }

    private fun FlyersAfterDateQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.getFlyersAfterDate.map { flyer ->
            Flyer(
                id = flyer.id,
                title = flyer.title,
                organizations = flyer.organizations.mapAfterDateDataToOrganizations(),
                flyerURL = flyer.flyerURL,
                startDate = flyer.startDate as String,
                endDate = flyer.endDate as String,
                imageURL = flyer.imageURL,
                location = flyer.location
            )
        }

    private fun FlyersBeforeDateQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.getFlyersBeforeDate.map { flyer ->
            Flyer(
                id = flyer.id,
                title = flyer.title,
                organizations = flyer.organizations.mapBeforeDateDataToOrganizations(),
                flyerURL = flyer.flyerURL,
                startDate = flyer.startDate as String,
                endDate = flyer.endDate as String,
                imageURL = flyer.imageURL,
                location = flyer.location
            )
        }

    /* Ideally all these functions should just be named mapDataToOrganizations but I was dealing
     * with some very strange issues with the compiler having issues with overloaded extension
     * functions so I gave them all different names instead. */
    private fun List<FlyersByIDsQuery.Organization>.mapIdsDataToOrganizations(): List<Organization> =
        this.map { organization ->
            Organization(
                id = organization.id,
                name = organization.name,
                slug = organization.slug,
                categorySlug = organization.categorySlug
            )
        }

    private fun List<TrendingFlyersQuery.Organization>.mapTrendsDataToOrganizations(): List<Organization> =
        this.map { organization ->
            Organization(
                id = organization.id,
                name = organization.name,
                slug = organization.slug,
                categorySlug = organization.categorySlug
            )
        }

    private fun List<FlyersAfterDateQuery.Organization>.mapAfterDateDataToOrganizations(): List<Organization> =
        this.map { organization ->
            Organization(
                id = organization.id,
                name = organization.name,
                slug = organization.slug,
                categorySlug = organization.categorySlug
            )
        }

    private fun List<FlyersBeforeDateQuery.Organization>.mapBeforeDateDataToOrganizations(): List<Organization> =
        this.map { organization ->
            Organization(
                id = organization.id,
                name = organization.name,
                slug = organization.slug,
                categorySlug = organization.categorySlug
            )
        }

    private fun List<FlyersByOrganizationIDsQuery.Organization>.mapOrganizationIdDataToOrganization(): List<Organization> =
        this.map { organization ->
            Organization(
                id = organization.id,
                name = organization.name,
                slug = organization.slug,
                categorySlug = organization.categorySlug
            )
        }

    private fun OrganizationsByCategoryQuery.Data.mapOrganizationCategoryDataToOrganizations(): List<Organization> =
        this.getOrganizationsByCategory!!.map { organization ->
            Organization(
                id = organization.id,
                name = organization.name,
                slug = organization.slug,
                categorySlug = organization.categorySlug,
            )
        }
}

