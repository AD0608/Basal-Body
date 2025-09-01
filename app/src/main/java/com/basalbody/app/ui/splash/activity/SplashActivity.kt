package com.basalbody.app.ui.splash.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivitySplashBinding
import com.basalbody.app.ui.home.activity.HomeActivity
import com.basalbody.app.ui.intro.activity.IntroActivity
import com.basalbody.app.ui.splash.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {

    private var TAG = "SplashActivity"

    override fun getViewBinding(): ActivitySplashBinding  = ActivitySplashBinding.inflate(layoutInflater)

    override fun initSetup() {
        setupUI()
    }

    fun setupUI() {
        Log.e(TAG, "setupUI")
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")

        lifecycleScope.launch {
            delay(3000L)
            startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            finish()
        }
    }
}