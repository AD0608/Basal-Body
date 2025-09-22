package com.basalbody.app.ui.profile.activity

import android.util.Log
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityEditProfileBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.ui.common.showGenderSelectionPopup
import com.basalbody.app.ui.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileActivity : BaseActivity<ProfileViewModel, ActivityEditProfileBinding>() {

    private val TAG = "EditProfileActivity"
    override fun getViewBinding(): ActivityEditProfileBinding =
        ActivityEditProfileBinding.inflate(layoutInflater)

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        Log.e(TAG, "setupUI()")
        binding.apply {
            llToolBar.tvTitle.changeText(getString(R.string.title_edit_profile))

            etFullName.editText.changeText(localDataRepository.getUserDetails()?.user?.fullname ?: "")
            etEmail.editText.changeText(localDataRepository.getUserDetails()?.user?.email ?: "")
            var stGender  = localDataRepository.getUserDetails()?.user?.gender ?: ""
            etGender.txtField.changeText(stGender.lowercase().replaceFirstChar { it.titlecase() })
        }
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
        binding.apply {

            llToolBar.ivBack.onSafeClick {
                finish()
            }
            etGender.onSafeClick {
                showGenderSelectionPopup(this@EditProfileActivity,
                    etGender, callback = {
                        etGender.txtField.text = it
                    })
            }
            btnUpdate.onSafeClick {
                finish()
            }
        }
    }
}