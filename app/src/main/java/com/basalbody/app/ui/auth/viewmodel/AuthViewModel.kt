package com.basalbody.app.ui.auth.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.viewModelScope
import com.basalbody.app.base.BaseViewModel
import com.basalbody.app.model.Resource
import com.basalbody.app.model.request.ForgotPasswordRequest
import com.basalbody.app.model.request.InitRequest
import com.basalbody.app.model.request.LoginRequest
import com.basalbody.app.model.request.RegisterRequest
import com.basalbody.app.model.request.ResendOtpRequest
import com.basalbody.app.model.request.ResetPasswordStep1Request
import com.basalbody.app.model.request.ResetPasswordStep2Request
import com.basalbody.app.ui.auth.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private var splashRepository: AuthRepository,
) : BaseViewModel() {

    var isSelectEmail: Boolean = false
    var isTermsConditionCheck: Boolean = false
    var countDownTimer: CountDownTimer? = null
    var request : InitRequest? = null

    //-------Init Api-------//
    /**Always set Initial state of flow is Show loading [false]*/
    private val _callInitApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callInitApiStateFlow: StateFlow<Resource<*>> = _callInitApiMutableStateFlow

    fun callInitApi(request: InitRequest) {
        viewModelScope.launch {
            splashRepository.callInitApi(request).collect {
                _callInitApiMutableStateFlow.value = it
            }
        }
    }

    //-------Login Api-------//
    private val _callLoginApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callLoginApiStateFlow: StateFlow<Resource<*>> = _callLoginApiMutableStateFlow
    fun callLoginApi(request: LoginRequest) {
        viewModelScope.launch {
            splashRepository.callLoginApi(request).collect {
                _callLoginApiMutableStateFlow.value = it
            }
        }
    }

    //-------Register Api-------//
    private val _callRegisterApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callRegisterApiStateFlow: StateFlow<Resource<*>> = _callRegisterApiMutableStateFlow
    fun callRegisterApi(request: RegisterRequest) {
        viewModelScope.launch {
            splashRepository.callRegisterApi(request).collect {
                _callRegisterApiMutableStateFlow.value = it
            }
        }
    }

    //-------Forgot Password Api-------//
    private val _callForgotPasswordApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callForgotPasswordApiStateFlow: StateFlow<Resource<*>> =
        _callForgotPasswordApiMutableStateFlow

    fun callForgotPasswordApi(request: ForgotPasswordRequest) {
        viewModelScope.launch {
            splashRepository.callForgotPasswordApi(request).collect {
                _callForgotPasswordApiMutableStateFlow.value = it
            }
        }
    }

    //-------Resend OTP Api-------//
    private val _callResendOtpApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callResendOtpApiStateFlow: StateFlow<Resource<*>> = _callResendOtpApiMutableStateFlow
    fun callResendOtpApi(request: ResendOtpRequest) {
        viewModelScope.launch {
            splashRepository.callResendOtpApi(request).collect {
                _callResendOtpApiMutableStateFlow.value = it
            }
        }
    }

    //-------Reset Password Step 1 Api-------//
    private val _callResetPasswordStep1ApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callResetPasswordStep1ApiStateFlow: StateFlow<Resource<*>> =
        _callResetPasswordStep1ApiMutableStateFlow

    fun callResetPasswordStep1Api(request: ResetPasswordStep1Request) {
        viewModelScope.launch {
            splashRepository.callResetPasswordStep1Api(request).collect {
                _callResetPasswordStep1ApiMutableStateFlow.value = it
            }
        }
    }

    //-------Reset Password Step 2 Api-------//
    private val _callResetPasswordStep2ApiMutableStateFlow =
        MutableStateFlow(Resource.Loading<Boolean>(isLoadingShow = false) as Resource<*>)
    val callResetPasswordStep2ApiStateFlow: StateFlow<Resource<*>> =
        _callResetPasswordStep2ApiMutableStateFlow

    fun callResetPasswordStep2Api(request: ResetPasswordStep2Request) {
        viewModelScope.launch {
            splashRepository.callResetPasswordStep2Api(request).collect {
                _callResetPasswordStep2ApiMutableStateFlow.value = it
            }
        }
    }

}