package com.basalbody.app.ui.auth.activity

import android.content.Intent
import android.util.Log
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityLoginBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.setTextDecorator
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.ui.auth.viewmodel.AuthViewModel
import com.basalbody.app.ui.home.activity.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<AuthViewModel, ActivityLoginBinding>() {

    private val TAG = "LoginActivity"

    override fun getViewBinding(): ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)

    override fun initSetup() {
        Log.e(TAG, "initSetup")
        setupUI()
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

    override fun listeners() {
        Log.e(TAG, "listeners()")
        binding.apply {

            tvForgotPass.onSafeClick {
                startActivity(Intent(this@LoginActivity, ForgotPasswordOptionActivity::class.java))
            }

            btnLogin.onSafeClick {
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            }
        }
    }

}