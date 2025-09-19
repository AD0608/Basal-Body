package com.basalbody.app.ui.auth.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.base.FlowInActivity
import com.basalbody.app.databinding.ActivityForgotPasswordBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.extensions.visible
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.request.ForgotPasswordRequest
import com.basalbody.app.model.response.ForgotPasswordResponse
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.LimitCount
import com.basalbody.app.utils.ValidationStatus
import com.basalbody.app.utils.getCountryCode
import com.basalbody.app.utils.getText
import com.basalbody.app.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity<AuthViewModel, ActivityForgotPasswordBinding>() {

    private val TAG = "ForgotPasswordActivity"

    override fun getViewBinding(): ActivityForgotPasswordBinding =
        ActivityForgotPasswordBinding.inflate(layoutInflater)

    override fun addObservers() {
        lifecycleScope.launch {
            viewModel.callForgotPasswordApiStateFlow.collect {
                FlowInActivity<BaseResponse<ForgotPasswordResponse>>(
                    data = it,
                    context = this@ForgotPasswordActivity,
                    shouldShowErrorMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleForgotPasswordResponse,
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

            llToolBar.ivBack.onSafeClick {
                finish()
            }

            btnResetPass.onSafeClick {
                if (allDetailsValid()) {
                    val request = ForgotPasswordRequest().apply {
                        if (viewModel.isSelectEmail) {
                            email = etEmail.getText()?.trim().toString()
                        } else {
                            phoneNumber =
                                etPhone.getCountryCode() + etPhone.getText()?.trim().toString()
                        }
                    }
                    viewModel.callForgotPasswordApi(request)
                }
            }
        }
    }

    private fun getDataFromIntent() {
        when {
            intent.notNull() && intent.extras.notNull() -> {
                if (intent.hasExtra(Constants.BUNDLE_KEY_IS_EMAIL)) {
                    viewModel.isSelectEmail =
                        intent.getBooleanExtra(Constants.BUNDLE_KEY_IS_EMAIL, true)
                }
            }
        }
    }

    private fun setupUI() {
        binding.apply {
            llToolBar.tvTitle.changeText(R.string.lbl_forgot_password)

            if (viewModel.isSelectEmail) {
                etEmail.visible()
                etPhone.gone()
                tvMessage.changeText(R.string.lbl_please_enter_your_email_to_reset_the_password)
            } else {
                etPhone.visible()
                etEmail.gone()
                tvMessage.changeText(R.string.lbl_please_enter_your_phone_no_to_reset_the_password)
            }
        }
    }

    private fun handleForgotPasswordResponse(response: BaseResponse<ForgotPasswordResponse>?) {
        if (response.notNull() && response?.status == true) {
            showSnackBar(response.data?.message ?: "", Constants.STATUS_SUCCESSFUL, this)
            val bundle = Bundle()
            bundle.putBoolean(Constants.BUNDLE_KEY_IS_EMAIL, viewModel.isSelectEmail)
            bundle.putString(
                Constants.BUNDLE_KEY_USER_EMAIL_OR_PHONE,
                if (viewModel.isSelectEmail) binding.etEmail.getText()?.trim()
                    .toString() else binding.etPhone.getCountryCode() + binding.etPhone.getText()
                    ?.trim().toString()
            )
            startNewActivity(VerificationCodeActivity::class.java, bundle = bundle)
        }
    }

    private fun allDetailsValid(): Boolean {
        binding.apply {
            return if (viewModel.isSelectEmail) {
                if (etEmail.getText()?.trim()?.isEmpty() == true) {
                    viewModel.setValidationValue(ValidationStatus.EMPTY_EMAIL)
                    false
                } else if (!etEmail.getText().toString().trim()
                        .matches(Constants.EMAIL_PATTERN.toRegex())
                ) {
                    viewModel.setValidationValue(ValidationStatus.INVALID_EMAIL)
                    false
                } else {
                    true
                }
            } else {
                if (etPhone.getText().toString().trim().isEmpty()) {
                    viewModel.setValidationValue(ValidationStatus.EMPTY_PHONE)
                    false
                } else if (etPhone.getText().toString().trim().length < LimitCount.phoneMin) {
                    viewModel.setValidationValue(ValidationStatus.PHONE_LENGTH)
                    false
                } else {
                    true
                }
            }
        }
    }
}