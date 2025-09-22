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
import com.basalbody.app.utils.ImagePickerNew
import com.basalbody.app.utils.Logger
import com.basalbody.app.utils.loadImageViaGlide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class EditProfileActivity : BaseActivity<ProfileViewModel, ActivityEditProfileBinding>() {

    private val TAG = "EditProfileActivity"
    override fun getViewBinding(): ActivityEditProfileBinding =
        ActivityEditProfileBinding.inflate(layoutInflater)

    private var imageUri : Uri? = null

    override fun addObservers() {
        lifecycleScope.launch {
            viewModel.callChangePasswordApiStateFlow.collect {
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
                finish()
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