package com.cornellappdev.android.volume.data.repositories

import com.cornellappdev.android.volume.*
import com.cornellappdev.android.volume.data.NetworkApi
import com.cornellappdev.android.volume.data.models.ContentType
import com.cornellappdev.android.volume.data.models.Magazine
import com.cornellappdev.android.volume.data.models.Publication
import com.cornellappdev.android.volume.data.models.Social
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Magazine Repository. Encapsulate the logic required to access data sources, particularly
 * those depending on the Magazine model and mutations/queries on Magazines.
 *
 * @see Magazine
 */
@Singleton
class MagazineRepository @Inject constructor(private val networkApi: NetworkApi) {
    suspend fun fetchMagazinesBySemester(semester: String, limit: Double? = null) =
        networkApi.fetchMagazinesBySemester(semester = semester, limit = limit)
            .dataAssertNoErrors.mapDataToMagazines()

    suspend fun fetchFeaturedMagazines(limit: Double? = null): List<Magazine> =
        networkApi.fetchFeaturedMagazines(limit).dataAssertNoErrors.mapDataToMagazines()

    suspend fun fetchMagazineById(id: String): Magazine =
        networkApi.fetchMagazineById(id).dataAssertNoErrors.mapDataToMagazine()

    suspend fun fetchMagazinesByIds(ids: List<String>): List<Magazine> =
        networkApi.fetchMagazinesByIds(ids).dataAssertNoErrors.mapDataToMagazines()

    suspend fun fetchAllMagazines(limit: Double? = null): List<Magazine> =
        networkApi.fetchAllMagazines(limit).dataAssertNoErrors.mapDataToMagazines()

    suspend fun incrementMagazineShoutouts(id: String, uuid: String): IncrementMagazineShoutoutsMutation.Data =
        networkApi.incrementMagazineShoutout(id, uuid).dataAssertNoErrors

    private fun FeaturedMagazinesQuery.Data.mapDataToMagazines(): List<Magazine> {
        // Not really sure why I need a non-null assertion here, if there are issues getting
        // featured magazines in the future this could be the cause.
        return this.getFeaturedMagazines!!.map {
            val publication = it.publication
            Magazine (
                id = it.id,
                title = it.title,
                date = it.date.toString(),
                semester = it.semester,
                pdfURL = it.pdfURL,
                publication = Publication(
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    contentTypes = publication.contentTypes.map {
                        ContentType.valueOf(it.uppercase())
                    },
                    websiteURL = publication.websiteURL,
                    numArticles = publication.numArticles,
                    socials = publication.socials.map {
                        Social(it.social, it.url)
                    }
                ),
                shoutouts = it.shoutouts
            )
        }
    }

    private fun MagazinesBySemesterQuery.Data.mapDataToMagazines(): List<Magazine> {
        return this.getMagazinesBySemester.map {
            val publication = it.publication
            Magazine (
                id = it.id,
                title = it.title,
                date = it.date.toString(),
                semester = it.semester,
                pdfURL = it.pdfURL,
                publication = Publication(
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    contentTypes = publication.contentTypes.map {
                        ContentType.valueOf(it.uppercase())
                    },
                    websiteURL = publication.websiteURL,
                    numArticles = publication.numArticles,
                    socials = publication.socials.map {
                        Social(it.social, it.url)
                    }
                ),
                shoutouts = it.shoutouts
            )
        }
    }

    private fun MagazinesByIDsQuery.Data.mapDataToMagazines(): List<Magazine> {
        return this.getMagazinesByIDs.map {
            val publication = it.publication
            Magazine (
                id = it.id,
                title = it.title,
                date = it.date.toString(),
                semester = it.semester,
                pdfURL = it.pdfURL,
                publication = Publication(
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    contentTypes = publication.contentTypes.map {
                        ContentType.valueOf(it.uppercase())
                    },
                    websiteURL = publication.websiteURL,
                    numArticles = publication.numArticles,
                    socials = publication.socials.map {
                        Social(it.social, it.url)
                    }
                ),
                shoutouts = it.shoutouts
            )
        }
    }

    private fun MagazineByIdQuery.Data.mapDataToMagazine(): Magazine {
        // Same with here.
        val magRep = this.getMagazineByID!!
        val publication = magRep.publication
        return Magazine (
            id = magRep.id,
            title = magRep.title,
            date = magRep.date.toString(),
            semester = magRep.semester,
            pdfURL = magRep.pdfURL,
            publication = Publication(
                backgroundImageURL = publication.backgroundImageURL,
                bio = publication.bio,
                name = publication.name,
                profileImageURL = publication.profileImageURL,
                rssName = publication.rssName,
                rssURL = publication.rssURL,
                slug = publication.slug,
                shoutouts = publication.shoutouts,
                contentTypes = publication.contentTypes.map {
                    ContentType.valueOf(it.uppercase())
                },
                websiteURL = publication.websiteURL,
                numArticles = publication.numArticles,
                socials = publication.socials.map {
                    Social(it.social, it.url)
                }
            ),
            shoutouts = magRep.shoutouts
        )
    }

    private fun AllMagazinesQuery.Data.mapDataToMagazines(): List<Magazine> {
        val magsRep = this.getAllMagazines
        return magsRep.map { magazine ->
            val publication = magazine.publication
            Magazine(
                id = magazine.id,
                title = magazine.title,
                date = magazine.date.toString(),
                semester = magazine.semester,
                pdfURL = magazine.pdfURL,
                publication = Publication(
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    contentTypes = publication.contentTypes.map {
                        ContentType.valueOf(it.uppercase())
                    },
                    websiteURL = publication.websiteURL,
                    numArticles = publication.numArticles,
                    socials = publication.socials.map {
                        Social(it.social, it.url)
                    }
                ),
                shoutouts = magazine.shoutouts
            )
        }
    }
}