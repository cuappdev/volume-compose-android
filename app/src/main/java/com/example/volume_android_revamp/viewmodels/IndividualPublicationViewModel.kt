package com.example.volume_android_revamp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volume_android_revamp.ArticlesByPublicationIDQuery
import com.example.volume_android_revamp.PublicationByIDQuery
import com.example.volume_android_revamp.TrendingArticlesQuery
import com.example.volume_android_revamp.networking.DataRepository
import com.example.volume_android_revamp.state.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class IndividualPublicationViewModel (private val repository: DataRepository, id:String): ViewModel(){
    private val _publicationByIDState = MutableStateFlow<State<PublicationByIDQuery.Data>>(State.Empty())
    val publicationByIDState: StateFlow<State<PublicationByIDQuery.Data>> = _publicationByIDState
    private val _articlesByPubIDState= MutableStateFlow<State<ArticlesByPublicationIDQuery.Data>>(State.Empty())
    val articlesByPubIDState: StateFlow<State<ArticlesByPublicationIDQuery.Data>> = _articlesByPubIDState
    init {
        queryPublicationByID(id);
        queryArticleByPublicationID(id)
    }


    private fun queryPublicationByID(id: String) = viewModelScope.launch {
        _publicationByIDState.value = State.Loading()
        try {
            val response = repository.fetchPublicationByID(id)
            _publicationByIDState.value = response?.let { State.Success(it) }!!

        }catch (e: Exception){
            Log.d("home", e.toString())
            _publicationByIDState.value = State.Error("There was an error loading trending articles!")
        }
    }
    private fun queryArticleByPublicationID(id: String) = viewModelScope.launch {
        _articlesByPubIDState.value = State.Loading()
        try {
            val response = repository.fetchArticleByPublicationID(id)
            _articlesByPubIDState.value = response?.let { State.Success(it) }!!

        }catch (e: Exception){
            Log.d("home", e.toString())
            _articlesByPubIDState.value = State.Error("There was an error loading trending articles!")
        }
    }
}