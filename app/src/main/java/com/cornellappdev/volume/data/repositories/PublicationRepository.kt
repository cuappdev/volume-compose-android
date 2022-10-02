package com.cornellappdev.volume.data.repositories

import com.cornellappdev.volume.AllPublicationsQuery
import com.cornellappdev.volume.PublicationBySlugQuery
import com.cornellappdev.volume.data.NetworkApi
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.data.models.Social
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Publication Repository. Encapsulate the logic required to access data sources, particularly
 * those depending on the Publication model and mutations/queries on Publications.
 *
 * @see Article
 */
@Singleton
class PublicationRepository @Inject constructor(private val networkApi: NetworkApi) {
    class PublicationNotFound : Exception()

    suspend fun fetchAllPublications(): List<Publication> =
        networkApi.fetchAllPublications().dataAssertNoErrors.mapDataToPublications()

    suspend fun fetchPublicationsByIDs(slugs: List<String>): List<Publication> =
        slugs.map { slug ->
            fetchPublicationBySlug(slug).first()
        }

    /** Throws an exception if the publication isn't found. */
    suspend fun fetchPublicationBySlug(slug: String): List<Publication> =
        networkApi.fetchPublicationBySlug(slug).dataAssertNoErrors.mapDataToPublications()

    private fun AllPublicationsQuery.Data.mapDataToPublications(): List<Publication> {
        return this.getAllPublications.map { publicationData ->
            Publication(
                backgroundImageURL = publicationData.backgroundImageURL,
                bio = publicationData.bio,
                name = publicationData.name,
                profileImageURL = publicationData.profileImageURL,
                rssName = publicationData.rssName,
                rssURL = publicationData.rssURL,
                slug = publicationData.slug,
                shoutouts = publicationData.shoutouts,
                websiteURL = publicationData.websiteURL,
                mostRecentArticle = publicationData.mostRecentArticle?.nsfw?.let { isNSFW ->
                    Article(
                        publicationData.mostRecentArticle.id,
                        publicationData.mostRecentArticle.title,
                        publicationData.mostRecentArticle.articleURL,
                        publicationData.mostRecentArticle.imageURL,
                        Publication(

                            backgroundImageURL = publicationData.backgroundImageURL,
                            bio = publicationData.bio,
                            name = publicationData.name,
                            profileImageURL = publicationData.profileImageURL,
                            rssName = publicationData.rssName,
                            rssURL = publicationData.rssURL,
                            slug = publicationData.slug,
                            shoutouts = publicationData.shoutouts,
                            websiteURL = publicationData.websiteURL,
                            socials = publicationData.socials
                                .map { Social(it.social, it.url) }),
                        publicationData.mostRecentArticle.date.toString(),
                        publicationData.mostRecentArticle.shoutouts,
                        isNSFW,
                    )
                },
                socials = publicationData.socials
                    .map { Social(it.social, it.url) })
        }
    }

    private fun PublicationBySlugQuery.Data.mapDataToPublications(): List<Publication> {
        return this.getPublicationBySlug?.let { publicationData ->
            listOf(
                Publication(
                    backgroundImageURL = publicationData.backgroundImageURL,
                    bio = publicationData.bio,
                    name = publicationData.name,
                    profileImageURL = publicationData.profileImageURL,
                    rssName = publicationData.rssName,
                    rssURL = publicationData.rssURL,
                    slug = publicationData.slug,
                    shoutouts = publicationData.shoutouts,
                    websiteURL = publicationData.websiteURL,
                    mostRecentArticle = publicationData.mostRecentArticle?.nsfw?.let { isNSFW ->
                        Article(
                            publicationData.mostRecentArticle.id,
                            publicationData.mostRecentArticle.title,
                            publicationData.mostRecentArticle.articleURL,
                            publicationData.mostRecentArticle.imageURL,
                            Publication(

                                backgroundImageURL = publicationData.backgroundImageURL,
                                bio = publicationData.bio,
                                name = publicationData.name,
                                profileImageURL = publicationData.profileImageURL,
                                rssName = publicationData.rssName,
                                rssURL = publicationData.rssURL,
                                slug = publicationData.slug,
                                shoutouts = publicationData.shoutouts,
                                websiteURL = publicationData.websiteURL,
                                socials = publicationData.socials
                                    .map { Social(it.social, it.url) }),
                            publicationData.mostRecentArticle.date.toString(),
                            publicationData.mostRecentArticle.shoutouts,
                            isNSFW,
                        )
                    },
                    socials = publicationData.socials
                        .map { Social(it.social, it.url) })
            )
        } ?: throw PublicationNotFound()
    }
}
