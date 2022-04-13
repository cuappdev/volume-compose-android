package com.example.volume_android_revamp.networking

import com.example.volume_android_revamp.TrendingArticlesQuery

class DataRepository(private val webService:NetworkingApi) {

    suspend fun fetchAllArticles(){
        webService.fetchAllArticles()
    }

    suspend fun fetchTrendingArticles(): TrendingArticlesQuery.Data? {
       return webService.fetchTrendingArticles().data
    }
}