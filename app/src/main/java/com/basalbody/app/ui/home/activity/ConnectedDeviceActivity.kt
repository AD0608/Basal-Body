package com.basalbody.app.ui.home.activity

import androidx.core.graphics.toColorInt
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityConnectedDeviceBinding
import com.basalbody.app.extensions.addRippleWaves
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConnectedDeviceActivity : BaseActivity<HomeViewModel, ActivityConnectedDeviceBinding>() {
    override fun getViewBinding(): ActivityConnectedDeviceBinding =
        ActivityConnectedDeviceBinding.inflate(layoutInflater)

    override fun initSetup() {
        binding.apply {
            imgBluetooth.addRippleWaves(color = "#46B74F".toColorInt())
            toolBar.tvTitle.changeText(getString(R.string.label_connected_device))
        }
    }

    override fun listeners() {
        binding.apply {
            toolBar.ivBack onSafeClick {
                onBackPressedDispatcher.onBackPressed()
            }

            btnChangeConnectionStatus onSafeClick {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

}