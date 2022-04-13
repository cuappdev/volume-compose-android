package com.example.volume_android_revamp.networking

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.network.okHttpClient
import com.example.volume_android_revamp.AllArticlesQuery
import com.example.volume_android_revamp.TrendingArticlesQuery
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

    suspend fun fetchTrendingArticles(): ApolloResponse<TrendingArticlesQuery.Data>{
        return getApolloClient().query(TrendingArticlesQuery()).execute()
    }
}