package com.basalbody.app.ui.auth.activity

import android.util.Log
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityRegisterBinding
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : BaseActivity<AuthViewModel, ActivityRegisterBinding>() {

    private val TAG = "RegisterActivity"

    override fun getViewBinding(): ActivityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)

    override fun initSetup() {
        Log.e(TAG, "initSetup")
        binding.apply {

        }
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
    }
}