package com.basalbody.app.ui.auth.activity

import android.util.Log
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityForgotPasswordBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.extensions.visible
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import com.basalbody.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity<AuthViewModel, ActivityForgotPasswordBinding>(){

    private val TAG = "ForgotPasswordActivity"

    override fun getViewBinding(): ActivityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)

    override fun initSetup() {
        Log.e(TAG, "initSetup")
        when {
            intent.notNull() && intent.extras.notNull() -> {

                if (intent.hasExtra(Constants.INTENT_IS_EMAIL)){
                    viewModel.isSelectEmail = intent.getBooleanExtra(Constants.INTENT_IS_EMAIL, true)
                }

            }
        }

        setupUI()
    }

    private fun setupUI() {

        binding.apply {
            llToolBar.tvTitle.changeText(R.string.lbl_forgot_password)

            if (viewModel.isSelectEmail){
                etEmail.visible()
                etPhone.gone()
                tvMessage.changeText(R.string.lbl_please_enter_your_email_to_reset_the_password)
            }else{
                etPhone.visible()
                etEmail.gone()
                tvMessage.changeText(R.string.lbl_please_enter_your_phone_no_to_reset_the_password)
            }
        }
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
        binding.apply {

            llToolBar.ivBack.onSafeClick {
                finish()
            }

            btnResetPass.onSafeClick {
                startNewActivity(VerificationCodeActivity::class.java)
            }
        }
    }
}