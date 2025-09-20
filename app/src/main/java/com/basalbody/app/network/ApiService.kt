package com.basalbody.app.network

import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.request.ChangePasswordRequest
import com.basalbody.app.model.request.ForgotPasswordRequest
import com.basalbody.app.model.request.LoginRequest
import com.basalbody.app.model.request.RegisterRequest
import com.basalbody.app.model.request.ResendOtpRequest
import com.basalbody.app.model.request.ResetPasswordStep1Request
import com.basalbody.app.model.request.ResetPasswordStep2Request
import com.basalbody.app.model.response.ChangePasswordResponse
import com.basalbody.app.model.response.ForgotPasswordResponse
import com.basalbody.app.model.response.InitData
import com.basalbody.app.model.response.LogoutResponse
import com.basalbody.app.model.response.ResendOtpResponse
import com.basalbody.app.model.response.ResetPasswordStep1Response
import com.basalbody.app.model.response.ResetPasswordStep2Response
import com.basalbody.app.model.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    companion object {

        val API_SUCCESS_RANGE = 200..299
        const val API_NO_INTERNET_EXCEPTION = 100
        const val API_AUTH_EXCEPTION = 401
        const val API_ERROR = 400
        const val API_MAINTENANCE_MODE_EXCEPTION = 503
        const val API_CUSTOM_EXCEPTION = 999
        const val API_SERVER_ERROR = 500
    }

    //-------Init-------//
    @GET(API_INIT)
    suspend fun callInitApi(@Path("version") version: String): Response<BaseResponse<InitData>>

    //-------Login-------//
    @POST(API_LOGIN)
    suspend fun callLoginApi(@Body request: LoginRequest): Response<BaseResponse<UserResponse>>

    //-------Register-------//
    @POST(API_REGISTER)
    suspend fun callRegisterApi(@Body request: RegisterRequest): Response<BaseResponse<UserResponse>>

    //-------Forgot Password-------//
    @POST(API_FORGOT_PASSWORD)
    suspend fun callForgotPasswordApi(@Body request: ForgotPasswordRequest): Response<BaseResponse<ForgotPasswordResponse>>

    //-------Resend OTP-------//
    @POST(API_RESEND_OTP)
    suspend fun callResendOtpApi(@Body request: ResendOtpRequest): Response<BaseResponse<ResendOtpResponse>>

    //-------Reset Password Step 1-------//
    @POST(API_RESET_PASSWORD_STEP1)
    suspend fun callResetPasswordStep1Api(@Body request: ResetPasswordStep1Request): Response<BaseResponse<ResetPasswordStep1Response>>

    //-------Reset Password Step 2-------//
    @POST(API_RESET_PASSWORD_STEP2)
    suspend fun callResetPasswordStep2Api(@Body request: ResetPasswordStep2Request): Response<BaseResponse<ResetPasswordStep2Response>>

    @GET(API_LOGOUT)
    suspend fun callLogoutApi(): Response<BaseResponse<LogoutResponse>>

    @PUT(API_CHANGE_PASSWORD)
    suspend fun callChangePasswordApi(@Body request: ChangePasswordRequest): Response<BaseResponse<ChangePasswordResponse>>

}

enum class ApiIdentifier {
    API_INIT,
    API_LOGIN,
    API_REGISTER,
    API_LOGOUT,
    API_DELETE_ACCOUNT,
    API_CHANGE_PASSWORD
}
