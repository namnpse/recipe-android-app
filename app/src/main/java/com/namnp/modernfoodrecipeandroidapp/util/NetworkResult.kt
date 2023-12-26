package com.namnp.modernfoodrecipeandroidapp.util

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: UiText? = null
) {

    class Success<T>(data: T): NetworkResult<T>(data)
    class Error<T>(message: UiText?, data: T? = null): NetworkResult<T>(data, message)
    class Loading<T>: NetworkResult<T>()

}