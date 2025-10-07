package com.basalbody.app.ui.home.fragment

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
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
import com.basalbody.app.model.response.DeleteUserResponse
import com.basalbody.app.model.response.LogoutResponse
import com.basalbody.app.model.response.UserResponse
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
import com.basalbody.app.utils.loadImageViaGlide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<HomeViewModel, FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    val TAG = "ProfileFragment"

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

        lifecycleScope.launch {
            viewModel.callDeleteUserApiStateFlow.collect {
                FlowInFragment<BaseResponse<DeleteUserResponse>>(
                    data = it,
                    fragment = this@ProfileFragment,
                    shouldShowErrorMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleDeleteUserResponse,
                )
            }
        }

        lifecycleScope.launch {
            viewModel.callGetUserProfileApiStateFlow.collect {
                FlowInFragment<BaseResponse<UserResponse>>(
                    data = it,
                    fragment = this@ProfileFragment,
                    shouldShowErrorMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleProfileResponse,
                )
            }
        }
    }

    override fun initSetup() {
        binding.apply {
            llToolBar.ivBack.gone()
            llToolBar.tvTitle.changeText(R.string.item_profile)
        }
        setupUI()
    }

    private fun setupUI() {
        val user = localDataRepository.getUserDetails()?.user
        binding.apply {
            tvUserName.changeText(user?.fullname ?: "")
            tvUserEmail.changeText(user?.email ?: "")
            ivProfile.loadImageViaGlide(value = user?.profileImage?.url ?: "")
        }
    }

    override fun listeners() {
        binding.apply {

            ivEditProfile.onSafeClick {
                viewModel.callGetUserProfileApi()
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
                        viewModel.callDeleteUserApi()
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

    private fun handleDeleteUserResponse(response: BaseResponse<DeleteUserResponse>?) {
        if (response.notNull() && response?.status == true) {
            openLogoutSuccessPopup()
        }
    }

    private fun handleProfileResponse(response: BaseResponse<UserResponse>?) {
        if (response.notNull() && response?.status == true) {
            activityLauncher.launch(Intent(requireContext(), EditProfileActivity::class.java)) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    setupUI()
                }
            }
        }
    }

    private fun openDeleteAccountSuccessPopup() {
        CommonSuccessBottomSheetDialog.newInstance(binding.root, requireActivity(), callBack = {
            navigateToLoginScreen()
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