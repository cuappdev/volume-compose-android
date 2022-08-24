package com.cornellappdev.volume.data.repositories

import com.cornellappdev.volume.*
import com.cornellappdev.volume.data.NetworkingApi
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.models.Publication
import com.cornellappdev.volume.data.models.Social

class ArticleRepository(private val webService: NetworkingApi) {
    suspend fun fetchAllArticles(): List<Article> =
        webService.fetchAllArticles().dataAssertNoErrors.mapDataToArticles()

    suspend fun fetchTrendingArticles(): List<Article> =
        webService.fetchTrendingArticles().dataAssertNoErrors.mapDataToArticles()

    suspend fun fetchArticlesByPublicationID(pubID: String): List<Article> =
        webService.fetchArticleByPublicationID(pubID).dataAssertNoErrors.mapDataToArticles()

    suspend fun fetchArticlesByPublicationIDs(pubIDs: MutableList<String>): List<Article> =
        webService.fetchArticlesByPublicationIDs(pubIDs).dataAssertNoErrors.mapDataToArticles()

    suspend fun fetchArticlesByIDs(ids: MutableSet<String>): List<Article> =
        webService.fetchArticlesByIDs(ids).dataAssertNoErrors.mapDataToArticles()

    suspend fun fetchArticleByID(id: String): List<Article> =
        webService.fetchArticleByID(id).dataAssertNoErrors.mapDataToArticles()

    suspend fun incrementShoutout(id: String): IncrementShoutoutMutation.Data =
        webService.incrementShoutout(id).dataAssertNoErrors

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
                    id = publication.id,
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    websiteURL = publication.websiteURL,
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
                    id = publication.id,
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    websiteURL = publication.websiteURL,
                    socials = publication.socials
                        .map { Social(it.social, it.url) }
                ),
                shoutouts = articleData.shoutouts,
                nsfw = articleData.nsfw
            )
        }
    }

    private fun ArticlesByPublicationIDQuery.Data.mapDataToArticles(): List<Article> {
        return this.getArticlesByPublicationID.map { articleData ->
            val publication = articleData.publication
            Article(
                title = articleData.title,
                articleURL = articleData.articleURL,
                date = articleData.date.toString(),
                id = articleData.id,
                imageURL = articleData.imageURL,
                publication = Publication(
                    id = publication.id,
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    websiteURL = publication.websiteURL,
                    socials = publication.socials
                        .map { Social(it.social, it.url) }
                ),
                shoutouts = articleData.shoutouts,
                nsfw = articleData.nsfw
            )
        }
    }

    private fun ArticlesByPublicationIDsQuery.Data.mapDataToArticles(): List<Article> {
        return this.getArticlesByPublicationIDs.map { articleData ->
            val publication = articleData.publication
            Article(
                title = articleData.title,
                articleURL = articleData.articleURL,
                date = articleData.date.toString(),
                id = articleData.id,
                imageURL = articleData.imageURL,
                publication = Publication(
                    id = publication.id,
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
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
                    id = publication.id,
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
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
                    id = publication.id,
                    backgroundImageURL = publication.backgroundImageURL,
                    bio = publication.bio,
                    name = publication.name,
                    profileImageURL = publication.profileImageURL,
                    rssName = publication.rssName,
                    rssURL = publication.rssURL,
                    slug = publication.slug,
                    shoutouts = publication.shoutouts,
                    websiteURL = publication.websiteURL,
                    socials = publication.socials
                        .map { Social(it.social, it.url) }
                ),
                shoutouts = articleData.shoutouts,
                nsfw = articleData.nsfw
            ))
        } ?: listOf()
    }
}
