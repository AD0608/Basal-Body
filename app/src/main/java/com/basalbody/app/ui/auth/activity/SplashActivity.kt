package com.basalbody.app.ui.auth.activity

import android.content.Intent
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivitySplashBinding
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.openPlayStore
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.response.InitData
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import com.basalbody.app.ui.home.activity.HomeActivity
import com.basalbody.app.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<AuthViewModel, ActivitySplashBinding>() {

    private var TAG = "SplashActivity"

    override fun getViewBinding(): ActivitySplashBinding  = ActivitySplashBinding.inflate(layoutInflater)

    override fun initSetup() {
        setupUI()
    }

    fun setupUI() {
        Log.e(TAG, "setupUI")
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
        //viewModel.callInitApi()

        lifecycleScope.launch {
            delay(3000L)
//            startNewActivity(IntroActivity::class.java, isFinish = true)
            startNewActivity(HomeActivity::class.java, isFinish = true)
        }
    }

    override fun addObservers() {
        /*lifecycleScope.launch {
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
                            dialogImage = R.drawable.ic_back,
                            onButtonClick = {
                                viewModel.callInitApi()
                            })
                    })
            }
        }*/
    }

    private fun handleInitData(initData: BaseResponse<InitData>?) {
        if (initData.notNull()) {
            if (initData?.status == false) {
                if (initData.data?.forceUpdate == true) {
                    handleInitError(
                        dialogTitle = getString(R.string.title_update_mxb),
                        dialogMessage = initData.message.ifEmpty { getString(R.string.message_force_update) },
                        dialogButtonText = getString(R.string.button_update_now),
                        dialogImage = R.drawable.ic_back,
                        onButtonClick = {
                            openPlayStore(packageName)
                            finish()
                        })
                } else if (initData.data?.maintenance == true) {
                    handleInitError(
                        dialogTitle = getString(R.string.title_application_is_under_maintenance),
                        dialogMessage = initData.message.ifEmpty { getString(R.string.message_maintenance) },
                        dialogButtonText = getString(R.string.button_retry),
                        dialogImage = R.drawable.ic_back,
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
                        dialogImage = R.drawable.ic_back,
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
        dialogImage: Int = R.drawable.ic_back,
        onBackButtonClick: (() -> Unit)? = null
    ) {
        /*CommonBottomSheetDialog.newInstance(
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
        }.show(supportFragmentManager, "handleInitError")*/
    }

    private fun handleInitSuccess() {

    }
}