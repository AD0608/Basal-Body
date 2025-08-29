package com.mxb.app.ui.setting.repository

import android.content.Context
import com.mxb.app.BuildConfig
import com.mxb.app.base.BaseRepository
import com.mxb.app.model.Resource
import com.mxb.app.network.ApiIdentifier
import com.mxb.app.network.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingRepository @Inject constructor(
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
}