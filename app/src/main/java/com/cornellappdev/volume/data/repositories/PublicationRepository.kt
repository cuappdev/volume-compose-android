package com.cornellappdev.volume.data.repositories

import com.cornellappdev.volume.AllPublicationsQuery
import com.cornellappdev.volume.PublicationByIDQuery
import com.cornellappdev.volume.PublicationsByIDsQuery
import com.cornellappdev.volume.data.NetworkingApi
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.data.models.Social

class PublicationRepository(private val webService: NetworkingApi) {
    inner class PublicationNotFound : Exception()

    suspend fun fetchAllPublications(): List<Publication> =
        webService.fetchAllPublications().dataAssertNoErrors.mapDataToPublications()

    suspend fun fetchPublicationsByIDs(pubIDs: MutableList<String>): List<Publication> =
        webService.fetchPublicationsByIDs(pubIDs).dataAssertNoErrors.mapDataToPublications()

    /** Throws an exception if the publication isn't found. */
    suspend fun fetchPublicationByID(pubID: String): List<Publication> =
        webService.fetchPublicationByID(pubID).dataAssertNoErrors.mapDataToPublications()

    private fun AllPublicationsQuery.Data.mapDataToPublications(): List<Publication> {
        return this.getAllPublications.map { publicationData ->
            Publication(
                id = publicationData.id,
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
                            id = publicationData.id,
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

    private fun PublicationsByIDsQuery.Data.mapDataToPublications(): List<Publication> {
        return this.getPublicationsByIDs.map { publicationData ->
            Publication(
                id = publicationData.id,
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
                            id = publicationData.id,
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

    private fun PublicationByIDQuery.Data.mapDataToPublications(): List<Publication> {
        return this.getPublicationByID?.let { publicationData ->
            listOf(
                Publication(
                    id = publicationData.id,
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
                                id = publicationData.id,
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
