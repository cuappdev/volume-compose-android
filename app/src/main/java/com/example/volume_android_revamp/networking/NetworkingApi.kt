package com.example.volume_android_revamp.networking

import android.hardware.biometrics.BiometricManager
import android.service.controls.DeviceTypes
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.network.okHttpClient
import com.example.volume_android_revamp.*
import okhttp3.OkHttpClient

class NetworkingApi {

    private val prodEndpoint = com.example.volume_android_revamp.BuildConfig.prod_endpoint

    fun getApolloClient(): ApolloClient {
        val okHttpClient = OkHttpClient.Builder().build()
        return ApolloClient.builder()
            .serverUrl(prodEndpoint)
            .okHttpClient(okHttpClient)
            .build()
    }

    suspend fun fetchAllArticles(): ApolloResponse<AllArticlesQuery.Data> {
        return getApolloClient().query(AllArticlesQuery()).execute()
    }

    suspend fun fetchTrendingArticles(): ApolloResponse<TrendingArticlesQuery.Data> {
        return getApolloClient().query(TrendingArticlesQuery()).execute()
    }

    suspend fun fetchAllPublications(): ApolloResponse<AllPublicationsQuery.Data>{
        return getApolloClient().query(AllPublicationsQuery()).execute()
    }

    suspend fun fetchArticleByPublicationID(pubID: String): ApolloResponse<ArticlesByPublicationIDQuery.Data>{
        return getApolloClient().query(ArticlesByPublicationIDQuery(pubID)).execute()
    }

    suspend fun fetchArticlesByPublicationIDs(pubIDs: MutableList<String>): ApolloResponse<ArticlesByPublicationIDsQuery.Data> {
        return getApolloClient().query(ArticlesByPublicationIDsQuery(pubIDs.toList())).execute()
    }

    suspend fun fetchPublicationsByIDs(pubIDs: MutableList<String>): ApolloResponse<PublicationsByIDsQuery.Data>{
        return getApolloClient().query(PublicationsByIDsQuery(pubIDs.toList())).execute()
    }

    suspend fun fetchPublicationByID(pubID: String): ApolloResponse<PublicationByIDQuery.Data>{
        return getApolloClient().query(PublicationByIDQuery(pubID)).execute()
    }

    suspend fun fetchArticlesByIDs (ids: MutableSet<String>): ApolloResponse<ArticlesByIDsQuery.Data>{
        return getApolloClient().query(ArticlesByIDsQuery(ids.toList())).execute()
    }

    suspend fun fetchArticleByID (id: String): ApolloResponse<ArticleByIDQuery.Data>{
        return getApolloClient().query(ArticleByIDQuery(id)).execute()
    }

    suspend fun shoutoutArticle(id: String): ApolloResponse<IncrementShoutoutMutation.Data>{
        return getApolloClient().mutation(IncrementShoutoutMutation(id)).execute()
    }

    suspend fun createUser(DEVICE_TYPE: String, followedPublications: List<String>, deviceToken: String): ApolloResponse<CreateUserMutation.Data>{
        return getApolloClient().mutation(CreateUserMutation(DEVICE_TYPE, followedPublications, deviceToken)).execute()
    }

    suspend fun followPublication(pubID: String, uuid: String): ApolloResponse<FollowPublicationMutation.Data>{
        return getApolloClient().mutation(FollowPublicationMutation(pubID, uuid)).execute()
    }

    suspend fun unfollowPublication(pubID:String, uuid: String): ApolloResponse<UnfollowPublicationMutation.Data>{
        return getApolloClient().mutation(UnfollowPublicationMutation(pubID, uuid)).execute()
    }
}