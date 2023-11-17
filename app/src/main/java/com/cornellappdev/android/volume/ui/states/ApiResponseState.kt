package com.cornellappdev.android.volume.ui.states

/**
 * A generic response state class for the loading, error, and success states during network requests.
 * Delete all other response state classes, and replace them with this one
 */
sealed class ResponseState<out T> {
    object Loading : ResponseState<Nothing>()
    data class Error(val errors: List<com.apollographql.apollo3.api.Error> = listOf()) :
        ResponseState<Nothing>()

    data class Success<T>(val data: T) : ResponseState<T>()
}
