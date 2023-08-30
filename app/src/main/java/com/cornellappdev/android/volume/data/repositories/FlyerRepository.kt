package com.cornellappdev.android.volume.data.repositories

import com.cornellappdev.android.volume.FlyersAfterDateQuery
import com.cornellappdev.android.volume.FlyersBeforeDateQuery
import com.cornellappdev.android.volume.FlyersByIDsQuery
import com.cornellappdev.android.volume.FlyersByOrganizationSlugsQuery
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

    suspend fun fetchOrganizationsByCategorySlug(slug: String): List<Organization> =
        networkApi.fetchOrganizationsByCategory(category = slug).dataAssertNoErrors
            .mapOrganizationCategoryDataToOrganizations()

    suspend fun fetchFlyersByOrganizationSlugs(ids: List<String>): List<Flyer> =
        networkApi.fetchFlyersByOrganizationSlugs(ids).dataAssertNoErrors.mapDataToFlyers()

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
                organizations = flyer.organizations.mapSearchDataToOrganizations(),
                flyerURL = flyer.flyerURL,
                startDate = flyer.startDate as String,
                endDate = flyer.endDate as String,
                imageURL = flyer.imageURL,
                location = flyer.location,
            )
        }


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

    private fun FlyersByOrganizationSlugsQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.getFlyersByOrganizationSlugs.map { flyer ->
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
                name = organization.name,
                slug = organization.slug,
                categorySlug = organization.categorySlug
            )
        }

    private fun List<TrendingFlyersQuery.Organization>.mapTrendsDataToOrganizations(): List<Organization> =
        this.map { organization ->
            Organization(
                name = organization.name,
                slug = organization.slug,
                categorySlug = organization.categorySlug
            )
        }

    private fun List<FlyersAfterDateQuery.Organization>.mapAfterDateDataToOrganizations(): List<Organization> =
        this.map { organization ->
            Organization(
                name = organization.name,
                slug = organization.slug,
                categorySlug = organization.categorySlug
            )
        }

    private fun List<FlyersBeforeDateQuery.Organization>.mapBeforeDateDataToOrganizations(): List<Organization> =
        this.map { organization ->
            Organization(
                name = organization.name,
                slug = organization.slug,
                categorySlug = organization.categorySlug
            )
        }

    private fun List<FlyersByOrganizationSlugsQuery.Organization>.mapOrganizationIdDataToOrganization(): List<Organization> =
        this.map { organization ->
            Organization(
                name = organization.name,
                slug = organization.slug,
                categorySlug = organization.categorySlug
            )
        }

    private fun OrganizationsByCategoryQuery.Data.mapOrganizationCategoryDataToOrganizations(): List<Organization> =
        this.getOrganizationsByCategory!!.map { organization ->
            Organization(
                name = organization.name,
                slug = organization.slug,
                categorySlug = organization.categorySlug,
            )
        }

    private fun List<SearchFlyersQuery.Organization>.mapSearchDataToOrganizations(): List<Organization> =
        this.map { organization ->
            Organization(
                name = organization.name,
                slug = organization.slug,
                categorySlug = organization.categorySlug
            )
        }
}



