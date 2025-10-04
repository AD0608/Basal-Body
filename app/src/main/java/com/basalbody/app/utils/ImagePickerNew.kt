package com.basalbody.app.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.FragmentActivity
import com.basalbody.app.R
import com.basalbody.app.base.BaseBottomSheetDialogFragment
import com.basalbody.app.databinding.BottomSheetImagePickerNewLayoutBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.visibleIfOrGone
import com.basalbody.app.ui.common.CommonViewModel
import com.basalbody.app.utils.Constants.GIF_MIME_TYPE
import com.basalbody.app.utils.Constants.WEBP_MIME_TYPE
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.KClass

@AndroidEntryPoint
class ImagePickerNew(isPreventBackButton: Boolean) :
    BaseBottomSheetDialogFragment<CommonViewModel, BottomSheetImagePickerNewLayoutBinding>(
        BottomSheetImagePickerNewLayoutBinding::inflate,
        isCancel = true,
        isDraggable = false, isPreventBackButton
    ) {

    private var activityLauncher: ActivityLauncher<Intent, ActivityResult>? = null
    private var mCurrentPhotoPath: String? = null
    private var title: String = ""
    private var description: String = ""
    private var photoURI: Uri? = null
    private lateinit var pickSingleMedia: ActivityResultLauncher<PickVisualMediaRequest>
    override val modelClass: KClass<CommonViewModel>
        get() = CommonViewModel::class

    override fun initControls() {
        binding.textViewGallery.visibleIfOrGone(true) // only gallery
        binding.textViewTitle.changeText(title)
        binding.tvDescription.changeText(description)
        if (activityLauncher == null) dismiss()

        pickSingleMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                dismiss()
                if (uri != null) {
                    val mimeType = mActivity.contentResolver?.getType(uri)
                    if (mimeType == GIF_MIME_TYPE || mimeType == WEBP_MIME_TYPE) {
                        showSnackBar(
                            getString(R.string.validation_gif_images_are_not_supported),
                            Constants.STATUS_ERROR,
                            mActivity
                        )
                        return@registerForActivityResult
                    }
                    val tenMB = 10 * 1024 * 1024 // 10 MB in bytes
                    val fileSize =
                        mActivity.contentResolver.openAssetFileDescriptor(uri, "r")?.use {
                            it.length
                        } ?: 0L

                    if (fileSize > tenMB) {
                        showSnackBar(
                            getString(R.string.validation_maximum_size_for_image_should_be_10_mb),
                            Constants.STATUS_ERROR,
                            mActivity
                        )
                        return@registerForActivityResult
                    }

                    onResult?.invoke(null, null, uri)
                }
            }
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success && photoURI != null && mCurrentPhotoPath != null) {
                    val file = File(mCurrentPhotoPath!!)

                    if (file.exists()) {
                        val correctedBitmap = fixImageOrientation(file)
                        correctedBitmap?.let {
                            val savedUri = saveBitmapToFile(it, file) // overwrite original file
                            onResult?.invoke(null, null, savedUri)
                        }
                    } else {
                        Logger.e("File not found at path: $mCurrentPhotoPath")
                    }

                    dismiss()
                } else {
                    Logger.e("Camera capture failed or cancelled")
                }
            }

    }

    override fun setOnClickListener() {
        binding.textViewCamera onSafeClick {
            activityLauncher?.let { it1 ->
                CameraAndExternalStoragePermission(
                    context = requireActivity(),
                    activityLauncher = it1
                ).checkCameraPermission {
                    dispatchTakePictureIntent()
                }
            }
        }

        binding.textViewGallery onSafeClick {
            activityLauncher?.let { launcher ->
                val permissionHelper = CameraAndExternalStoragePermission(
                    context = requireActivity(),
                    activityLauncher = launcher
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickSingleMedia.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    // Android 12 and below -> check READ_EXTERNAL_STORAGE, then open legacy gallery
                    permissionHelper.checkStoragePermission {
                        openGallery()
                    }
                }
            }
        }


    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        val mimeTypes = arrayOf("image/png", "image/jpg", "image/jpeg")
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        selectImageFromGalleryResult.launch(intent)
    }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult: ActivityResult? ->
            activityResult?.let {
                dismiss()
                onResult?.invoke(null, it.data?.clipData, it.data?.data)
            }
        }

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private fun dispatchTakePictureIntent() {
        try {
            val photoFile = createImageFile()
            photoURI = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )

            // ✅ launch the URI directly
            photoURI?.let { takePictureLauncher.launch(it) }

        } catch (ex: IOException) {
            ex.printStackTrace()
            Logger.e("Camera error: ${ex.message}")
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        ).apply {
            mCurrentPhotoPath = absolutePath
        }
    }

    var onResult: ((path: String?, clipData: android.content.ClipData?, uri: Uri?) -> Unit)? =
        null

    companion object {
        fun newInstance(
            rootView: ViewGroup,
            activity: FragmentActivity,
            activityLauncher: ActivityLauncher<Intent, ActivityResult>,
            isPreventBackButton: Boolean,
            title: String = "",
            description: String = ""
        ) = ImagePickerNew(isPreventBackButton).apply {
            this.rootView = rootView
            this.mActivity = activity
            this.title = title
            this.description = description
            this.activityLauncher = activityLauncher
        }
    }

    private fun fixImageOrientation(file: File): Bitmap? {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return null

        val exif = ExifInterface(file.absolutePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun saveBitmapToFile(bitmap: Bitmap, file: File): Uri {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return Uri.fromFile(file)
    }
}

fun getFileNameFromUri(context: Context, uri: Uri): String? {
    return when (uri.scheme) {
        ContentResolver.SCHEME_CONTENT -> {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
            null
        }

        ContentResolver.SCHEME_FILE -> File(uri.path!!).name
        else -> null
    }
}
