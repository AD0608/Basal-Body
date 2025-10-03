package com.basalbody.app.ui.profile.activity

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.base.FlowInActivity
import com.basalbody.app.databinding.ActivityChangePasswordBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.request.ChangePasswordRequest
import com.basalbody.app.model.response.ChangePasswordResponse
import com.basalbody.app.ui.profile.viewmodel.ProfileViewModel
import com.basalbody.app.utils.ValidationStatus
import com.basalbody.app.utils.getText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordActivity : BaseActivity<ProfileViewModel, ActivityChangePasswordBinding>() {

    private val TAG = "ChangePasswordActivity"
    override fun getViewBinding(): ActivityChangePasswordBinding =
        ActivityChangePasswordBinding.inflate(layoutInflater)

    override fun addObservers() {
        lifecycleScope.launch {
            viewModel.callChangePasswordApiStateFlow.collect {
                FlowInActivity<BaseResponse<ChangePasswordResponse>>(
                    data = it,
                    context = this@ChangePasswordActivity,
                    shouldShowErrorMessage = true,
                    shouldShowSuccessMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleChangePasswordResponse,
                )
            }
        }
    }

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        Log.e(TAG, "setupUI()")
        binding.apply {
            llToolBar.tvTitle.changeText(R.string.lbl_change_password)
        }
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
        binding.apply {
            llToolBar.ivBack.onSafeClick {
                finish()
            }

            btnUpdate.onSafeClick {
                if (allDetailsValid()) {
                    val request = ChangePasswordRequest().apply {
                        oldPassword = etCurrentPass.getText()?.trim() ?: ""
                        newPassword = etNewPass.getText()?.trim() ?: ""
                        confirmPassword = etConfirmPass.getText()?.trim() ?: ""
                    }
                    viewModel.callChangePasswordApi(request = request, userId = localDataRepository.getUserDetails()?.user?.id ?: 0)
                }
            }
        }
    }

    private fun handleChangePasswordResponse(response: BaseResponse<ChangePasswordResponse>?) {
        if (response.notNull() && response?.status == true) {
            navigateToLoginScreen()
        }
    }

    private fun allDetailsValid(): Boolean {
        val currentPassword = binding.etCurrentPass.getText()?.trim() ?: ""
        val newPassword = binding.etNewPass.getText()?.trim() ?: ""
        val confirmPassword = binding.etConfirmPass.getText()?.trim() ?: ""

        return when {
            !currentPassword(currentPassword) -> false
            !newPassword(newPassword) -> false
            !confirmPassword(confirmPassword) -> false
            currentPassword == newPassword -> {
                viewModel.setValidationValue(ValidationStatus.CURRENT_PASSWORD_NEW_PASSWORD_NOT_SAME)
                false
            }
            newPassword != confirmPassword -> {
                viewModel.setValidationValue(ValidationStatus.NEW_PASSWORD_CONFIRM_PASS_NOT_SAME)
                false
            }
            else -> true
        }
    }
}