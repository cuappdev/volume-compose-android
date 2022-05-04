package com.example.volume_android_revamp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volume_android_revamp.AllPublicationsQuery
import com.example.volume_android_revamp.networking.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.volume_android_revamp.state.State
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class PublicationTabViewModel(private val repository: DataRepository): ViewModel() {
    private val _allPublicationsState = MutableStateFlow<State<AllPublicationsQuery.Data>>(State.Empty())

    val allPublicationsState: StateFlow<State<AllPublicationsQuery.Data>> = _allPublicationsState

    init{
        queryAllPublications()
    }

    private fun queryAllPublications() = viewModelScope.launch {
        _allPublicationsState.value = State.Loading()
        try {
            val response = repository.fetchAllPublications()
            _allPublicationsState.value = response?.let { State.Success(it) }!!
        }catch (e:Exception){
            _allPublicationsState.value = State.Error("There was an error loading all the publications")
        }
    }
}