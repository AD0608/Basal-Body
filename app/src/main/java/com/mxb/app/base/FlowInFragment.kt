package com.mxb.app.base

import com.mxb.app.R
import com.mxb.app.model.BaseResponse
import com.mxb.app.model.Resource
import com.mxb.app.utils.Constants
import com.mxb.app.utils.showSnackBar
import com.mxb.app.utils.showSnackBarInternet

class FlowInFragment<T>(
    data: Resource<*>,
    fragment: BaseFragment<*, *>,
    onSuccess: (T?) -> Unit,
    onError: ((T?) -> Unit)? = null,
    shouldShowLoader: Boolean = true,
    shouldShowErrorMessage: Boolean = true,
    shouldShowSuccessMessage: Boolean = false
) {
    init {
        when (data) {
            is Resource.Error<*> -> {
                if (shouldShowErrorMessage) {
                    fragment.showApiErrorMessage(data.message)
                }
                onError?.invoke(null)
            }

            is Resource.ErrorWithData<*> -> {
                if (shouldShowErrorMessage) {
                    fragment.showApiErrorMessage(data.message)
                }
                onError?.invoke(data.data as T?)
            }

            is Resource.AuthException<*> -> {
                fragment.showApiErrorMessage(fragment.getString(R.string.message_you_have_been_logged_out_please_log_back_in))
                fragment.clearDataOnLogoutAndNavigateToLoginScreen()
            }

            is Resource.Success<*> -> {
                if (shouldShowSuccessMessage) {
                    showSnackBar(
                        (data.data as BaseResponse<*>).message,
                        Constants.STATUS_SUCCESSFUL,
                        context = fragment.requireContext()
                    )
                }
                data.data?.let { it1 ->
                    onSuccess.invoke(it1 as T?)
                }
            }

            is Resource.Loading<*> -> {
                if (shouldShowLoader) {
                    fragment.manageLoader(data)
                }
            }

            is Resource.NoInternetError<*> -> {
                // fragment.showNoInternetError(it)
                showSnackBarInternet(fragment.requireContext())
            }
        }
    }
}