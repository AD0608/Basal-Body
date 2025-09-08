package com.basalbody.app.ui.auth.activity

import android.util.Log
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityResetPasswordBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onNoSafeClick
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.ui.common.CommonSuccessBottomSheetDialog
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordActivity  : BaseActivity<AuthViewModel, ActivityResetPasswordBinding>(){

    private val TAG = "ResetPasswordActivity"

    override fun getViewBinding(): ActivityResetPasswordBinding = ActivityResetPasswordBinding.inflate(layoutInflater)

    override fun initSetup() {
        Log.e(TAG, "initSetup")
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            llToolBar.tvTitle.changeText(R.string.btn_reset_password)
        }
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
        binding.apply {
            llToolBar.ivBack.onNoSafeClick {
                finish()
            }

            btnSubmit.onSafeClick {
                CommonSuccessBottomSheetDialog.newInstance(root, this@ResetPasswordActivity, callBack =  {
                    startNewActivity(LoginActivity::class.java, isClearAllStacks = true, isFinish = true)
                }).apply {
                    title = this@ResetPasswordActivity.getString(R.string.label_password_update_successfully)
                    description = this@ResetPasswordActivity.getString(R.string.message_password_reset_successfully)
                    btnText = this@ResetPasswordActivity.getString(R.string.btn_back_to_login)
                }.show(supportFragmentManager, "PasswordResetSuccessBottomSheetDialog")
            }
        }
    }
}