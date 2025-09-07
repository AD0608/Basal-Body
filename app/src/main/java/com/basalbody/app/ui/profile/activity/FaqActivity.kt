package com.basalbody.app.ui.profile.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityConnectedBluetoothDeviceBinding
import com.basalbody.app.databinding.ActivityFaqBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.ui.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FaqActivity : BaseActivity<ProfileViewModel, ActivityFaqBinding>() {

    private val TAG = "FaqActivity"

    override fun getViewBinding(): ActivityFaqBinding = ActivityFaqBinding.inflate(layoutInflater)

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            llToolBar.tvTitle.changeText(R.string.lbl_faq)
        }
    }

    override fun listeners() {
        binding.apply {
            llToolBar.ivBack.onSafeClick {
                finish()
            }
        }
    }
}