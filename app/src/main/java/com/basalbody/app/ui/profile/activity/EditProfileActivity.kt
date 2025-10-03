package com.basalbody.app.ui.profile.activity

import android.net.Uri
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.base.FlowInActivity
import com.basalbody.app.databinding.ActivityEditProfileBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.getImageMultipart
import com.basalbody.app.extensions.notNull
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.model.response.ChangePasswordResponse
import com.basalbody.app.model.response.UserResponse
import com.basalbody.app.ui.common.showGenderSelectionPopup
import com.basalbody.app.ui.profile.viewmodel.ProfileViewModel
import com.basalbody.app.utils.Constants.EMAIL_PATTERN
import com.basalbody.app.utils.ImagePickerNew
import com.basalbody.app.utils.Logger
import com.basalbody.app.utils.ValidationStatus
import com.basalbody.app.utils.finishActivityWithLauncherResult
import com.basalbody.app.utils.getText
import com.basalbody.app.utils.loadImageViaGlide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import kotlin.text.uppercase

@AndroidEntryPoint
class EditProfileActivity : BaseActivity<ProfileViewModel, ActivityEditProfileBinding>() {

    private val TAG = "EditProfileActivity"
    override fun getViewBinding(): ActivityEditProfileBinding =
        ActivityEditProfileBinding.inflate(layoutInflater)

    private var imageUri : Uri? = null
    private var profileImageUrl : String = ""

    override fun addObservers() {
        lifecycleScope.launch {
            viewModel.callUploadProfileImageApiStateFlow.collect {
                FlowInActivity<BaseResponse<UserResponse>>(
                    data = it,
                    context = this@EditProfileActivity,
                    shouldShowErrorMessage = true,
                    shouldShowSuccessMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleUploadProfilePictureResponse,
                )
            }
        }

        lifecycleScope.launch {
            viewModel.callUpdateProfileApiStateFlow.collect {
                FlowInActivity<BaseResponse<UserResponse>>(
                    data = it,
                    context = this@EditProfileActivity,
                    shouldShowErrorMessage = true,
                    shouldShowSuccessMessage = true,
                    shouldShowLoader = true,
                    onSuccess = ::handleUpdateProfileResponse,
                )
            }
        }
    }

    override fun initSetup() {
        setupUI()
    }

    private fun setupUI() {
        Log.e(TAG, "setupUI()")
        val user = localDataRepository.getUserDetails()?.user
        binding.apply {
            llToolBar.tvTitle.changeText(getString(R.string.title_edit_profile))
            etFullName.editText.changeText(user?.fullname ?: "")
            etEmail.editText.changeText(user?.email ?: "")
            val stGender  = user?.gender ?: ""
            etGender.txtField.changeText(stGender.lowercase().replaceFirstChar { it.titlecase() })
            profileImageUrl = user?.profileImage?.url ?: ""
            ivProfile.loadImageViaGlide(value = user?.profileImage?.url ?: "")
        }
    }
    //getImageMultipart

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
                if (detailsAreNotUpdated()) {
                    finish()
                    return@onSafeClick
                }
                if (allDetailsValid()) {
                    val request = com.basalbody.app.model.request.RegisterRequest(
                        fullName = etFullName.getText()?.trim() ?: "",
                        email = etEmail.getText()?.trim() ?: "",
                        gender = etGender.txtField.getText()?.trim()?.toString()?.uppercase() ?: "",
                    )
                    viewModel.callUpdateProfileApi(request = request, userId = localDataRepository.getUserDetails()?.user?.id ?: 0)
                }
            }

            ivCamera onSafeClick {
                openImagePickerDialog()
            }
        }
    }

    private fun handleUploadProfilePictureResponse(response: BaseResponse<UserResponse>?) {
        Log.e(TAG, "handleUploadProfilePictureResponse()")
        if (response.notNull() && response?.status == true) {
            val userProfilePicture = localDataRepository.getUserDetails()?.user?.profileImage?.url ?: ""
            binding.ivProfile.loadImageViaGlide(value = userProfilePicture)
        }
    }

    private fun handleUpdateProfileResponse(response: BaseResponse<UserResponse>?) {
        Log.e(TAG, "handleUpdateProfileResponse()")
        if (response.notNull() && response?.status == true) {
            val user = localDataRepository.getUserDetails()?.user
            binding.apply {
                etFullName.editText.changeText(user?.fullname ?: "")
                etEmail.editText.changeText(user?.email ?: "")
                val stGender  = user?.gender ?: ""
                etGender.txtField.changeText(stGender.lowercase().replaceFirstChar { it.titlecase() })
                finishActivityWithLauncherResult()
            }
        }
    }

    private fun allDetailsValid(): Boolean {
        val fullName = binding.etFullName.getText()?.trim() ?: ""
        val email = binding.etEmail.getText()?.trim() ?: ""
        val gender = binding.etGender.txtField.getText()?.trim() ?: ""
        return when {
            fullName.isEmpty() -> {
                viewModel.setValidationValue(ValidationStatus.EMPTY_NAME)
                false
            }

            email.isEmpty() -> {
                viewModel.setValidationValue(ValidationStatus.EMPTY_EMAIL)
                false
            }

            !email.trim().matches(EMAIL_PATTERN.toRegex()) -> {
                viewModel.setValidationValue(ValidationStatus.INVALID_EMAIL)
                false
            }

            gender.isEmpty() -> {
                viewModel.setValidationValue(ValidationStatus.EMPTY_GENDER)
                false
            }

            else -> true
        }
    }

    private fun detailsAreNotUpdated(): Boolean {
        val user = localDataRepository.getUserDetails()?.user
        val fullName = binding.etFullName.getText()?.trim() ?: ""
        val email = binding.etEmail.getText()?.trim() ?: ""
        val gender = binding.etGender.txtField.getText()?.toString()?.trim()?.uppercase() ?: ""
        return profileImageUrl == user?.profileImage?.url && fullName == user.fullname && email == user.email && gender == user.gender
    }

    private fun openImagePickerDialog() {
        ImagePickerNew.newInstance(binding.main, this, activityLauncher, isPreventBackButton = false, title = "Update Profile Photo", description = "Choose how you’d like to add your photo: take a new picture or select one from your gallery.").apply {
            onResult = { path, clipData, uri ->
                Logger.e("ImagePickerNew", "path: $path \n clipData: $clipData \n uri: $uri")
                // Convert result to Uri

                imageUri = when {
                    uri != null -> uri
                    clipData != null && clipData.itemCount > 0 -> clipData.getItemAt(0).uri
                    path != null -> Uri.fromFile(File(path))
                    else -> null
                }
                Logger.e("ImagePickerNew", "Final imageUri: $imageUri")
                if (imageUri != null) {
                    binding.ivProfile.loadImageViaGlide(uri = imageUri)
                    val uriMultipart = getImageMultipart(imageUri!!, "profile")
                    uriMultipart?.let { this@EditProfileActivity.viewModel.callUploadProfileImageApi(it) }
                }
            }
        }.show(supportFragmentManager, "ImagePicker")
    }
}