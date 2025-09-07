package com.basalbody.app.ui.home.fragment

import com.basalbody.app.R
import com.basalbody.app.base.BaseFragment
import com.basalbody.app.databinding.FragmentProfileBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.ui.home.activity.NotificationsActivity
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import com.basalbody.app.ui.profile.activity.ChangePasswordActivity
import com.basalbody.app.ui.profile.activity.ConnectedBluetoothDeviceActivity
import com.basalbody.app.ui.profile.activity.ContactSupportActivity
import com.basalbody.app.ui.profile.activity.EditProfileActivity
import com.basalbody.app.ui.profile.activity.FaqActivity
import com.basalbody.app.ui.profile.activity.TroubleShootActivity
import com.basalbody.app.ui.setting.activity.WebViewActivity

class ProfileFragment :
    BaseFragment<HomeViewModel, FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    override fun getViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            llToolBar.ivBack.gone()
            llToolBar.tvTitle.changeText(R.string.item_profile)
            tvUserName.changeText("Selly Mal")
            tvUserEmail.changeText("selly@gmail.com")
        }
    }

    override fun listeners() {
        binding.apply {

            ivEditProfile.onSafeClick {
                startNewActivity(EditProfileActivity::class.java)
            }
            llChangePass.onSafeClick {
                startNewActivity(ChangePasswordActivity::class.java)
            }
            llLanguage.onSafeClick {

            }
            lLConnectBluetooth.onSafeClick {
                startNewActivity(ConnectedBluetoothDeviceActivity::class.java)
            }
            llDataPrivacy.onSafeClick {
                startNewActivity(WebViewActivity::class.java)
            }
            llFAQ.onSafeClick {
                startNewActivity(FaqActivity::class.java)
            }
            llContactSupport.onSafeClick {
                startNewActivity(ContactSupportActivity::class.java)
            }
            llTroubleShoot.onSafeClick {
                startNewActivity(TroubleShootActivity::class.java)
            }
            llNotification.onSafeClick {
                startNewActivity(NotificationsActivity::class.java)
            }
            llLogout.onSafeClick {

            }
            llDeleteAccount.onSafeClick {

            }

        }
    }
}