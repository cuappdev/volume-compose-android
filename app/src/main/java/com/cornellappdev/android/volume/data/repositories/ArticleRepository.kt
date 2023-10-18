package com.cornellappdev.android.volume.data.repositories

import com.cornellappdev.android.volume.AllArticlesQuery
import com.cornellappdev.android.volume.ArticleByIDQuery
import com.cornellappdev.android.volume.ArticlesByIDsQuery
import com.cornellappdev.android.volume.ArticlesByPublicationSlugQuery
import com.cornellappdev.android.volume.ArticlesByPublicationSlugsQuery
import com.cornellappdev.android.volume.IncrementShoutoutsMutation
import com.cornellappdev.android.volume.SearchArticlesQuery
import com.cornellappdev.android.volume.ShuffledArticlesByPublicationSlugsQuery
import com.cornellappdev.android.volume.TrendingArticlesQuery
import com.cornellappdev.android.volume.data.NetworkApi
import com.cornellappdev.android.volume.data.models.Article
import com.cornellappdev.android.volume.data.models.Publication
import com.cornellappdev.android.volume.data.models.Social
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Article Repository. Encapsulate the logic required to access data sources, particularly
 * those depending on the Article model and mutations/queries on Articles.
 *
 * @see Article
 */
@Singleton
class ArticleRepository @Inject constructor(private val networkApi: NetworkApi) {
    suspend fun fetchAllArticles(limit: Double? = null): List<Article> =
        networkApi.fetchAllArticles(limit).dataAssertNoErrors.mapDataToArticles()

    suspend fun fetchTrendingArticles(limit: Double? = null): List<Article> =
        networkApi.fetchTrendingArticles(limit).dataAssertNoErrors.mapDataToArticles()

    suspend fun fetchArticlesByPublicationSlug(slug: String): List<Article> =
        networkApi.fetchArticleByPublicationSlug(slug).dataAssertNoErrors.mapDataToArticles()

    suspend fun fetchArticlesByPublicationSlugs(slugs: List<String>): List<Article> =
        networkApi.fetchArticlesByPublicationSlugs(slugs).dataAssertNoErrors.mapDataToArticles()

    suspend fun fetchArticlesByShuffledPublicationSlugs(slugs: List<String>): List<Article> =
        networkApi.fetchShuffledArticlesByPublicationSlugs(slugs).dataAssertNoErrors.mapDataToArticles()

    suspend fun fetchArticlesByIDs(ids: List<String>): List<Article> =
        networkApi.fetchArticlesByIDs(ids).dataAssertNoErrors.mapDataToArticles()

    suspend fun fetchArticleByID(id: String): Article =
        networkApi.fetchArticleByID(id).dataAssertNoErrors.mapDataToArticles().first()

    suspend fun searchArticles(query: String): List<Article> =
        networkApi.fetchSearchedArticles(query).dataAssertNoErrors.mapDataToArticles()

    suspend fun incrementShoutout(id: String, uuid: String): IncrementShoutoutsMutation.Data =
        networkApi.incrementShoutout(id, uuid).dataAssertNoErrors

    private fun SearchArticlesQuery.Data.mapDataToArticles(): List<Article> {
        return this.searchArticles.map { articleData ->
            val publication = articleData.publication
            Article(
                title = articleData.title,
                articleURL = articleData.articleURL,
                date = articleData.date.toString(),
                id = articleData.id,
                imageURL = articleData.imageURL,
                publication = Publication(
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    websiteURL = publication.websiteURL,
                    numArticles = publication.numArticles,
                    socials = publication.socials
                        .map { Social(it.social, it.url) }
                ),
                shoutouts = articleData.shoutouts,

                nsfw = articleData.nsfw
            )
        }
    }

    private fun AllArticlesQuery.Data.mapDataToArticles(): List<Article> {
        return this.getAllArticles.map { articleData ->
            val publication = articleData.publication
            Article(
                title = articleData.title,
                articleURL = articleData.articleURL,
                date = articleData.date.toString(),
                id = articleData.id,
                imageURL = articleData.imageURL,
                publication = Publication(
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    websiteURL = publication.websiteURL,
                    numArticles = publication.numArticles,
                    socials = publication.socials
                        .map { Social(it.social, it.url) }
                ),
                shoutouts = articleData.shoutouts,

                nsfw = articleData.nsfw
            )
        }
    }

