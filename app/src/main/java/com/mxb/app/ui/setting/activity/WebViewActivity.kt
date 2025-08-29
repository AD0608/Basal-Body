package com.mxb.app.ui.setting.activity

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import com.mxb.app.R
import com.mxb.app.base.BaseActivity
import com.mxb.app.common.CommonViewModel
import com.mxb.app.databinding.ActivityWebViewBinding
import com.mxb.app.extensions.getEnum
import com.mxb.app.extensions.withNotNull
import com.mxb.app.utils.Constants
import com.mxb.app.utils.Constants.EMPTY_STRING
import com.mxb.app.utils.EnumUtils
import com.mxb.app.utils.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewActivity : BaseActivity<CommonViewModel, ActivityWebViewBinding>() {

    private var receiptUrl = ""

    override fun getViewBinding() = ActivityWebViewBinding.inflate(layoutInflater)

    override fun initSetup() {
        LoadingDialog.showLoadDialog(this@WebViewActivity)
//        showProgressDialog(this)
        binding.webView.apply {
            intent.extras.withNotNull { bundle ->
                bundle.getEnum<EnumUtils.WebView>(Constants.BUNDLE_KEY_WHICH_WEB_VIEW).withNotNull {
                        when (it) {
                            EnumUtils.WebView.TERMS_AND_CONDITIONS -> {
                                binding.toolbar.setTitle(getString(R.string.label_term_conditions))
                                loadUrl(Constants.URL_TERM_CONDITION)
                            }

                            EnumUtils.WebView.PRIVACY_POLICY -> {
                                binding.toolbar.setTitle(getString(R.string.label_privacy_policy))
                                loadUrl(Constants.URL_PRIVACY_POLICY)
                            }

                            EnumUtils.WebView.ABOUT_US -> {
                                binding.toolbar.setTitle(getString(R.string.label_about_us))
                                loadUrl(Constants.URL_ABOUT_US)
                            }

                            else -> {}
                        }
                    }
            } ?: run {
                loadUrl(Constants.URL_TEST)
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    view?.loadUrl(request?.url.toString())
                    return true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    //---------Below code for reload page again----//
                    if ((view?.title ?: EMPTY_STRING).isNotEmpty()) {
                        LoadingDialog.hideLoadDialog()
                    } else {
                        view?.reload()
                    }
                    super.onPageFinished(view, url)
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                }

                override fun onReceivedError(
                    view: WebView?, request: WebResourceRequest?, error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                }
            }
        }

        onBackPressedDispatcher.addCallback(this@WebViewActivity) {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                finish() // or remove() to allow system back
            }
        }
    }


    override fun listeners() {

    }

}