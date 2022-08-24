package com.cornellappdev.volume.data

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.network.okHttpClient
import com.cornellappdev.volume.*
import okhttp3.OkHttpClient

private const val DEVICE_TYPE: String = "ANDROID"
private const val ENDPOINT = BuildConfig.DEV_ENDPOINT

class NetworkingApi {

    private val apolloClient = ApolloClient.Builder()
        .serverUrl(ENDPOINT)
        .okHttpClient(OkHttpClient.Builder().build())
        .build()

    suspend fun fetchAllArticles(): ApolloResponse<AllArticlesQuery.Data> {
        return apolloClient.query(AllArticlesQuery()).execute()
    }

    suspend fun fetchTrendingArticles(): ApolloResponse<TrendingArticlesQuery.Data> {
        return apolloClient.query(TrendingArticlesQuery()).execute()
    }

    suspend fun fetchAllPublications(): ApolloResponse<AllPublicationsQuery.Data> {
        return apolloClient.query(AllPublicationsQuery()).execute()
    }

    suspend fun fetchArticleByPublicationID(pubID: String): ApolloResponse<ArticlesByPublicationIDQuery.Data> {
        return apolloClient.query(ArticlesByPublicationIDQuery(pubID)).execute()
    }

    suspend fun fetchArticlesByPublicationIDs(pubIDs: MutableList<String>): ApolloResponse<ArticlesByPublicationIDsQuery.Data> {
        return apolloClient.query(ArticlesByPublicationIDsQuery(pubIDs.toList())).execute()
    }

    suspend fun fetchPublicationsByIDs(pubIDs: MutableList<String>): ApolloResponse<PublicationsByIDsQuery.Data> {
        return apolloClient.query(PublicationsByIDsQuery(pubIDs.toList())).execute()
    }

    suspend fun fetchPublicationByID(pubID: String): ApolloResponse<PublicationByIDQuery.Data> {
        return apolloClient.query(PublicationByIDQuery(pubID)).execute()
    }

    suspend fun fetchArticlesByIDs(ids: MutableSet<String>): ApolloResponse<ArticlesByIDsQuery.Data> {
        return apolloClient.query(ArticlesByIDsQuery(ids.toList())).execute()
    }

    suspend fun fetchArticleByID(id: String): ApolloResponse<ArticleByIDQuery.Data> {
        return apolloClient.query(ArticleByIDQuery(id)).execute()
    }

    suspend fun incrementShoutout(id: String): ApolloResponse<IncrementShoutoutMutation.Data> {
        return apolloClient.mutation(IncrementShoutoutMutation(id)).execute()
    }

    suspend fun createUser(
        followedPublications: List<String>,
        deviceToken: String
    ): ApolloResponse<CreateUserMutation.Data> {
        return apolloClient.mutation(
            CreateUserMutation(
                DEVICE_TYPE,
                followedPublications,
                deviceToken
            )
        ).execute()
    }

    suspend fun followPublication(
        pubID: String,
        uuid: String
    ): ApolloResponse<FollowPublicationMutation.Data> {
        return apolloClient.mutation(FollowPublicationMutation(pubID, uuid)).execute()
    }

    suspend fun unfollowPublication(
        pubID: String,
        uuid: String
    ): ApolloResponse<UnfollowPublicationMutation.Data> {
        return apolloClient.mutation(UnfollowPublicationMutation(pubID, uuid)).execute()
    }
}
