package com.basalbody.app.ui.home.repository

import android.content.Context
import com.basalbody.app.BuildConfig
import com.basalbody.app.base.BaseRepository
import com.basalbody.app.model.Resource
import com.basalbody.app.model.request.AddDailyLogRequest
import com.basalbody.app.model.request.ChangePasswordRequest
import com.basalbody.app.network.ApiIdentifier
import com.basalbody.app.network.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeRepository @Inject constructor(
    var context: Context,
    private val apiService: ApiService,
) : BaseRepository() {

    //-------Logout Api-------//
    fun callLogoutApi(): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_LOGOUT,
            apiCall = {
                apiService.callLogoutApi()
            }
        )
    }

    //-------Logout Api-------//
    fun callDeleteUserApi(): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_DELETE_ACCOUNT,
            apiCall = {
                apiService.callDeleteUserApi()
            }
        )
    }

    //-------Get User Profile Api-------//
    fun callGetUserProfileApi(): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_GET_USER_PROFILE,
            apiCall = {
                apiService.callGetUserProfileApi()
            }
        )
    }

    //-------Add Daily Log Api-------//
    fun callAddDailyLogApi(request: AddDailyLogRequest): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiCall = {
                apiService.callAddDailyLogApi(request = request)
            }
        )
    }

    //-------Get Logs Insights Api-------//
    fun callGetLogsInsights() : Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_GET_LOGS_INSIGHTS,
            apiCall = {
                apiService.callGetLogsInsights()
            }
        )
    }

    //-------Get Calender Logs Api-------//
    fun callGetCalenderLogsApi(): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiCall = {
                apiService.callGetCalenderLogsApi()
            }
        )
    }

    //-------Home Api-------//
    fun callHome() : Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_HOME,
            apiCall = {
                apiService.callHome()
            }
        )
    }
}