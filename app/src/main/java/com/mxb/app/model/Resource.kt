package com.mxb.app.model

import okhttp3.MultipartBody

// A generic class that contains data and status about loading this data.
//Use of "multiPartBodyImage": In case we upload image in api in multiPartBody Part and in any case we fail to upload image we need to update UI for that specific image
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val isLoadingShow: Boolean? = null,
    val multiPartBodyImage: MultipartBody.Part? = null,
) {
    class Success<T>(data: T) : Resource<T>(data = data)
    class Loading<T>(isLoadingShow: Boolean) : Resource<T>(isLoadingShow = isLoadingShow)
    class Error<T>(message: String) : Resource<T>(message = message)
    class AuthException<T>(data: T) : Resource<T>(data = data)

    class ErrorWithData<T>(
        data: T,
        message: String,
        multiPartBodyImage: MultipartBody.Part? = null,
    ) : Resource<T>(data = data, message = message, multiPartBodyImage = multiPartBodyImage)

    class NoInternetError<T>(message: String) : Resource<T>(message = message)
}