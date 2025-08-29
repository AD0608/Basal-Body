package com.mxb.app.network

data class ApiResources<out T>(
    val status: Status,
    val data: T?,
    val message: String? = "",
    val code: Int = 200
) {
    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
        NO_INTERNET_CONNECTION,
        UNKNOWN
    }

    companion object {

        fun <T> success(data: T, message: String? = ""): ApiResources<T> {
            return ApiResources(Status.SUCCESS, data, message)
        }

        fun <T> error(message: String?, data: T? = null, code: Int = 200): ApiResources<T> {
            return ApiResources(Status.ERROR, data, message, code)
        }

        fun <T> loading(data: T? = null): ApiResources<T> {
            return ApiResources(Status.LOADING, data, null)
        }

        fun <T> unknown(data: T? = null): ApiResources<T> {
            return ApiResources(Status.UNKNOWN, data, null)
        }

        fun <T> noInternetConnection(data: T? = null): ApiResources<T> {
            return ApiResources(Status.NO_INTERNET_CONNECTION, null)
        }
    }
}
