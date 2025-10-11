package com.basalbody.app.ui.auth.activity

import android.os.Bundle
import android.util.Log
import androidx.compose.ui.text.toUpperCase
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.base.FlowInActivity
import com.basalbody.app.databinding.ActivityRegisterBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.putEnum
import com.basalbody.app.extensions.setTextDecorator
import com.basalbody.app.extensions.showToast
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.request.RegisterRequest
import com.basalbody.app.model.response.UserResponse
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import com.basalbody.app.ui.common.showGenderSelectionPopup
import com.basalbody.app.ui.home.activity.HomeActivity
import com.basalbody.app.ui.profile.activity.WebViewActivity
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.Constants.EMAIL_PATTERN
import com.basalbody.app.utils.EnumUtils
import com.basalbody.app.utils.LimitCount
import com.basalbody.app.utils.ValidationStatus
import com.basalbody.app.utils.getText
import com.basalbody.app.utils.getTextTextView
import com.basalbody.app.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class RegisterActivity : BaseActivity<AuthViewModel, ActivityRegisterBinding>() {

    private val TAG = "RegisterActivity"

    override fun getViewBinding(): ActivityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)

    override fun initSetup() {
        Log.e(TAG, "initSetup")
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            llToolBar.tvTitle.changeText(R.string.register)
            tvTermsCondition.setTextDecorator(resources.getString(R.string.terms_conditions),
                R.color.colorText_263238,
                R.font.geist_bold,
                true,
                true,
                callBack = {
                    Log.e(TAG, "setupUI() Terms and Condition")
                    val bundle = Bundle().apply {
                        putEnum(Constants.BUNDLE_KEY_WHICH_WEB_VIEW, EnumUtils.WebView.TERMS_AND_CONDITIONS)
                    }

                    startNewActivity(WebViewActivity::class.java, bundle= bundle)
                })
            tvSignInNow.setTextDecorator(resources.getString(R.string.sign_in_now),
                R.color.colorText_309C34,
                R.font.geist_bold,
                allowCallback = true,
                callBack = {
                    Log.e(TAG, "setupUI() Sign in Now")
                    onBackPressedDispatcher.onBackPressed()
                })
        }
        callTermsConditionCheck()
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
        binding.apply {

            llToolBar.ivBack.onSafeClick {
                finish()
            }

            etGender.onSafeClick {
                showGenderSelectionPopup(this@RegisterActivity,
                    etGender, callback = {
                        etGender.txtField.text = it
                    })
            }

            btnRegister.onSafeClick {
                if (allDetailsValid()) {
                    val request = RegisterRequest(
                        fullName = etFullName.getText()?.trim().toString(),
                        email = etEmail.getText()?.trim().toString(),
                        gender = etGender.getTextTextView().toString().uppercase(),
                        phoneNumber = "${etPhone.tvPhoneNumberCode.text.trim()}${etPhone.getText().toString().uppercase()}",
                        password = etPass.getText()?.trim().toString()
                    )
                    viewModel.callRegisterApi(request)
                }
            }

            ivCheck.onSafeClick {
                viewModel.isTermsConditionCheck = !viewModel.isTermsConditionCheck
                callTermsConditionCheck()
            }
        }
    }

    override fun addObservers() {
        lifecycleScope.launch {
            viewModel.callRegisterApiStateFlow.collect {
                FlowInActivity<BaseResponse<UserResponse>>(
                    data = it,
                    context = this@RegisterActivity,
                    shouldShowErrorMessage = true,
                    shouldShowSuccessMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleRegisterResponse,
                )
            }
        }
    }

    private fun handleRegisterResponse(userResponse: BaseResponse<UserResponse>?) {
        if (userResponse.notNull() && userResponse?.status == true) {
            startNewActivity(HomeActivity::class.java, isFinish = true)
        } else {
            showSnackBar(userResponse?.message ?: "", Constants.STATUS_ERROR, this)
        }
    }

    private fun callTermsConditionCheck(){
        val image = if (viewModel.isTermsConditionCheck) R.drawable.ic_select_check_box else R.drawable.ic_unselect_check_box
        binding.ivCheck.setImageResource(image)
    }

    private fun allDetailsValid(): Boolean {

        val fullName = binding.etFullName.getText()?.trim() ?: ""
        val email = binding.etEmail.getText()?.trim() ?: ""
        val gender = binding.etGender.getTextTextView()?.trim() ?: ""
        val phone = binding.etPhone.getText()?.trim() ?: ""
        val pass = binding.etPass.getText()?.toString() ?: ""
        val confirmPassword = binding.etConfirmPass.getText()?.toString() ?: ""

        binding.apply {
            return when {
                fullName.isEmpty() -> {
                    viewModel.setValidationValue(ValidationStatus.EMPTY_NAME)
                    false
                }
                email.isEmpty() -> {
                    viewModel.setValidationValue(ValidationStatus.EMPTY_EMAIL)
                    false
                }
                email.trim()?.matches(EMAIL_PATTERN.toRegex()) == false -> {
                    viewModel.setValidationValue(ValidationStatus.INVALID_EMAIL)
                    false
                }
                gender.isEmpty() -> {
                    viewModel.setValidationValue(ValidationStatus.EMPTY_GENDER)
                    false
                }
                phone.isEmpty() -> {
                    viewModel.setValidationValue(ValidationStatus.EMPTY_PHONE)
                    false
                }
                phone.trim().length < LimitCount.phoneMin -> {
                    viewModel.setValidationValue(ValidationStatus.PHONE_LENGTH)
                    false
                }
                !password(pass) ->  false
                !confirmPassword(confirmPassword) -> false
                pass != confirmPassword -> {
                    viewModel.setValidationValue(ValidationStatus.PASSWORD_CONFIRM_PASS_SAME)
                    false
                }
                !viewModel.isTermsConditionCheck -> {
                    viewModel.setValidationValue(ValidationStatus.TERMS_CONDITION)
                    false
                }

                else -> true
            }
        }
    }
}