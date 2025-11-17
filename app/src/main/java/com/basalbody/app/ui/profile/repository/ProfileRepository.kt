package com.basalbody.app.ui.profile.repository

import android.content.Context
import com.basalbody.app.BuildConfig
import com.basalbody.app.base.BaseRepository
import com.basalbody.app.model.Resource
import com.basalbody.app.model.request.ChangePasswordRequest
import com.basalbody.app.model.request.RegisterRequest
import com.basalbody.app.network.ApiIdentifier
import com.basalbody.app.network.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    var context: Context,
    private val apiService: ApiService,
) : BaseRepository() {
    //-------Change Password Api-------//
    fun callChangePasswordApi(request: ChangePasswordRequest, userId : Int): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_CHANGE_PASSWORD,
            apiCall = {
                apiService.callChangePasswordApi(request = request, userId = userId)
            }
        )
    }

    //-------Upload Profile Image Api-------//
    fun callUploadProfileImageApi(image: okhttp3.MultipartBody.Part): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_UPDATE_USER_PROFILE_PICTURE,
            apiCall = {
                apiService.callUpdateUserProfilePictureApi(profile = image)
            }
        )
    }

    //-------Update Profile Api-------//
    fun callUpdateProfileApi(request : RegisterRequest, userId : Int) : Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_UPDATE_USER_PROFILE,
            apiCall = {
                apiService.callUpdateUserProfileApi(request = request, userId = userId)
            }
        )
    }

    //-------Add Inquiry Api-------//
    fun callAddInquiryApi(request : com.basalbody.app.model.request.AddInquiryRequest) : Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiCall = {
                apiService.callAddInquiryApi(request)
            }
        )
    }

    //-------FAQ Api-------//
    fun callFaqApi() : Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_FAQ,
            apiCall = {
                apiService.callFaqApi()
            }
        )
    }
}