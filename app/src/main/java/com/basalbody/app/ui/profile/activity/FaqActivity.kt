package com.basalbody.app.ui.profile.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityConnectedBluetoothDeviceBinding
import com.basalbody.app.databinding.ActivityFaqBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.ui.profile.adapter.ConnectedBluetoothDevicesListAdapter
import com.basalbody.app.ui.profile.adapter.FaqListAdapter
import com.basalbody.app.ui.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FaqActivity : BaseActivity<ProfileViewModel, ActivityFaqBinding>() {

    private val TAG = "FaqActivity"

    override fun getViewBinding(): ActivityFaqBinding = ActivityFaqBinding.inflate(layoutInflater)

    private val faqAdapter by lazy {
        FaqListAdapter(
            arrayListOf("", "", "", "", "", "","", "", "", "", "", "","", "", "", "", "", "","", "", "", "", "", ""),
            ::onConnectDeviceClick
        )
    }

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            ViewCompat.setOnApplyWindowInsetsListener(main) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPaddingRelative(0,systemBars.top,0,0)
                rvFaq.updatePadding(bottom = systemBars.bottom)
                insets
            }
            llToolBar.tvTitle.changeText(R.string.lbl_faq)
            rvFaq.adapter = faqAdapter
        }
    }

    override fun listeners() {
        binding.apply {
            llToolBar.ivBack.onSafeClick {
                finish()
            }
        }
    }

    private fun onConnectDeviceClick(s: String) {
        Log.e(TAG, "onConnectDeviceClick()")
    }
}