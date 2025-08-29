package com.mxb.app.network

import com.mxb.app.model.BaseResponse
import com.mxb.app.model.response.InitData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    companion object {

        val API_SUCCESS_RANGE = 200..299
        const val API_NO_INTERNET_EXCEPTION = 100
        const val API_AUTH_EXCEPTION = 401
        const val API_MAINTENANCE_MODE_EXCEPTION = 503
        const val API_CUSTOM_EXCEPTION = 999
        const val API_SERVER_ERROR = 500
    }

    //-------Init-------//
    @GET(API_INIT)
    suspend fun callInitApi(@Path("version") version: String): Response<BaseResponse<InitData>>

}

enum class ApiIdentifier {
    API_INIT,
}
