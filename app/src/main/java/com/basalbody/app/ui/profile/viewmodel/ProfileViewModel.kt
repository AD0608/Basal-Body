package com.basalbody.app.ui.profile.viewmodel

import androidx.lifecycle.viewModelScope
import com.basalbody.app.base.BaseViewModel
import com.basalbody.app.model.Resource
import com.basalbody.app.model.request.ChangePasswordRequest
import com.basalbody.app.model.response.FaqItem
import com.basalbody.app.ui.profile.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.util.ArrayList
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private var profileRepository: ProfileRepository,
) : BaseViewModel() {

    var faqArrayList = ArrayList<FaqItem>()

    /**Always set Initial state of flow is Show loading [false]*/
    //-------Change Password Api-------//
    private val _callChangePasswordApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callChangePasswordApiStateFlow: StateFlow<Resource<*>> = _callChangePasswordApiMutableStateFlow
    fun callChangePasswordApi(request: ChangePasswordRequest, userId : Int) {
        viewModelScope.launch {
            profileRepository.callChangePasswordApi(request = request, userId = userId).collect {
                _callChangePasswordApiMutableStateFlow.value = it
            }
        }
    }

    //-------Upload Profile Image Api-------//
    private val _callUploadProfileImageApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callUploadProfileImageApiStateFlow: StateFlow<Resource<*>> = _callUploadProfileImageApiMutableStateFlow
    fun callUploadProfileImageApi(image: MultipartBody.Part) {
        viewModelScope.launch {
            profileRepository.callUploadProfileImageApi(image).collect {
                _callUploadProfileImageApiMutableStateFlow.value = it
            }
        }
    }

    //-------Update Profile Api-------//
    private val _callUpdateProfileApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callUpdateProfileApiStateFlow: StateFlow<Resource<*>> = _callUpdateProfileApiMutableStateFlow
    fun callUpdateProfileApi(request : com.basalbody.app.model.request.RegisterRequest, userId: Int) {
        viewModelScope.launch {
            profileRepository.callUpdateProfileApi(request = request, userId = userId).collect {
                _callUpdateProfileApiMutableStateFlow.value = it
            }
        }
    }

    //-------Add Inquiry Api-------//
    private val _callAddInquiryApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callAddInquiryApiStateFlow: StateFlow<Resource<*>> = _callAddInquiryApiMutableStateFlow
    fun callAddInquiryApi(request : com.basalbody.app.model.request.AddInquiryRequest) {
        viewModelScope.launch {
            profileRepository.callAddInquiryApi(request).collect {
                _callAddInquiryApiMutableStateFlow.value = it
            }
        }
    }

    //-------Add Inquiry Api-------//
    private val _callFaqApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callFaqApiStateFlow: StateFlow<Resource<*>> = _callFaqApiMutableStateFlow
    fun callFaqApi() {
        viewModelScope.launch {
            profileRepository.callFaqApi().collect {
                _callFaqApiMutableStateFlow.value = it
            }
        }
    }

}