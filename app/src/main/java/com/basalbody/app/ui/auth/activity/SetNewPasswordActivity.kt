package com.basalbody.app.ui.auth.activity

import android.util.Log
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivitySetNewPasswordBinding
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel

class SetNewPasswordActivity  : BaseActivity<AuthViewModel, ActivitySetNewPasswordBinding>(){

    private val TAG = "SetNewPasswordActivity"

    override fun getViewBinding(): ActivitySetNewPasswordBinding = ActivitySetNewPasswordBinding.inflate(layoutInflater)

    override fun initSetup() {
        Log.e(TAG, "initSetup")
        binding.apply {

        }
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
    }
}