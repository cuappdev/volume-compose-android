package com.example.volume_android_revamp.networking

import com.example.volume_android_revamp.AllArticlesQuery
import com.example.volume_android_revamp.TrendingArticlesQuery

class DataRepository(private val webService:NetworkingApi) {

    suspend fun fetchAllArticles(): AllArticlesQuery.Data? =
        webService.fetchAllArticles().data

    suspend fun fetchTrendingArticles(): TrendingArticlesQuery.Data? =
        webService.fetchTrendingArticles().data

}