    private fun TrendingArticlesQuery.Data.mapDataToArticles(): List<Article> {
        return this.getTrendingArticles.map { articleData ->
            val publication = articleData.publication
            Article(
                title = articleData.title,
                articleURL = articleData.articleURL,
                date = articleData.date.toString(),
                id = articleData.id,
                imageURL = articleData.imageURL,
                publication = Publication(
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    numArticles = publication.numArticles,
                    websiteURL = publication.websiteURL,
                    socials = publication.socials
                        .map { Social(it.social, it.url) }
                ),
                shoutouts = articleData.shoutouts,
                nsfw = articleData.nsfw
            )
        }
    }

    private fun ArticlesByPublicationSlugQuery.Data.mapDataToArticles(): List<Article> {
        return this.getArticlesByPublicationSlug.map { articleData ->
            val publication = articleData.publication
            Article(
                title = articleData.title,
                articleURL = articleData.articleURL,
                date = articleData.date.toString(),
                id = articleData.id,
                imageURL = articleData.imageURL,
                publication = Publication(
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    numArticles = publication.numArticles,
                    websiteURL = publication.websiteURL,
                    socials = publication.socials
                        .map { Social(it.social, it.url) }
                ),
                shoutouts = articleData.shoutouts,
                nsfw = articleData.nsfw
            )
        }
    }

    private fun ArticlesByPublicationSlugsQuery.Data.mapDataToArticles(): List<Article> {
        return this.getArticlesByPublicationSlugs.map { articleData ->
            val publication = articleData.publication
            Article(
                title = articleData.title,
                articleURL = articleData.articleURL,
                date = articleData.date.toString(),
                id = articleData.id,
                imageURL = articleData.imageURL,
                publication = Publication(
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    numArticles = publication.numArticles,
                    websiteURL = publication.websiteURL,
                    socials = publication.socials
                        .map { Social(it.social, it.url) }
                ),
                shoutouts = articleData.shoutouts,
                nsfw = articleData.nsfw
            )
        }
    }

    private fun ShuffledArticlesByPublicationSlugsQuery.Data.mapDataToArticles(): List<Article> {
        return this.getShuffledArticlesByPublicationSlugs.map { articleData ->
            val publication = articleData.publication
            Article(
                title = articleData.title,
                articleURL = articleData.articleURL,
                date = articleData.date.toString(),
                id = articleData.id,
                imageURL = articleData.imageURL,
                publication = Publication(
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    numArticles = publication.numArticles,
                    websiteURL = publication.websiteURL,
                    socials = publication.socials
                        .map { Social(it.social, it.url) }
                ),
                shoutouts = articleData.shoutouts,
                nsfw = articleData.nsfw
            )
        }
    }

    private fun ArticlesByIDsQuery.Data.mapDataToArticles(): List<Article> {
        return this.getArticlesByIDs.map { articleData ->
            val publication = articleData.publication
            Article(
                title = articleData.title,
                articleURL = articleData.articleURL,
                date = articleData.date.toString(),
                id = articleData.id,
                imageURL = articleData.imageURL,
                publication = Publication(
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    numArticles = publication.numArticles,
                    websiteURL = publication.websiteURL,
                    socials = publication.socials
                        .map { Social(it.social, it.url) }
                ),
                shoutouts = articleData.shoutouts,
                nsfw = articleData.nsfw
            )
        }
    }

    private fun ArticleByIDQuery.Data.mapDataToArticles(): List<Article> {
        return this.getArticleByID?.let { articleData ->
            val publication = articleData.publication
            listOf(Article(
                title = articleData.title,
                articleURL = articleData.articleURL,
                date = articleData.date.toString(),
                id = articleData.id,
                imageURL = articleData.imageURL,
                publication = Publication(
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    websiteURL = publication.websiteURL,
                    numArticles = publication.numArticles,
                    socials = publication.socials
                        .map { Social(it.social, it.url) }
                ),
                shoutouts = articleData.shoutouts,
                nsfw = articleData.nsfw
            ))
        } ?: listOf()
    }
}
