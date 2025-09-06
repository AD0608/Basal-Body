package com.basalbody.app.ui.auth.activity

import android.os.CountDownTimer
import android.util.Log
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityVerificationCodeBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.setTextDecorator
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.extensions.visible
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import com.basalbody.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerificationCodeActivity :  BaseActivity<AuthViewModel, ActivityVerificationCodeBinding>(){

    private val TAG = "VerificationCodeActivity"

    override fun getViewBinding(): ActivityVerificationCodeBinding = ActivityVerificationCodeBinding.inflate(layoutInflater)

    override fun initSetup() {
        Log.e(TAG, "initSetup")
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            llToolBar.tvTitle.changeText(R.string.title_otp_verification)
            tvMessage.changeText("We just sent you a verified code via an email user@example.com")
            tvResend.setTextDecorator(resources.getString(R.string.re_send),
                R.color.colorText_263238,
                R.font.geist_semi_bold,
                allowCallback = true,
                callBack = {
                    Log.e(TAG, "setupUI Resend OTP")
                    startOtpTimer(Constants.OTP_TIMER_INTERVAL)
                })

        }
        startOtpTimer(Constants.OTP_TIMER_INTERVAL)
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
        binding.apply {
            llToolBar.ivBack.onSafeClick {
                finish()
            }
            btnSubmit.onSafeClick {
                startNewActivity(ResetPasswordActivity::class.java)
            }
        }
    }

    private fun startOtpTimer(timeInMillis: Long) {

        binding.apply {

            // disable until timer ends
            tvResend.gone()
            tvTimer.visible()

            viewModel.countDownTimer?.cancel() // cancel any existing timer

            viewModel.countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.countDownTimer?.cancel()
    }
}