package com.mxb.app.ui.home.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.mxb.app.R
import com.mxb.app.base.BaseActivity
import com.mxb.app.base.FlowInActivity
import com.mxb.app.common.CommonBottomSheetDialog
import com.mxb.app.databinding.ActivityHomeBinding
import com.mxb.app.extensions.changeText
import com.mxb.app.extensions.notNull
import com.mxb.app.extensions.onSafeClick
import com.mxb.app.extensions.openPlayStore
import com.mxb.app.extensions.putEnum
import com.mxb.app.extensions.startNewActivity
import com.mxb.app.model.BaseResponse
import com.mxb.app.model.response.InitData
import com.mxb.app.ui.common.CommonDialog
import com.mxb.app.ui.home.viewmodel.HomeViewModel
import com.mxb.app.ui.setting.activity.WebViewActivity
import com.mxb.app.utils.Constants
import com.mxb.app.utils.EnumUtils
import com.mxb.app.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>() {
    override fun getViewBinding(): ActivityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            tvApiResponse.changeText("Press button to call Api")
        }
    }

    override fun listeners() {
        binding.apply {
            btnCallAPI onSafeClick {
                //viewModel.callInitApi()
                tvApiResponse.changeText("Calling Api")
            }
            btnTermCondition.onSafeClick {
                navigateWebView()
            }
            buttonLogout.onSafeClick {
                val logoutDialog =
                    CommonDialog.newInstance(type = EnumUtils.DialogType.LOG_OUT).apply {
                        callback = {
                           //------Here need to call api---//
                        }
                    }
                logoutDialog.show(
                    supportFragmentManager,
                    logoutDialog::class.simpleName
                )
            }
            buttonDeleteAccount.onSafeClick {
                val deleteAccountDialog =
                    CommonDialog.newInstance(type = EnumUtils.DialogType.DELETE_ACCOUNT).apply {
                        callback = {
                            //------Here need to call api---//
                        }
                    }
                deleteAccountDialog.show(
                    supportFragmentManager,
                    deleteAccountDialog::class.simpleName
                )
            }
        }
    }

    override fun addObservers() {
        lifecycleScope.launch {
            viewModel.callInitApiStateFlow.collect {
                FlowInActivity<BaseResponse<InitData>>(
                    data = it,
                    context = this@HomeActivity,
                    shouldShowErrorMessage = false,
                    shouldShowLoader = true,
                    onSuccess = ::handleInitData,
                    onError = ::handleInitData,
                    noInternet = {
                        handleInitError(
                            dialogTitle = getString(R.string.title_no_internet),
                            dialogMessage = getString(R.string.message_no_internet_found),
                            dialogButtonText = getString(R.string.button_retry),
                            dialogImage = R.drawable.ic_no_internet,
                            onButtonClick = {
                                viewModel.callInitApi()
                            })
                    })
            }
        }
    }

    private fun handleInitData(initData: BaseResponse<InitData>?) {
        if (initData.notNull()) {
            if (initData?.status == false) {
                if (initData.data?.forceUpdate == true) {
                    handleInitError(
                        dialogTitle = getString(R.string.title_update_mxb),
                        dialogMessage = initData.message.ifEmpty { getString(R.string.message_force_update) },
                        dialogButtonText = getString(R.string.button_update_now),
                        dialogImage = R.drawable.ic_force_update,
                        onButtonClick = {
                            openPlayStore(packageName)
                            finish()
                        })
                } else if (initData.data?.maintenance == true) {
                    handleInitError(
                        dialogTitle = getString(R.string.title_application_is_under_maintenance),
                        dialogMessage = initData.message.ifEmpty { getString(R.string.message_maintenance) },
                        dialogButtonText = getString(R.string.button_retry),
                        dialogImage = R.drawable.ic_maintenance,
                        onButtonClick = {
                            viewModel.callInitApi()
                        }
                    )
                }
            } else {
                if (initData?.data?.update == true) {
                    handleInitError(
                        dialogTitle = getString(R.string.title_update_mxb),
                        dialogMessage = initData.message.ifEmpty { getString(R.string.message_normal_update) },
                        dialogButtonText = getString(R.string.button_update),
                        needToShowBackButton = true,
                        dialogImage = R.drawable.ic_normal_update,
                        onButtonClick = {
                            openPlayStore(packageName)
                            finish()
                        },
                        onBackButtonClick = {
                            localDataRepository.saveInitData(initData.data)
                            Logger.e("Init Success")
                            handleInitSuccess()
                        }
                    )
                } else {
                    localDataRepository.saveInitData(initData?.data)
                    Logger.e("Init Success")
                    handleInitSuccess()
                }
            }

        }
    }

    private fun handleInitError(
        needToShowBackButton: Boolean = false,
        dialogNeedToDismiss: Boolean = false,
        dialogTitle: String = "",
        dialogMessage: String = "",
        dialogButtonText: String = "",
        onButtonClick: () -> Unit,
        dialogImage: Int = R.drawable.ic_no_internet,
        onBackButtonClick: (() -> Unit)? = null
    ) {
        CommonBottomSheetDialog.newInstance(
            this,
            binding.main,
            isPreventBackButton = !dialogNeedToDismiss,
            isCancel = dialogNeedToDismiss,
            isBackButtonVisible = needToShowBackButton
        ).apply {
            title = dialogTitle
            description = dialogMessage
            buttonText = dialogButtonText
            image = dialogImage
            onClick = onButtonClick
            onBackClick = onBackButtonClick
        }.show(supportFragmentManager, "handleInitError")
    }

    private fun handleInitSuccess() {
        binding.apply {
            tvApiResponse.changeText("Api Call Success")
        }
    }
    private fun navigateWebView(){
        startNewActivity(className = WebViewActivity::class.java, bundle = Bundle().apply {
            putEnum(
                Constants.BUNDLE_KEY_WHICH_WEB_VIEW,
                EnumUtils.WebView.TERMS_AND_CONDITIONS
            )
        })
    }
}