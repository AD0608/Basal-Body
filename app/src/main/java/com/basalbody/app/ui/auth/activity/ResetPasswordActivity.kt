package com.basalbody.app.ui.auth.activity

import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.base.FlowInActivity
import com.basalbody.app.databinding.ActivityResetPasswordBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.onNoSafeClick
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.request.ResetPasswordStep2Request
import com.basalbody.app.model.response.ResetPasswordStep2Response
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import com.basalbody.app.ui.common.CommonSuccessBottomSheetDialog
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.Logger
import com.basalbody.app.utils.ValidationStatus
import com.basalbody.app.utils.getText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordActivity : BaseActivity<AuthViewModel, ActivityResetPasswordBinding>() {

    private val TAG = "ResetPasswordActivity"
    private var emailOrPhone = ""
    private var resetToken = ""

    override fun getViewBinding(): ActivityResetPasswordBinding =
        ActivityResetPasswordBinding.inflate(layoutInflater)

    override fun addObservers() {
        lifecycleScope.launch {
            viewModel.callResetPasswordStep2ApiStateFlow.collect {
                FlowInActivity<BaseResponse<ResetPasswordStep2Response>>(
                    data = it,
                    context = this@ResetPasswordActivity,
                    shouldShowErrorMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleResetPasswordStep2Response
                )
            }
        }
    }

    override fun initSetup() {
        getDataFromIntent()
        setupUI()
    }

    override fun listeners() {
        binding.apply {
            llToolBar.ivBack.onNoSafeClick {
                finish()
            }

            btnSubmit.onSafeClick {
                if (allDetailsValid()) {
                    val request = ResetPasswordStep2Request().apply {
                        resetToken = this@ResetPasswordActivity.resetToken
                        if (viewModel.isSelectEmail) {
                            this.email = emailOrPhone
                        } else {
                            this.phoneNumber = emailOrPhone
                        }
                        this.newPassword = etPass.getText()?.trim() ?: ""
                        this.confirmPassword = etConfirmPass.getText()?.trim() ?: ""
                    }
                    viewModel.callResetPasswordStep2Api(request)
                }
            }
        }
    }

    private fun getDataFromIntent() {
        intent?.let {
            emailOrPhone = it.getStringExtra(Constants.BUNDLE_KEY_USER_EMAIL_OR_PHONE) ?: ""
            resetToken = it.getStringExtra(Constants.BUNDLE_KEY_RESET_TOKEN) ?: ""
            viewModel.isSelectEmail = it.getBooleanExtra(Constants.BUNDLE_KEY_IS_EMAIL, false)
            Logger.e(
                TAG,
                "getDataFromIntent: emailOrPhone: $emailOrPhone , resetToken: $resetToken, isSelectEmail: ${viewModel.isSelectEmail}"
            )
        }
    }

    private fun setupUI() {
        binding.apply {
            llToolBar.tvTitle.changeText(R.string.btn_reset_password)
        }
    }

    private fun allDetailsValid(): Boolean {
        binding.apply {
            val newPassword = etPass.getText()?.trim() ?: ""
            val confirmPassword = etConfirmPass.getText()?.trim() ?: ""
            return when {
                !newPassword(newPassword) -> {
                    false
                }

                !confirmPassword(confirmPassword) -> {
                    false
                }

                newPassword != confirmPassword -> {
                    viewModel.setValidationValue(ValidationStatus.NEW_PASSWORD_CONFIRM_PASS_NOT_SAME)
                    false
                }

                else -> true
            }
        }
    }

    private fun handleResetPasswordStep2Response(response: BaseResponse<ResetPasswordStep2Response>?) {
        if (response.notNull() && response?.status == true) {
            CommonSuccessBottomSheetDialog.newInstance(
                binding.root,
                this@ResetPasswordActivity,
                callBack = {
                    startNewActivity(
                        LoginActivity::class.java,
                        isClearAllStacks = true,
                        isFinish = true
                    )
                }).apply {
                title =
                    this@ResetPasswordActivity.getString(R.string.label_password_update_successfully)
                description =
                    this@ResetPasswordActivity.getString(R.string.message_password_reset_successfully)
                btnText = this@ResetPasswordActivity.getString(R.string.btn_back_to_login)
            }.show(supportFragmentManager, "PasswordResetSuccessBottomSheetDialog")
        }
    }

}