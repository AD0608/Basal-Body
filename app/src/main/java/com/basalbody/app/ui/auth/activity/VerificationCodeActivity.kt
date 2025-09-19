package com.basalbody.app.ui.auth.activity

import android.os.CountDownTimer
import android.text.Html
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.base.FlowInActivity
import com.basalbody.app.databinding.ActivityVerificationCodeBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.setTextDecorator
import com.basalbody.app.extensions.shake
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.extensions.visible
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.request.ResendOtpRequest
import com.basalbody.app.model.request.ResetPasswordStep1Request
import com.basalbody.app.model.response.ResendOtpResponse
import com.basalbody.app.model.response.ResetPasswordStep1Response
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.LimitCount
import com.basalbody.app.utils.Logger
import com.basalbody.app.utils.ValidationStatus
import com.basalbody.app.utils.otp.view.OtpView
import com.basalbody.app.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerificationCodeActivity : BaseActivity<AuthViewModel, ActivityVerificationCodeBinding>() {

    private val TAG = "VerificationCodeActivity"
    private var emailOrPhone = ""
    private var enteredOtp = ""

    override fun getViewBinding(): ActivityVerificationCodeBinding =
        ActivityVerificationCodeBinding.inflate(layoutInflater)

    override fun addObservers() {
        lifecycleScope.launch {
            viewModel.callResendOtpApiStateFlow.collect {
                FlowInActivity<BaseResponse<ResendOtpResponse>>(
                    data = it,
                    context = this@VerificationCodeActivity,
                    shouldShowErrorMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleResendOtpResponse,
                )
            }
        }

        lifecycleScope.launch {
            viewModel.callResetPasswordStep1ApiStateFlow.collect {
                FlowInActivity<BaseResponse<ResetPasswordStep1Response>>(
                    data = it,
                    context = this@VerificationCodeActivity,
                    shouldShowErrorMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleResetPasswordStep1Response,
                    onError = ::handleResetPasswordStep1Error
                )
            }
        }
    }

    override fun initSetup() {
        getDataFromIntent()
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            llToolBar.tvTitle.changeText(R.string.title_otp_verification)
            val message = Html.fromHtml(
                "We just sent you a verified code via ${if (viewModel.isSelectEmail) "an email <u>$emailOrPhone</u>" else "phone number <u>$emailOrPhone</u>"}",
                Html.FROM_HTML_MODE_LEGACY
            )
            tvMessage.text = message
            tvResend.setTextDecorator(
                resources.getString(R.string.re_send),
                R.color.colorText_263238,
                R.font.geist_semi_bold,
                allowCallback = true,
                callBack = {
                    startOtpTimer()
                })

        }
        startOtpTimer()
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
        binding.apply {
            llToolBar.ivBack.onSafeClick {
                finish()
            }

            viewOtp.setTextChangeListener(object : OtpView.ChangeListener {
                override fun onTextChange(value: String, completed: Boolean) {
                    enteredOtp = value
                }
            })

            btnSubmit.onSafeClick {
                if (enteredOtp.isEmpty()) {
                    viewModel.setValidationValue(ValidationStatus.EMPTY_OTP)
                } else if (enteredOtp.length < LimitCount.otpLength) {
                    viewModel.setValidationValue(ValidationStatus.INVALID_OTP)
                } else {
                    val request = ResetPasswordStep1Request().apply {
                        otp = enteredOtp
                        if (viewModel.isSelectEmail) {
                            email = emailOrPhone
                        } else {
                            phoneNumber = emailOrPhone
                        }
                    }
                    viewModel.callResetPasswordStep1Api(request)
                }
                //startNewActivity(ResetPasswordActivity::class.java)
            }

            tvResend onSafeClick {
                if (tvResend.isVisible) {
                    val request = ResendOtpRequest().apply {
                        type = "forgot_password_otp"
                        if (viewModel.isSelectEmail) {
                            email = emailOrPhone
                        } else {
                            phoneNumber = emailOrPhone
                        }
                    }
                    viewModel.callResendOtpApi(request)
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
                if (intent.hasExtra(Constants.BUNDLE_KEY_USER_EMAIL_OR_PHONE)) {
                    emailOrPhone =
                        intent.getStringExtra(Constants.BUNDLE_KEY_USER_EMAIL_OR_PHONE)
                            .toString()
                }
                Logger.e(
                    TAG,
                    "getDataFromIntent: isSelectEmail: ${viewModel.isSelectEmail}, emailOrPhone: $emailOrPhone"
                )
            }
        }
    }

    private fun startOtpTimer() {
        binding.apply {
            tvResend.gone()
            tvTimer.visible()
            viewModel.countDownTimer?.cancel()
            viewModel.countDownTimer = object : CountDownTimer(Constants.OTP_TIMER_INTERVAL, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = (millisUntilFinished / 1000) % 60
                    val minutes = (millisUntilFinished / 1000) / 60
                    tvTimer.changeText("${String.format("%02d:%02d", minutes, seconds)} sec")
                }

                override fun onFinish() {
                    tvTimer.changeText("00:00 sec")
                    tvTimer.gone()
                    tvResend.visible() // enable resend
                }
            }.start()
        }
    }

    private fun handleResendOtpResponse(response: BaseResponse<ResendOtpResponse>?) {
        if (response.notNull() && response?.status == true) {
            showSnackBar(response.data?.message ?: "", Constants.STATUS_SUCCESSFUL, this)
            startOtpTimer()
        }
    }

    private fun handleResetPasswordStep1Response(response: BaseResponse<ResetPasswordStep1Response>?) {
        if (response.notNull() && response?.status == true) {
            showSnackBar(response.message, Constants.STATUS_SUCCESSFUL, this)
            val bundle = android.os.Bundle().apply {
                putString(Constants.BUNDLE_KEY_USER_EMAIL_OR_PHONE, emailOrPhone)
                putString(Constants.BUNDLE_KEY_RESET_TOKEN, response.data?.resetToken ?: "")
                putBoolean(Constants.BUNDLE_KEY_IS_EMAIL, viewModel.isSelectEmail)
            }
            startNewActivity(ResetPasswordActivity::class.java, bundle = bundle)
        }
    }

    private fun handleResetPasswordStep1Error(response: BaseResponse<ResetPasswordStep1Response>??) {
        binding.viewOtp.setText("")
        binding.viewOtp.shake()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.countDownTimer?.cancel()
    }
}