package com.basalbody.app.ui.auth.repository

import android.content.Context
import com.basalbody.app.BuildConfig
import com.basalbody.app.base.BaseRepository
import com.basalbody.app.model.Resource
import com.basalbody.app.model.request.LoginRequest
import com.basalbody.app.network.ApiIdentifier
import com.basalbody.app.network.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    var context: Context,
    private val apiService: ApiService,
) : BaseRepository() {
    //-------Init Api-------//
    fun callInitApi(): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_INIT,
            apiCall = {
                apiService.callInitApi(BuildConfig.VERSION_NAME)
            }
        )
    }

    //-------Login Api-------//
    fun callLoginApi(request: LoginRequest): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_LOGIN,
            apiCall = {
                apiService.callLoginApi(request)
            }
        )
    }
}