package com.cornellappdev.android.volume.data.repositories

import com.cornellappdev.android.volume.FlyersAfterDateQuery
import com.cornellappdev.android.volume.FlyersBeforeDateQuery
import com.cornellappdev.android.volume.FlyersByCategorySlugQuery
import com.cornellappdev.android.volume.FlyersByIDsQuery
import com.cornellappdev.android.volume.OrganizationsByCategoryQuery
import com.cornellappdev.android.volume.SearchFlyersQuery
import com.cornellappdev.android.volume.TrendingFlyersQuery
import com.cornellappdev.android.volume.data.NetworkApi
import com.cornellappdev.android.volume.data.models.Flyer
import com.cornellappdev.android.volume.data.models.Organization
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FlyerRepository @Inject constructor(private val networkApi: NetworkApi) {
    suspend fun fetchFlyersAfterDate(date: String): List<Flyer> =
        networkApi.fetchFlyersAfterDate(date).dataAssertNoErrors.mapDataToFlyers()

    suspend fun fetchFlyersBeforeDate(date: String): List<Flyer> =
        networkApi.fetchFlyersBeforeDate(date).dataAssertNoErrors.mapDataToFlyers()

    suspend fun fetchFlyersByCategorySlug(slug: String): List<Flyer> =
        networkApi.fetchFlyersByCategorySlug(slug).dataAssertNoErrors.mapDataToFlyers()

    suspend fun fetchFlyersByIds(ids: List<String>): List<Flyer> =
        networkApi.fetchFlyersByIds(ids).dataAssertNoErrors.mapDataToFlyers()

    suspend fun incrementTimesClicked(id: String) {
        networkApi.incrementTimesClicked(id)
    }

    suspend fun fetchTrendingFlyers(): List<Flyer> =
        networkApi.fetchTrendingFlyers().dataAssertNoErrors.mapDataToFlyers()

    suspend fun fetchSearchedFlyers(query: String): List<Flyer> =
        networkApi.fetchSearchedFlyers(query).dataAssertNoErrors.mapDataToFlyers()

    // These functions map the apollo query types to types of the models that are in place.
    private fun SearchFlyersQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.searchFlyers.map { flyer ->
            Flyer(
                id = flyer.id,
                title = flyer.title,
                organization = flyer.organization.mapToOrganization(),
                flyerURL = flyer.flyerURL,
                startDate = flyer.startDate as String,
                endDate = flyer.endDate as String,
                imageURL = flyer.imageURL,
                location = flyer.location,
                categorySlug = flyer.categorySlug,
            )
        }

    // Let this one directly go to the network API so it is easier to view errors
    suspend fun createFlyer(
        title: String,
        startDate: String,
        location: String,
        flyerURL: String,
        endDate: String,
        categorySlug: String,
        imageBase64: String,
        organizationId: String,
    ) = networkApi.createFlyer(
        title,
        startDate,
        location,
        flyerURL,
        endDate,
        categorySlug,
        imageBase64,
        organizationId
    )

    private fun FlyersByIDsQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.getFlyersByIDs.map { flyer ->
            Flyer(
                id = flyer.id,
                title = flyer.title,
                organization = flyer.organization.mapToOrganization(),
                flyerURL = flyer.flyerURL,
                startDate = flyer.startDate as String,
                endDate = flyer.endDate as String,
                imageURL = flyer.imageURL,
                location = flyer.location,
                categorySlug = flyer.categorySlug,
            )
        }

    private fun FlyersByCategorySlugQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.getFlyersByCategorySlug.map { flyer ->
            Flyer(
                id = flyer.id,
                title = flyer.title,
                organization = flyer.organization.mapToOrganization(),
                flyerURL = flyer.flyerURL,
                startDate = flyer.startDate as String,
                endDate = flyer.endDate as String,
                imageURL = flyer.imageURL,
                location = flyer.location,
                categorySlug = flyer.categorySlug,
            )
        }

    private fun TrendingFlyersQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.getTrendingFlyers.map { flyer ->
            Flyer(
                id = flyer.id,
                title = flyer.title,
                organization = flyer.organization.mapToOrganization(),
                flyerURL = flyer.flyerURL,
                startDate = flyer.startDate as String,
                endDate = flyer.endDate as String,
                imageURL = flyer.imageURL,
                location = flyer.location,
                categorySlug = flyer.categorySlug,
            )
        }

    private fun FlyersAfterDateQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.getFlyersAfterDate.map { flyer ->
            Flyer(
                id = flyer.id,
                title = flyer.title,
                organization = flyer.organization.mapToOrganization(),
                flyerURL = flyer.flyerURL,
                startDate = flyer.startDate as String,
                endDate = flyer.endDate as String,
                imageURL = flyer.imageURL,
                location = flyer.location,
                categorySlug = flyer.categorySlug,
            )
        }

    private fun FlyersBeforeDateQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.getFlyersBeforeDate.map { flyer ->
            Flyer(
                id = flyer.id,
                title = flyer.title,
                organization = flyer.organization.mapToOrganization(),
                flyerURL = flyer.flyerURL,
                startDate = flyer.startDate as String,
                endDate = flyer.endDate as String,
                imageURL = flyer.imageURL,
                location = flyer.location,
                categorySlug = flyer.categorySlug,
            )
        }


    private fun SearchFlyersQuery.Organization.mapToOrganization(): Organization = Organization(
        name = this.name,
        categorySlug = this.categorySlug,
        websiteURL = this.websiteURL,
        backgroundImageURL = this.backgroundImageURL,
        bio = this.bio,
        id = this.id
    )

    private fun FlyersByIDsQuery.Organization.mapToOrganization(): Organization = Organization(
        name = this.name,
        categorySlug = this.categorySlug,
        websiteURL = this.websiteURL,
        backgroundImageURL = this.backgroundImageURL,
        bio = this.bio,
        id = this.id
    )

    private fun FlyersByCategorySlugQuery.Organization.mapToOrganization(): Organization =
        Organization(
            name = this.name,
            categorySlug = this.categorySlug,
            websiteURL = this.websiteURL,
            backgroundImageURL = this.backgroundImageURL,
            bio = this.bio,
            id = this.id
        )

    private fun TrendingFlyersQuery.Organization.mapToOrganization(): Organization = Organization(
        name = this.name,
        categorySlug = this.categorySlug,
        websiteURL = this.websiteURL,
        backgroundImageURL = this.backgroundImageURL,
        bio = this.bio,
        id = this.id
    )

    private fun FlyersAfterDateQuery.Organization.mapToOrganization(): Organization = Organization(
        name = this.name,
        categorySlug = this.categorySlug,
        websiteURL = this.websiteURL,
        backgroundImageURL = this.backgroundImageURL,
        bio = this.bio,
        id = this.id
    )

    private fun FlyersBeforeDateQuery.Organization.mapToOrganization(): Organization = Organization(
        name = this.name,
        categorySlug = this.categorySlug,
        websiteURL = this.websiteURL,
        backgroundImageURL = this.backgroundImageURL,
        bio = this.bio,
        id = this.id
    )

    private fun OrganizationsByCategoryQuery.Data.mapToOrganizations(): List<Organization> =
        // TODO why do I need a non-null assertion here?
        this.getOrganizationsByCategory!!.map {
            Organization(
                name = it.name,
                categorySlug = it.categorySlug,
                websiteURL = it.websiteURL,
                backgroundImageURL = it.backgroundImageURL,
                bio = it.bio,
                id = it.id
            )
        }
}



