package com.basalbody.app.ui.profile.activity

import android.util.Log
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityContactSupportBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.ui.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactSupportActivity : BaseActivity<ProfileViewModel, ActivityContactSupportBinding>() {

    private val TAG = "ContactSupportActivity"
    override fun getViewBinding(): ActivityContactSupportBinding =
        ActivityContactSupportBinding.inflate(layoutInflater)

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        Log.e(TAG, "setupUI()")
        binding.apply {
            llToolBar.tvTitle.changeText(R.string.contact_support)
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