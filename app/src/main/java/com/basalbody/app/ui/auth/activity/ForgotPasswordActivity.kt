package com.basalbody.app.ui.auth.activity

import android.util.Log
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityForgotPasswordBinding
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel

class ForgotPasswordActivity : BaseActivity<AuthViewModel, ActivityForgotPasswordBinding>(){

    private val TAG = "ForgotPasswordActivity"

    override fun getViewBinding(): ActivityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)

    override fun initSetup() {
        Log.e(TAG, "initSetup")
        binding.apply {

        }
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
    }
}