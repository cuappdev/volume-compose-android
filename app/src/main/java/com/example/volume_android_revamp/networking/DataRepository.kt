package com.example.volume_android_revamp.networking

class DataRepository(private val webService:NetworkingApi) {

    suspend fun fetchAllArticles(){
        webService.fetchAllArticles()
    }

    suspend fun fetchTrendingArticles(){
        webService.fetchTrendingArticles()
    }
}