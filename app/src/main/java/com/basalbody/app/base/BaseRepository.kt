package com.basalbody.app.base

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.basalbody.app.R
import com.basalbody.app.datastore.LocalDataRepository
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.Resource
import com.basalbody.app.model.response.InitData
import com.basalbody.app.network.ApiIdentifier
import com.basalbody.app.network.ApiService.Companion.API_AUTH_EXCEPTION
import com.basalbody.app.network.ApiService.Companion.API_CUSTOM_EXCEPTION
import com.basalbody.app.network.ApiService.Companion.API_MAINTENANCE_MODE_EXCEPTION
import com.basalbody.app.network.ApiService.Companion.API_NO_INTERNET_EXCEPTION
import com.basalbody.app.network.ApiService.Companion.API_SERVER_ERROR
import com.basalbody.app.network.ApiService.Companion.API_SUCCESS_RANGE
import com.basalbody.app.extensions.nullSafe
import com.basalbody.app.extensions.safeCast
import com.basalbody.app.extensions.toObject
import com.basalbody.app.extensions.withNotNull
import com.basalbody.app.model.response.LoginResponse
import com.basalbody.app.utils.CommonUtils.checkInternetConnected
import com.basalbody.app.utils.dotsindicator.toObjectTypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.Serializable
import javax.inject.Inject

abstract class BaseRepository {

    @Inject
    lateinit var localDataRepository: LocalDataRepository

    protected fun String.convertToResponseBody(): RequestBody =
        this.toRequestBody("text/plain".toMediaType())

