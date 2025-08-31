package com.basalbody.app.base

import com.basalbody.app.R
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.Resource
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.showSnackBar
import com.basalbody.app.utils.showSnackBarInternet

class FlowInActivity<T>(
    data: Resource<*>,
    context: BaseActivity<*, *>,
    onSuccess: (T?) -> Unit,
    onError: ((T?) -> Unit)? = null,
    shouldShowLoader: Boolean = true,
    shouldShowErrorMessage: Boolean = true,
    shouldShowSuccessMessage: Boolean = false,
    noInternet: (() -> Unit)? = null,
) {
    init {
        when (data) {
            is Resource.Error<*> -> {
                if (shouldShowErrorMessage) {
                    context.showApiErrorMessage(data.message)
                }
                onError?.invoke(null)
            }

            is Resource.ErrorWithData<*> -> {
                if (shouldShowErrorMessage) {
                    context.showApiErrorMessage(data.message)
                }
                onError?.invoke(data.data as T?)
            }

            is Resource.AuthException<*> -> {
                context.showApiErrorMessage(context.getString(R.string.message_you_have_been_logged_out_please_log_back_in))
                context.clearDataOnLogoutAndNavigateToLoginScreen()
            }

            is Resource.Success<*> -> {
                if (shouldShowSuccessMessage) {
                    showSnackBar(
                        (data.data as BaseResponse<*>).message,
                        Constants.STATUS_SUCCESSFUL,
                        context = context
                    )
                }
                data.data?.let { it1 ->
                    onSuccess.invoke(it1 as T?)
                }
            }

            is Resource.Loading<*> -> {
                if (shouldShowLoader) {
                    context.manageLoader(data)
                }
            }

            is Resource.NoInternetError<*> -> {
                //  context.showNoInternetError(it)
                noInternet?.invoke() ?: showSnackBarInternet(context)
            }
        }
    }
}