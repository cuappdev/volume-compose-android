package com.cornellappdev.volume.data

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.network.okHttpClient
import com.cornellappdev.volume.*
import okhttp3.OkHttpClient

private const val DEVICE_TYPE: String = "ANDROID"
private const val ENDPOINT = BuildConfig.DEV_ENDPOINT

object NetworkingApi {

    private val apolloClient = ApolloClient.Builder()
        .serverUrl(ENDPOINT)
        .okHttpClient(OkHttpClient.Builder().build())
        .build()

    suspend fun fetchAllArticles(limit: Double? = null): ApolloResponse<AllArticlesQuery.Data> =
        apolloClient.query(AllArticlesQuery(Optional.presentIfNotNull(limit))).execute()

    suspend fun fetchTrendingArticles(limit: Double? = null): ApolloResponse<TrendingArticlesQuery.Data> =
        apolloClient.query(TrendingArticlesQuery(Optional.presentIfNotNull(limit))).execute()

    suspend fun fetchAllPublications(): ApolloResponse<AllPublicationsQuery.Data> =
        apolloClient.query(AllPublicationsQuery()).execute()

    suspend fun fetchArticleByPublicationID(pubID: String): ApolloResponse<ArticlesByPublicationIDQuery.Data> =
        apolloClient.query(ArticlesByPublicationIDQuery(pubID)).execute()

    suspend fun fetchArticlesByPublicationIDs(pubIDs: MutableList<String>): ApolloResponse<ArticlesByPublicationIDsQuery.Data> =
        apolloClient.query(ArticlesByPublicationIDsQuery(pubIDs.toList())).execute()

    suspend fun fetchPublicationsByIDs(pubIDs: MutableList<String>): ApolloResponse<PublicationsByIDsQuery.Data> =
        apolloClient.query(PublicationsByIDsQuery(pubIDs.toList())).execute()

    suspend fun fetchPublicationByID(pubID: String): ApolloResponse<PublicationByIDQuery.Data> =
        apolloClient.query(PublicationByIDQuery(pubID)).execute()

    suspend fun fetchArticlesByIDs(ids: MutableSet<String>): ApolloResponse<ArticlesByIDsQuery.Data> =
        apolloClient.query(ArticlesByIDsQuery(ids.toList())).execute()

    suspend fun fetchArticleByID(id: String): ApolloResponse<ArticleByIDQuery.Data> =
        apolloClient.query(ArticleByIDQuery(id)).execute()

    suspend fun incrementShoutout(
        id: String,
        uuid: String
    ): ApolloResponse<IncrementShoutoutMutation.Data> =
        apolloClient.mutation(IncrementShoutoutMutation(id, uuid)).execute()

    suspend fun createUser(
        followedPublications: List<String>,
        deviceToken: String
    ): ApolloResponse<CreateUserMutation.Data> = apolloClient.mutation(
        CreateUserMutation(
            DEVICE_TYPE,
            followedPublications,
            deviceToken
        )
    ).execute()

    suspend fun getUser(uuid: String): ApolloResponse<GetUserQuery.Data> =
        apolloClient.query(GetUserQuery(uuid)).execute()

    suspend fun followPublication(
        pubID: String,
        uuid: String
    ): ApolloResponse<FollowPublicationMutation.Data> =
        apolloClient.mutation(FollowPublicationMutation(pubID, uuid)).execute()

    suspend fun unfollowPublication(
        pubID: String,
        uuid: String
    ): ApolloResponse<UnfollowPublicationMutation.Data> =
        apolloClient.mutation(UnfollowPublicationMutation(pubID, uuid)).execute()
}
