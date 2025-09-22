package com.basalbody.app.ui.profile.viewmodel

import androidx.lifecycle.viewModelScope
import com.basalbody.app.base.BaseViewModel
import com.basalbody.app.model.Resource
import com.basalbody.app.model.request.ChangePasswordRequest
import com.basalbody.app.ui.profile.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private var profileRepository: ProfileRepository,
) : BaseViewModel() {

    /**Always set Initial state of flow is Show loading [false]*/
    //-------Change Password Api-------//
    private val _callChangePasswordApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callChangePasswordApiStateFlow: StateFlow<Resource<*>> = _callChangePasswordApiMutableStateFlow
    fun callChangePasswordApi(request: ChangePasswordRequest) {
        viewModelScope.launch {
            profileRepository.callChangePasswordApi(request).collect {
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

}