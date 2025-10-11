package com.basalbody.app.ui.auth.activity

import android.content.Intent
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.base.FlowInActivity
import com.basalbody.app.databinding.ActivitySplashBinding
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.openPlayStore
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.request.InitRequest
import com.basalbody.app.model.request.LoginRequest
import com.basalbody.app.model.response.InitData
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import com.basalbody.app.ui.home.activity.HomeActivity
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.Logger
import com.basalbody.app.utils.getText
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
        viewModel.request = InitRequest(
            device = "android"
        )
        viewModel.request?.let {
            viewModel.callInitApi(it)
        }

        /*lifecycleScope.launch {
            delay(3000L)
            navigateUser()
        }*/
    }

    override fun addObservers() {
        lifecycleScope.launch {
            viewModel.callInitApiStateFlow.collect {
                FlowInActivity<BaseResponse<InitData>>(
                    data = it,
                    context = this@SplashActivity,
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
                                viewModel.request?.let {
                                    viewModel.callInitApi(it)
                                }
                            })
                    })
            }
        }
    }

    private fun handleInitData(initData: BaseResponse<InitData>?) {
        if (initData.notNull()) {
            if (initData?.status == true) {
                if (initData.data?.isForceUpdate == true) {
                    handleInitError(
                        dialogTitle = getString(R.string.title_update_mxb),
                        dialogMessage = initData.message.ifEmpty { getString(R.string.message_force_update) },
                        dialogButtonText = getString(R.string.button_update_now),
                        dialogImage = R.drawable.ic_back,
                        onButtonClick = {
                            openPlayStore(packageName)
                            finish()
                        })
                } else if (initData.data?.isMaintenance == true) {
                    handleInitError(
                        dialogTitle = getString(R.string.title_application_is_under_maintenance),
                        dialogMessage = initData.message.ifEmpty { getString(R.string.message_maintenance) },
                        dialogButtonText = getString(R.string.button_retry),
                        dialogImage = R.drawable.ic_back,
                        onButtonClick = {
                            viewModel.request?.let {
                                viewModel.callInitApi(it)
                            }
                        }
                    )
                }
                else if (initData?.data?.isPartialUpdate == true) {
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

                    initData?.data?.webPageUrl?.let {

                        it.termsAndConditions?.let { it1 ->
                            Constants.URL_TERM_CONDITION = it1
                        }
                        it.dataPrivacy?.let { it1 ->
                            Constants.URL_DATA_PRIVACY = it1
                        }

                    }
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
        navigateUser()
    }

    private fun navigateUser() {
        val isOnboardingCompleted = localDataRepository.isOnboardingCompleted()
        if (isOnboardingCompleted) {
            val userDetails = localDataRepository.getUserDetails()
            if (userDetails.notNull()) {
                startNewActivity(HomeActivity::class.java, isFinish = true)
            } else {
                startNewActivity(LoginActivity::class.java, isFinish = true)
            }
        } else {
            startNewActivity(IntroActivity::class.java, isFinish = true)
        }
    }
}