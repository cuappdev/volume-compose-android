package com.cornellappdev.android.volume.data.repositories

import com.cornellappdev.android.volume.FeaturedMagazinesQuery
import com.cornellappdev.android.volume.MagazinesBySemesterQuery
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

    private fun FeaturedMagazinesQuery.Data.mapDataToMagazines(): List<Magazine> {
        // FIXME I should not need a non-null assertion here
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
}