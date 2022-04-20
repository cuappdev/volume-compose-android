package com.example.volume_android_revamp.state

sealed class State<T>(val value: T? = null, val message: String? = null) {
    class Success<T>(data: T) : State<T>(data)
    class Error<T>(message: String?, data: T? = null) : State<T>(data, message)
    class Loading<T> : State<T>()
    class Empty<T>: State<T>()

}