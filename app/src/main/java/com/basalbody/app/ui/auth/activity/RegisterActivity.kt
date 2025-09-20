package com.basalbody.app.ui.auth.activity

import android.util.Log
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityRegisterBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.setTextDecorator
import com.basalbody.app.extensions.showToast
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.model.request.LoginRequest
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import com.basalbody.app.ui.profile.activity.ChangePasswordActivity
import com.basalbody.app.ui.profile.activity.WebViewActivity
import com.basalbody.app.utils.Constants.EMAIL_PATTERN
import com.basalbody.app.utils.ValidationStatus
import com.basalbody.app.utils.getText
import dagger.hilt.android.AndroidEntryPoint

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
                    startNewActivity(WebViewActivity::class.java)
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
                showToast("Select Gender")
            }

            btnRegister.onSafeClick {
                if (allDetailsValid()) {
                    val request = LoginRequest(
                        email = etEmail.getText()?.trim().toString(),
                        password = etPass.getText()?.trim().toString(),
                    )
                    viewModel.callLoginApi(request)
                }
            }

            ivCheck.onSafeClick {
                viewModel.isTermsConditionCheck = !viewModel.isTermsConditionCheck
                callTermsConditionCheck()
            }
        }
    }

    private fun callTermsConditionCheck(){
        val image = if (viewModel.isTermsConditionCheck) R.drawable.ic_select_check_box else R.drawable.ic_unselect_check_box
        binding.ivCheck.setImageResource(image)
    }

    private fun allDetailsValid(): Boolean {
        binding.apply {
            return when {
                etEmail.getText()?.trim()?.isEmpty() == true -> {
                    viewModel.setValidationValue(ValidationStatus.EMPTY_EMAIL)
                    false
                }

                etEmail.getText()?.trim()?.matches(EMAIL_PATTERN.toRegex()) == false -> {
                    viewModel.setValidationValue(ValidationStatus.INVALID_EMAIL)
                    false
                }

                !password(etPass.getText()?.trim() ?: "") -> {
                    false
                }

                else -> true
            }
        }
    }
}