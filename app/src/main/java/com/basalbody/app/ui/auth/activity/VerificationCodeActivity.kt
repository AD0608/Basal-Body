package com.basalbody.app.ui.auth.activity

import android.util.Log
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityVerificationCodeBinding
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel

class VerificationCodeActivity :  BaseActivity<AuthViewModel, ActivityVerificationCodeBinding>(){

    private val TAG = "VerificationCodeActivity"

    override fun getViewBinding(): ActivityVerificationCodeBinding = ActivityVerificationCodeBinding.inflate(layoutInflater)

    override fun initSetup() {
        Log.e(TAG, "initSetup")
        binding.apply {

        }
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
    }
}