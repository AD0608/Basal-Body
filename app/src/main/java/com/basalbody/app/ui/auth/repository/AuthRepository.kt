package com.basalbody.app.ui.auth.repository

import android.content.Context
import com.basalbody.app.BuildConfig
import com.basalbody.app.base.BaseRepository
import com.basalbody.app.model.Resource
import com.basalbody.app.model.request.ForgotPasswordRequest
import com.basalbody.app.model.request.InitRequest
import com.basalbody.app.model.request.LoginRequest
import com.basalbody.app.model.request.RegisterRequest
import com.basalbody.app.model.request.ResendOtpRequest
import com.basalbody.app.model.request.ResetPasswordStep1Request
import com.basalbody.app.model.request.ResetPasswordStep2Request
import com.basalbody.app.network.ApiIdentifier
import com.basalbody.app.network.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    var context: Context,
    private val apiService: ApiService,
) : BaseRepository() {
    //-------Init Api-------//
    fun callInitApi(request: InitRequest): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_INIT,
            apiCall = {
                apiService.callInitApi(request)
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

    //-------Register Api-------//
    fun callRegisterApi(request: RegisterRequest): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiIdentifier = ApiIdentifier.API_REGISTER,
            apiCall = {
                apiService.callRegisterApi(request)
            }
        )
    }

    //-------Forgot Password Api-------//
    fun callForgotPasswordApi(request: ForgotPasswordRequest): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiCall = {
                apiService.callForgotPasswordApi(request)
            }
        )
    }

    //-------Resend Otp Api-------//
    fun callResendOtpApi(request: ResendOtpRequest): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiCall = {
                apiService.callResendOtpApi(request)
            }
        )
    }

    //-------Reset Password Step 1 Api-------//
    fun callResetPasswordStep1Api(request: ResetPasswordStep1Request): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiCall = {
                apiService.callResetPasswordStep1Api(request)
            }
        )
    }

    //-------Reset Password Step 2 Api-------//
    fun callResetPasswordStep2Api(request: ResetPasswordStep2Request): Flow<Resource<*>> {
        return callAPI(
            context = context,
            apiCall = {
                apiService.callResetPasswordStep2Api(request)
            }
        )
    }
}