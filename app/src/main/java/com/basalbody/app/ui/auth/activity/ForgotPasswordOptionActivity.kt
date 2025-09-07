package com.basalbody.app.ui.auth.activity

import android.os.Bundle
import android.util.Log
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityForgotPasswordOptionBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import com.basalbody.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordOptionActivity : BaseActivity<AuthViewModel, ActivityForgotPasswordOptionBinding>(){

    private val TAG = "ForgotPasswordOptionActivity"

    override fun getViewBinding(): ActivityForgotPasswordOptionBinding = ActivityForgotPasswordOptionBinding.inflate(layoutInflater)

    override fun initSetup() {
        Log.e(TAG, "initSetup")
        setupUI()
    }

    private fun setupUI() {

        binding.apply {
            llToolBar.tvTitle.changeText(R.string.lbl_forgot_password)
            tvEmailValue.changeText("****@gmail.com")
            tvPhoneValue.changeText("**** 2345")
        }
        selectedOption(false)
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
        binding.apply {

            llToolBar.ivBack.onSafeClick {
                finish()
            }

            cardEmail.onSafeClick {
                selectedOption(true)
            }

            cardPhone.onSafeClick {
                selectedOption(false)
            }

            btnResetPass.onSafeClick {
                val bundle = Bundle()
                bundle.putBoolean(Constants.INTENT_IS_EMAIL, viewModel.isSelectEmail)
                startNewActivity(ForgotPasswordActivity::class.java, bundle = bundle)
            }
        }
    }

    private fun selectedOption(isEmail : Boolean){
        viewModel.isSelectEmail = isEmail
        binding.apply {
            if (isEmail){
                ivPhoneSelect.setImageResource(R.drawable.ic_unselect)
                ivEmailSelect.setImageResource(R.drawable.ic_select)
            }else{
                ivPhoneSelect.setImageResource(R.drawable.ic_select)
                ivEmailSelect.setImageResource(R.drawable.ic_unselect)
            }
        }
    }
}