    @SuppressLint("HardwareIds")
    fun getDeviceId(mContext: Context): String {
        return Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)
    }

    protected fun <T : Serializable> callAPI(
        context: Context,
        apiIdentifier: ApiIdentifier? = null,
        apiCall: suspend () -> Response<BaseResponse<T>>,
    ): Flow<Resource<*>> {
        return flow {
            if (!context.checkInternetConnected()) {
                emit(
                    Resource.NoInternetError<String>(
                        context.getString(R.string.message_no_internet_found)
                    )
                )
            } else {
                emit(Resource.Loading<Boolean>(true))
                val response = apiCall()
                response.run {
                    emit(Resource.Loading<Boolean>(false))
                    kotlinx.coroutines.delay(100)
                    when {
                        response.isSuccessful -> {
                            val mBean: BaseResponse<T>? = response.body()
                            when (response.code()) {
                                in API_SUCCESS_RANGE -> {
                                    if (mBean?.status == true) {
                                        when (apiIdentifier) {
                                            ApiIdentifier.API_INIT -> {
                                                val initResponse =
                                                    (mBean as? BaseResponse<*>)?.safeCast<InitData>()
                                                initResponse?.data?.withNotNull { data ->
                                                    data.withNotNull { initData ->
                                                        localDataRepository.saveInitData(initData = initData)
                                                    }
                                                }
                                            }

                                            ApiIdentifier.API_LOGIN -> {
                                                val loginResponse =
                                                    (mBean as? BaseResponse<*>)?.safeCast<LoginResponse>()
                                                loginResponse?.data?.withNotNull { data ->
                                                    data.withNotNull { loginData ->
                                                        localDataRepository.saveBarrierToken(token = loginData.token ?: "")
                                                        localDataRepository.saveUserDetails(userData = loginData)
                                                    }
                                                }
                                            }

                                            ApiIdentifier.API_LOGOUT -> {
                                                localDataRepository.resetUserData()
                                            }

                                            ApiIdentifier.API_DELETE_ACCOUNT -> {
                                                localDataRepository.resetUserData()
                                            }

                                            else -> {}
                                        }
                                        emit(Resource.Success(mBean, message = mBean.message.nullSafe()))
                                    } else {
                                        emit(
                                            Resource.ErrorWithData(
                                                data = mBean,
                                                message = mBean?.message.nullSafe(),
                                                multiPartBodyImage = getMultiPartImageFromErrorResponse(
                                                    response
                                                )
                                            )
                                        )
                                    }
                                }

                                else -> {
                                    emit(
                                        Resource.ErrorWithData(
                                            data = mBean,
                                            message = mBean?.message.nullSafe(),
                                            multiPartBodyImage = getMultiPartImageFromErrorResponse(
                                                response
                                            )
                                        )
                                    )
                                }
                            }
                        }

                        response.code() == API_NO_INTERNET_EXCEPTION -> { //If we check internet exception in Interceptor
                            emit(Resource.NoInternetError<String>(response.message())) //You can change this message in override "intercept" function in Network module
                        }

                        response.code() == API_SERVER_ERROR -> {
                            emit(Resource.Error<String>(response.message()))
                        }

                        response.code() == API_AUTH_EXCEPTION -> {
                            localDataRepository.resetUserData()
                            emit(Resource.AuthException(response))
                        }

                        response.code() == API_CUSTOM_EXCEPTION -> {
                            emit(Resource.Error<String>(response.message()))
                        }

                        response.code() == API_MAINTENANCE_MODE_EXCEPTION -> {
                            if (response.errorBody() != null) {
                                val baseResponse =
                                    response.errorBody()!!.string()
                                        .toObjectTypeToken<BaseResponse<InitData>>()
                                if (baseResponse != null) {
                                    emit(
                                        Resource.ErrorWithData(
                                            data = baseResponse,
                                            message = baseResponse.message.nullSafe(),
                                            multiPartBodyImage = getMultiPartImageFromErrorResponse(
                                                response
                                            )
                                        )
                                    )
                                }
                            }
                        }

                        else -> {
                            if (response.errorBody() != null) {
                                val baseResponse =
                                    response.errorBody()!!.string().toObject<BaseResponse<*>>()
                                if (baseResponse != null) {
                                    emit(
                                        Resource.ErrorWithData(
                                            data = baseResponse,
                                            message = baseResponse.message.nullSafe(),
                                            multiPartBodyImage = getMultiPartImageFromErrorResponse(
                                                response
                                            )
                                        )
                                    )
                                } else {
                                    emit(Resource.Error<String>(response.message()))
                                }
                            } else {
                                emit(
                                    Resource.Error<String>(
                                        response.errorBody()?.string().nullSafe()
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    //-------In case we upload image in api in multiPartBody Part and in any case we fail to upload image we need to update UI for that specific image-------//
    private fun <T> getMultiPartImageFromErrorResponse(response: Response<T>): MultipartBody.Part? {
        return if (response.raw().request.body is MultipartBody) {
            val multipartBody = response.raw().request.body as MultipartBody
            val multiPartImage = multipartBody.parts.find {
                it.headers?.get("Content-Disposition")?.contains("filename") == true
            }
            multiPartImage
        } else {
            null
        }
    }

    //-------Below function is use for get request back from response-------//
    private fun <T> logFailedRequest(response: Response<T>) {
        val requestDetails = StringBuilder()
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                requestDetails.append("Request failed with code: ${response.code()}\n")
                requestDetails.append("URL: ${response.raw().request.url}\n")

                // Log query parameters
                val queryParameters = response.raw().request.url.queryParameterNames
                if (queryParameters.isNotEmpty()) {
                    requestDetails.append("Query Parameters:\n")
                    for (parameter in queryParameters) {
                        val value = response.raw().request.url.queryParameter(parameter)
                        requestDetails.append("$parameter: $value\n")
                    }
                }

                // Log request body if exists
                val requestBody = response.raw().request.body
                if (requestBody != null) {
                    requestDetails.append("Request Body:\n${formatRequestBody(requestBody = requestBody)}\n")
                }

//                val params = HashMap<String, String>()
                val params = HashMap<String, RequestBody>()
                var multiBodyPartImage: MultipartBody.Part?
                if (response.raw().request.body is MultipartBody) {
                    val multipartBody = response.raw().request.body as MultipartBody
                    requestDetails.append("Multipart Parts:\n")
                    for (part in multipartBody.parts) {
                        val contentDisposition = part.headers?.get("Content-Disposition")
                        val name =
                            contentDisposition?.substringAfter("name=\"")?.substringBefore("\"")
                        if (name != null) {
                            if (part.headers?.get("Content-Disposition")
                                    ?.contains("filename") == true
                            ) {
                                multiBodyPartImage = part
                                Log.e("PrinceEWW>>>", "multiBodyPartImage: $multiBodyPartImage")
                            } else {
                                val value = formatRequestBody(part.body)
//                            params[name] = value
                                params[name] = part.body
                                requestDetails.append("$name: $value\n")
                            }
                        }


                        Log.e(
                            "PrinceEWW>>>",
                            "headerContentDesc: ${part.headers?.get("Content-Disposition")}"
                        )
                        // Log image parts separately
                        if (part.headers?.get("Content-Disposition")
                                ?.contains("filename") == true
                        ) {
                            requestDetails.append("Image Part:\nHeaders: ${part.headers}\nBody: [Image Data]\n\n")
                        }
                    }
                }

                // Log form data or multipart data if exists
                // Adjust this part based on how you handle form data and multipart requests in Retrofit
                // Example assumes form data as query parameters
                val formData = response.raw().request.url.query
                if (!formData.isNullOrBlank()) {
                    requestDetails.append("Form Data:\n$formData\n")
                }
            }
            withContext(Dispatchers.Main) {
                Log.e("PrinceEWW>>>", "requestDetails: $requestDetails")
            }
        }


        // Log the request details to console or your logging framework

        // You can also store this request details in a database or a file for later analysis
        // Example: storeRequestDetails(requestDetails.toString())
    }

    private fun formatRequestBody(requestBody: RequestBody): String {
        val buffer = okio.Buffer()
        requestBody.writeTo(buffer)
        return buffer.readUtf8()
    }
}