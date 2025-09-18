package com.basalbody.app.ui.auth.activity

import android.content.Intent
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.base.FlowInActivity
import com.basalbody.app.databinding.ActivityLoginBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.setTextDecorator
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.request.LoginRequest
import com.basalbody.app.model.response.LoginResponse
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import com.basalbody.app.ui.home.activity.HomeActivity
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.Constants.EMAIL_PATTERN
import com.basalbody.app.utils.ValidationStatus
import com.basalbody.app.utils.getText
import com.basalbody.app.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<AuthViewModel, ActivityLoginBinding>() {

    private val TAG = "LoginActivity"

    override fun getViewBinding(): ActivityLoginBinding =
        ActivityLoginBinding.inflate(layoutInflater)

    override fun addObservers() {
        lifecycleScope.launch {
            viewModel.callLoginApiStateFlow.collect {
                FlowInActivity<BaseResponse<LoginResponse>>(
                    data = it,
                    context = this@LoginActivity,
                    shouldShowErrorMessage = true,
                    shouldShowSuccessMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleLoginResponse,
                )
            }
        }
    }

    override fun initSetup() {
        Log.e(TAG, "initSetup")
        setupUI()
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
        binding.apply {

            tvForgotPass.onSafeClick {
                startActivity(Intent(this@LoginActivity, ForgotPasswordOptionActivity::class.java))
            }

            btnLogin.onSafeClick {
                if (allDetailsValid()) {
                    val request = LoginRequest(
                        email = etEmail.getText()?.trim().toString(),
                        password = etPass.getText()?.trim().toString(),
                    )
                    viewModel.callLoginApi(request)
                }
                //startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            }
        }
    }

    private fun setupUI() {
        binding.apply {
            llToolBar.ivBack.gone()
            llToolBar.tvTitle.changeText(R.string.lbl_login)
            tvRegisterNow.setTextDecorator(
                resources.getString(R.string.register_now),
                R.color.colorText_309C34,
                textFont = R.font.geist_bold,
                allowCallback = true,
                callBack = {
                    startNewActivity(RegisterActivity::class.java)
                },
            )
        }
    }

    private fun handleLoginResponse(loginResponse: BaseResponse<LoginResponse>?) {
        if (loginResponse.notNull()) {
            if (loginResponse?.status == true) {
                startNewActivity(HomeActivity::class.java, isFinish = true)
            } else {
                showSnackBar(loginResponse?.message ?: "", Constants.STATUS_ERROR, this)
            }
        }
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