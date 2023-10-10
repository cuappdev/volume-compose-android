package com.cornellappdev.android.volume.ui.states

/**
 * A generic response state class for the loading, error, and success states during network requests.
 */
sealed class ResponseState<out T> {
    object Loading : ResponseState<Nothing>()
    data class Error(val errors: List<com.apollographql.apollo3.api.Error>) :
        ResponseState<Nothing>()

    data class Success<T>(val data: T) : ResponseState<T>()
}
