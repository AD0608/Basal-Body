package com.basalbody.app.ui.home.fragment

import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseFragment
import com.basalbody.app.base.FlowInFragment
import com.basalbody.app.databinding.FragmentProfileBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.gone
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.startNewActivity
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.response.LogoutResponse
import com.basalbody.app.ui.auth.activity.LoginActivity
import com.basalbody.app.ui.common.CommonConfirmationBottomSheetDialog
import com.basalbody.app.ui.common.CommonSuccessBottomSheetDialog
import com.basalbody.app.ui.common.showLanguageSelectionPopup
import com.basalbody.app.ui.home.activity.NotificationsActivity
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import com.basalbody.app.ui.profile.activity.ChangePasswordActivity
import com.basalbody.app.ui.profile.activity.ConnectedBluetoothDeviceActivity
import com.basalbody.app.ui.profile.activity.ContactSupportActivity
import com.basalbody.app.ui.profile.activity.EditProfileActivity
import com.basalbody.app.ui.profile.activity.FaqActivity
import com.basalbody.app.ui.profile.activity.TroubleShootActivity
import com.basalbody.app.ui.profile.activity.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<HomeViewModel, FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    override fun getViewBinding(): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater)

    override fun addObserver() {
        lifecycleScope.launch {
            viewModel.callLogoutApiStateFlow.collect {
                FlowInFragment<BaseResponse<LogoutResponse>>(
                    data = it,
                    fragment = this@ProfileFragment,
                    shouldShowErrorMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleLogoutResponse,
                )
            }
        }
    }

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
                CommonConfirmationBottomSheetDialog.newInstance(
                    root,
                    requireActivity(),
                    callBack = {
                        viewModel.callLogoutApi()
                    }).apply {
                    title = this@ProfileFragment.getString(R.string.label_logout)
                    description =
                        this@ProfileFragment.getString(R.string.message_logout_confirmation)
                    positiveBtnText = this@ProfileFragment.getString(R.string.btn_logout)
                }.show(childFragmentManager, "LogoutConfirmationBottomSheetDialog")
            }
            llDeleteAccount.onSafeClick {
                CommonConfirmationBottomSheetDialog.newInstance(
                    root,
                    requireActivity(),
                    callBack = {
                        openDeleteAccountSuccessPopup()
                    }).apply {
                    title = this@ProfileFragment.getString(R.string.label_delete_account)
                    description =
                        this@ProfileFragment.getString(R.string.message_delete_account_confirmation)
                    positiveBtnText = this@ProfileFragment.getString(R.string.btn_delete)
                }.show(childFragmentManager, "DeleteAccountConfirmationBottomSheetDialog")
            }

            tvCurrentLanguage onSafeClick {
                showLanguageSelectionPopup(requireActivity(), tvCurrentLanguage)
            }

        }
    }

    private fun handleLogoutResponse(response: BaseResponse<LogoutResponse>?) {
        if (response.notNull() && response?.status == true) {
            openLogoutSuccessPopup()
        }
    }

    private fun openDeleteAccountSuccessPopup() {
        CommonSuccessBottomSheetDialog.newInstance(binding.root, requireActivity(), callBack = {
            startNewActivity(LoginActivity::class.java, isClearAllStacks = true)
        }).apply {
            title = this@ProfileFragment.getString(R.string.label_account_deleted)
            description = this@ProfileFragment.getString(R.string.message_account_deleted_success)
            btnText = this@ProfileFragment.getString(R.string.btn_okay)
        }.show(childFragmentManager, "DeleteAccountSuccessBottomSheetDialog")
    }

    private fun openLogoutSuccessPopup() {
        CommonSuccessBottomSheetDialog.newInstance(binding.root, requireActivity(), callBack = {
            navigateToLoginScreen()
        }).apply {
            title = this@ProfileFragment.getString(R.string.label_logout_successfully)
            description = this@ProfileFragment.getString(R.string.message_logout_successfully)
            btnText = this@ProfileFragment.getString(R.string.btn_okay)
        }.show(childFragmentManager, "LogoutSuccessBottomSheetDialog")
    }
}