package com.cornellappdev.android.volume.data.repositories

import com.cornellappdev.android.volume.data.NetworkApi
import com.cornellappdev.android.volume.data.models.Organization
import javax.inject.Inject

class OrganizationRepository @Inject constructor(private val networkApi: NetworkApi) {
    suspend fun checkAccessCode(accessCode: String, slug: String) =
        networkApi.verifyAccessCode(accessCode = accessCode, organizationSlug = slug)

    suspend fun getOrganizationById(id: String) =
        networkApi.fetchOrganizationById(id).dataAssertNoErrors.getOrganizationByID?.let {
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