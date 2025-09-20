package com.basalbody.app.ui.profile.repository

import android.content.Context
import com.basalbody.app.BuildConfig
import com.basalbody.app.base.BaseRepository
import com.basalbody.app.model.Resource
import com.basalbody.app.model.request.ChangePasswordRequest
import com.basalbody.app.network.ApiIdentifier
import com.basalbody.app.network.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    var context: Context,
    private val apiService: ApiService,
) : BaseRepository() {
    //-------Change Password Api-------//
    fun callChangePasswordApi(request: ChangePasswordRequest): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_CHANGE_PASSWORD,
            apiCall = {
                apiService.callChangePasswordApi(request)
            }
        )
    }
}