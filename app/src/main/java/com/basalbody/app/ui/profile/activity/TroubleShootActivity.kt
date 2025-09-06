package com.basalbody.app.ui.profile.activity

import android.util.Log
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityTroubleShootBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.ui.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TroubleShootActivity : BaseActivity<ProfileViewModel, ActivityTroubleShootBinding>() {

    private val TAG = "TroubleShootActivity"
    override fun getViewBinding(): ActivityTroubleShootBinding =
        ActivityTroubleShootBinding.inflate(layoutInflater)

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        Log.e(TAG, "setupUI()")
        binding.apply {
            llToolBar.tvTitle.changeText(R.string.title_bluetooth_device_troubleshooting)
        }
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
        binding.apply {

            llToolBar.ivBack.onSafeClick {
                finish()
            }
            btnSubmit.onSafeClick {
                finish()
            }
        }
    }
}