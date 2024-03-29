package com.cornellappdev.android.volume.data.repositories

import com.apollographql.apollo3.api.ApolloResponse
import com.cornellappdev.android.volume.DeleteFlyerMutation
import com.cornellappdev.android.volume.FlyerByIDQuery
import com.cornellappdev.android.volume.FlyersAfterDateQuery
import com.cornellappdev.android.volume.FlyersBeforeDateQuery
import com.cornellappdev.android.volume.FlyersByCategorySlugQuery
import com.cornellappdev.android.volume.FlyersByIDsQuery
import com.cornellappdev.android.volume.FlyersByOrganizationSlugQuery
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

    suspend fun fetchFlyerById(id: String): Flyer =
        networkApi.fetchFlyerById(id).dataAssertNoErrors.mapDataToFlyer()

    suspend fun incrementTimesClicked(id: String) {
        networkApi.incrementTimesClicked(id)
    }

    suspend fun fetchTrendingFlyers(): List<Flyer> =
        networkApi.fetchTrendingFlyers().dataAssertNoErrors.mapDataToFlyers()

    suspend fun fetchSearchedFlyers(query: String): List<Flyer> =
        networkApi.fetchSearchedFlyers(query).dataAssertNoErrors.mapDataToFlyers()

    suspend fun fetchFlyersByOrganizationSlug(slug: String): List<Flyer> =
        networkApi.fetchFlyersByOrganizationSlug(slug).dataAssertNoErrors.mapDataToFlyers()

    suspend fun deleteFlyer(id: String): ApolloResponse<DeleteFlyerMutation.Data> =
        networkApi.deleteFlyer(id)

    /*
    Create and remove flyer operations send
     */


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

    private fun FlyerByIDQuery.Data.mapDataToFlyer(): Flyer {
        val flyerData = this.getFlyerByID!!
        return Flyer(
            id = flyerData.id,
            title = flyerData.title,
            organization = Organization(
                id = flyerData.organization.id,
                categorySlug = flyerData.organization.categorySlug,
                name = flyerData.organization.name,
                slug = flyerData.organization.slug,
                websiteURL = flyerData.organization.websiteURL,
                backgroundImageURL = flyerData.organization.backgroundImageURL,
                bio = flyerData.organization.bio,
                profileImageURL = flyerData.organization.profileImageURL,
            ),
            location = flyerData.location,
            flyerURL = flyerData.flyerURL,
            categorySlug = flyerData.categorySlug,
            endDate = flyerData.endDate.toString(),
            imageURL = flyerData.imageURL,
            startDate = flyerData.startDate.toString()
        )
    }

    private fun FlyersByOrganizationSlugQuery.Data.mapDataToFlyers(): List<Flyer> =
        this.getFlyersByOrganizationSlug.map { flyer ->
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
        id = this.id,
        slug = this.slug,
        profileImageURL = this.profileImageURL,
    )

    private fun FlyersByIDsQuery.Organization.mapToOrganization(): Organization = Organization(
        name = this.name,
        categorySlug = this.categorySlug,
        websiteURL = this.websiteURL,
        backgroundImageURL = this.backgroundImageURL,
        bio = this.bio,
        id = this.id,
        slug = this.slug,
        profileImageURL = this.profileImageURL,
    )

    private fun FlyersByCategorySlugQuery.Organization.mapToOrganization(): Organization =
        Organization(
            name = this.name,
            categorySlug = this.categorySlug,
            websiteURL = this.websiteURL,
            backgroundImageURL = this.backgroundImageURL,
            bio = this.bio,
            id = this.id,
            slug = this.slug,
            profileImageURL = this.profileImageURL,
        )

    private fun TrendingFlyersQuery.Organization.mapToOrganization(): Organization = Organization(
        name = this.name,
        categorySlug = this.categorySlug,
        websiteURL = this.websiteURL,
        backgroundImageURL = this.backgroundImageURL,
        bio = this.bio,
        id = this.id,
        slug = this.slug,
        profileImageURL = this.profileImageURL,
    )

    private fun FlyersAfterDateQuery.Organization.mapToOrganization(): Organization = Organization(
        name = this.name,
        categorySlug = this.categorySlug,
        websiteURL = this.websiteURL,
        backgroundImageURL = this.backgroundImageURL,
        bio = this.bio,
        id = this.id,
        slug = this.slug,
        profileImageURL = this.profileImageURL,
    )

    private fun FlyersBeforeDateQuery.Organization.mapToOrganization(): Organization = Organization(
        name = this.name,
        categorySlug = this.categorySlug,
        websiteURL = this.websiteURL,
        backgroundImageURL = this.backgroundImageURL,
        bio = this.bio,
        id = this.id,
        slug = this.slug,
        profileImageURL = this.profileImageURL,
    )

    private fun FlyersByOrganizationSlugQuery.Organization.mapToOrganization(): Organization =
        Organization(
            name = this.name,
            categorySlug = this.categorySlug,
            websiteURL = this.websiteURL,
            backgroundImageURL = this.backgroundImageURL,
            bio = this.bio,
            id = this.id,
            slug = this.slug,
            profileImageURL = this.profileImageURL,
        )
}



