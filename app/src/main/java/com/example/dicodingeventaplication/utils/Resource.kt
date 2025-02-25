package com.example.dicodingeventaplication.utils


// menangani 3 kemungkinan : error, succes, loading
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class ErrorConection<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
    class Empty<T>(data: T, message: String? = null) : Resource<T>(data, message)
//    class Shimmer<T> : Resource<T>()
}