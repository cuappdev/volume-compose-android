package com.example.volume_android_revamp.networking

import com.example.volume_android_revamp.*

class DataRepository(private val webService:NetworkingApi) {

    suspend fun fetchAllArticles(): AllArticlesQuery.Data? =
        webService.fetchAllArticles().data

    suspend fun fetchTrendingArticles(): TrendingArticlesQuery.Data? =
        webService.fetchTrendingArticles().data

    suspend fun fetchAllPublications(): AllPublicationsQuery.Data? =
        webService.fetchAllPublications().data

    suspend fun fetchArticleByPublicationID(pubID: String): ArticlesByPublicationIDQuery.Data? =
        webService.fetchArticleByPublicationID(pubID).data

    suspend fun fetchArticlesByPublicationIDs(pubIDs: MutableList<String>): ArticlesByPublicationIDsQuery.Data?=
        webService.fetchArticlesByPublicationIDs(pubIDs).data

    suspend fun fetchPublicationsByIDs(pubIDs: MutableList<String>): PublicationsByIDsQuery.Data?=
        webService.fetchPublicationsByIDs(pubIDs).data

    suspend fun fetchPublicationByID (pubID: String): PublicationByIDQuery.Data?=
        webService.fetchPublicationByID(pubID).data

    suspend fun fetchArticlesByIDs(ids: MutableSet<String>) : ArticlesByIDsQuery.Data?=
        webService.fetchArticlesByIDs(ids).data

    suspend fun fetchArticleByID (id: String): ArticleByIDQuery.Data?=
        webService.fetchArticleByID(id).data

    suspend fun shoutoutArticle(id: String) : IncrementShoutoutMutation.Data?=
        webService.shoutoutArticle(id).data

    suspend fun createUser(DEVICE_TYPE: String, followPublications: List<String>, deviceToken: String): CreateUserMutation.Data?=
        webService.createUser(DEVICE_TYPE, followPublications, deviceToken).data

    suspend fun followPublication(pubID: String, uuid: String): FollowPublicationMutation.Data?=
        webService.followPublication(pubID, uuid).data

    suspend fun unfollowPublication(pubID: String, uuid: String): UnfollowPublicationMutation.Data?=
        webService.unfollowPublication(pubID, uuid).data
}