package com.basalbody.app.ui.intro.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityIntroBinding
import com.basalbody.app.ui.intro.viewmodel.IntroViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : BaseActivity<IntroViewModel, ActivityIntroBinding>() {

    private val TAG = "IntroActivity"

    override fun getViewBinding(): ActivityIntroBinding  = ActivityIntroBinding.inflate(layoutInflater)

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {

        Log.e(TAG, "setupUI()")
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
    }

}