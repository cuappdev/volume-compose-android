package com.example.volume_android_revamp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.example.volume_android_revamp.AllArticlesQuery
import com.example.volume_android_revamp.TrendingArticlesQuery
import com.example.volume_android_revamp.networking.DataRepository
import com.example.volume_android_revamp.state.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeTabViewModel(private val repository: DataRepository):ViewModel() {
    private val _trendingArticlesState = MutableStateFlow<State<TrendingArticlesQuery.Data>>(State.Empty())
    private val _allArticlesState = MutableStateFlow<State<AllArticlesQuery.Data>>(State.Empty())

    val trendingArticlesState: StateFlow<State<TrendingArticlesQuery.Data>> = _trendingArticlesState
    val allArticlesState: StateFlow<State<AllArticlesQuery.Data>> = _allArticlesState

    init{
        queryTrendingArticles()
        queryAllArticles()
    }

    private fun queryTrendingArticles() = viewModelScope.launch {
        _trendingArticlesState.value = State.Loading()
        try {
            val response = repository.fetchTrendingArticles()
            _trendingArticlesState.value = response?.let { State.Success(it) }!!

        }catch (e:Exception){
            Log.d("home", e.toString())
            _trendingArticlesState.value = State.Error("There was an error loading trending articles!")
        }
    }

    private fun queryAllArticles() = viewModelScope.launch {
        _allArticlesState.value = State.Loading()
        try{
            val response = repository.fetchAllArticles()
            _allArticlesState.value = response?.let { State.Success(it) }!!
        } catch (e:Exception){
            _allArticlesState.value = State.Error("There was an error loading all the articles")
        }
    }

}