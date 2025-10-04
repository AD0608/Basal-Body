package com.basalbody.app.ui.profile.activity

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.base.FlowInActivity
import com.basalbody.app.databinding.ActivityTroubleShootBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.request.AddInquiryRequest
import com.basalbody.app.model.response.AddInquiryResponse
import com.basalbody.app.ui.profile.viewmodel.ProfileViewModel
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.Constants.EMAIL_PATTERN
import com.basalbody.app.utils.ValidationStatus
import com.basalbody.app.utils.disableField
import com.basalbody.app.utils.getText
import com.basalbody.app.utils.setText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TroubleShootActivity : BaseActivity<ProfileViewModel, ActivityTroubleShootBinding>() {

    private val TAG = "TroubleShootActivity"
    override fun getViewBinding(): ActivityTroubleShootBinding =
        ActivityTroubleShootBinding.inflate(layoutInflater)

    override fun addObservers() {
        lifecycleScope.launch {
            viewModel.callAddInquiryApiStateFlow.collect {
                FlowInActivity<BaseResponse<AddInquiryResponse>>(
                    data = it,
                    context = this@TroubleShootActivity,
                    shouldShowErrorMessage = true,
                    shouldShowSuccessMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleAddInquiryResponse,
                )
            }
        }
    }

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        val userDetails = localDataRepository.getUserDetails()?.user
        binding.apply {
            llToolBar.tvTitle.changeText(R.string.title_bluetooth_device_troubleshooting)
            etFullName.setText(userDetails?.fullname ?: "")
            etFullName.disableField()
            etEmail.setText(userDetails?.email ?: "")
            etEmail.disableField()
        }
    }

    override fun listeners() {
        Log.e(TAG, "listeners()")
        binding.apply {
            llToolBar.ivBack.onSafeClick {
                finish()
            }
            btnSubmit.onSafeClick {
                if (allDetailsValid()) {
                    val request = AddInquiryRequest(
                        /*fullname = etFullName.getText().toString().trim(),*/
                        /*email = etEmail.getText().toString().trim(),*/
                        type = Constants.ISSUE_TYPE_BLUETOOTH,
                        message = etIssueMessage.text.toString().trim(),
                    )
                    viewModel.callAddInquiryApi(request)
                }
            }
        }
    }

    private fun handleAddInquiryResponse(response: BaseResponse<AddInquiryResponse>?) {
        Log.e(TAG, "handleAddInquiryResponse()")
        Log.e(TAG, "handleAddInquiryResponse() response: $response")
        finish()
    }

    private fun allDetailsValid(): Boolean {
        Log.e(TAG, "allDetailsValid()")
        binding.apply {
            return when {
                etFullName.getText().toString().trim().isEmpty() -> {
                    viewModel.setValidationValue(ValidationStatus.EMPTY_NAME)
                    false
                }

                etEmail.getText()?.isEmpty() == true -> {
                    viewModel.setValidationValue(ValidationStatus.EMPTY_EMAIL)
                    false
                }

                etEmail.getText()?.trim()?.matches(EMAIL_PATTERN.toRegex()) == false -> {
                    viewModel.setValidationValue(ValidationStatus.INVALID_EMAIL)
                    false
                }

                etIssueMessage.text.toString().isEmpty() -> {
                    viewModel.setValidationValue(ValidationStatus.EMPTY_ISSUE_MESSAGE) // Change to appropriate status
                    false
                }

                else -> true
            }
        }
    }
}