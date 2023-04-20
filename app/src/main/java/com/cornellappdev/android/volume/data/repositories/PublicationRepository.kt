package com.cornellappdev.android.volume.data.repositories

import com.cornellappdev.android.volume.AllPublicationSlugsQuery
import com.cornellappdev.android.volume.AllPublicationsQuery
import com.cornellappdev.android.volume.PublicationBySlugQuery
import com.cornellappdev.android.volume.data.NetworkApi
import com.cornellappdev.android.volume.data.models.Article
import com.cornellappdev.android.volume.data.models.ContentType
import com.cornellappdev.android.volume.data.models.Publication
import com.cornellappdev.android.volume.data.models.Social
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Publication Repository. Encapsulate the logic required to access data sources, particularly
 * those depending on the Publication model and mutations/queries on Publications.
 *
 * @see Article
 */
private const val TAG = "PublicationRepository"
@Singleton
class PublicationRepository @Inject constructor(private val networkApi: NetworkApi) {
    class PublicationNotFound : Exception()

    suspend fun fetchAllPublications(): List<Publication> =
        networkApi.fetchAllPublications().dataAssertNoErrors.mapDataToPublication()

    suspend fun fetchAllPublicationSlugs(): List<String> =
        networkApi.fetchAllPublicationSlugs().dataAssertNoErrors.mapDataToStrings()

    suspend fun fetchPublicationsBySlugs(slugs: List<String>): List<Publication> =
        slugs.map { slug ->
            fetchPublicationBySlug(slug)
        } //test


    /** Throws an exception if the publication isn't found. */
    suspend fun fetchPublicationBySlug(slug: String): Publication =
        networkApi.fetchPublicationBySlug(slug).dataAssertNoErrors.mapDataToPublication()

    private fun AllPublicationSlugsQuery.Data.mapDataToStrings(): List<String> {
        return this.getAllPublications.map { it.slug }
    }

    private fun AllPublicationsQuery.Data.mapDataToPublication(): List<Publication> {
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
                numArticles = publicationData.numArticles,
                contentTypes = publicationData.contentTypes.map {
                    ContentType.valueOf(it.uppercase())
                },
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
                            numArticles = publicationData.numArticles,
                            contentTypes = publicationData.contentTypes.map {
                                ContentType.valueOf(it.uppercase())
                            },
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

    private fun PublicationBySlugQuery.Data.mapDataToPublication(): Publication {
        return this.getPublicationBySlug?.let { publicationData ->
            val pub = Publication(
                backgroundImageURL = publicationData.backgroundImageURL,
                bio = publicationData.bio,
                name = publicationData.name,
                profileImageURL = publicationData.profileImageURL,
                rssName = publicationData.rssName,
                rssURL = publicationData.rssURL,
                slug = publicationData.slug,
                shoutouts = publicationData.shoutouts,
                contentTypes = publicationData.contentTypes.map {
                    ContentType.valueOf(it.uppercase())
                },
                numArticles = publicationData.numArticles,
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
                            contentTypes = publicationData.contentTypes.map {
                                ContentType.valueOf(it.uppercase())
                            },
                            websiteURL = publicationData.websiteURL,
                            numArticles = publicationData.numArticles,
                            socials = publicationData.socials
                                .map { Social(it.social, it.url) }),
                        publicationData.mostRecentArticle.date.toString(),
                        publicationData.mostRecentArticle.shoutouts,
                        isNSFW,
                    )
                },
                socials = publicationData.socials
                    .map { Social(it.social, it.url) })
            pub
        } ?: throw PublicationRepository.PublicationNotFound()
    }
}
