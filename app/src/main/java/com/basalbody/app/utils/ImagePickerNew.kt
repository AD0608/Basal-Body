package com.basalbody.app.utils

import android.Manifest
import android.content.ClipData
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.FragmentActivity
import com.basalbody.app.R
import com.basalbody.app.base.BaseBottomSheetDialogFragment
import com.basalbody.app.common.CommonViewModel
import com.basalbody.app.databinding.BottomSheetImagePickerNewLayoutBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.justTry
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.extensions.visibleIfOrGone
import com.basalbody.app.utils.Constants.DEFAULT_ONE
import com.basalbody.app.utils.Constants.GIF_MIME_TYPE
import com.basalbody.app.utils.Constants.WEBP_MIME_TYPE
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

@AndroidEntryPoint
class ImagePickerNew(isPreventBackButton: Boolean) :
    BaseBottomSheetDialogFragment<CommonViewModel, BottomSheetImagePickerNewLayoutBinding>(
        BottomSheetImagePickerNewLayoutBinding::inflate,
        isCancel = true,
        isDraggable = false, isPreventBackButton
    ) {

    private var activityLauncher: ActivityLauncher<Intent, ActivityResult>? = null
    var isMultipleImageSelection: Boolean = false
    var isPDFPickerShow: Boolean = false
    var isGalleryShow: Boolean = true
    private var propertyImageListSize: Int? = null
    private var maxImageCount: Int = 0
    private var maxItems = DEFAULT_ONE
    private var mCurrentPhotoPath: String? = null
    private var title: String = ""
    private var description: String = ""
    private var photoURI: Uri? = null
    private lateinit var pickMultipleMedia: ActivityResultLauncher<PickVisualMediaRequest>
    override val modelClass: KClass<CommonViewModel>
        get() = CommonViewModel::class

    override fun initControls() {
        binding.textViewGallery.visibleIfOrGone(isGalleryShow)
        binding.textViewFiles.visibleIfOrGone(isPDFPickerShow)
        binding.textViewTitle.changeText(title)
        binding.tvDescription.changeText(description)
        if (activityLauncher == null) dismiss()
        maxItems = maxImageCount - (propertyImageListSize
            ?: 0)
        if (maxItems == DEFAULT_ONE) {
            pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia())
            { uri ->
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

                    onResult?.invoke(null, null, arrayListOf(uri), null)
                }
            }
        } else {
            pickMultipleMedia =
                registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxItems)) { uris ->
                    dismiss()

                    val tenMB = 10 * 1024 * 1024 // 10 MB in bytes
                    val filteredUris = uris.filterNot { uri ->
                        val mimeType = mActivity.contentResolver.getType(uri)

                        // Check for disallowed formats (GIF or WEBP)
                        if (mimeType == GIF_MIME_TYPE || mimeType == WEBP_MIME_TYPE) {
                            return@filterNot true
                        }

                        // Check for file size > 10MB
                        val fileSize =
                            mActivity.contentResolver.openAssetFileDescriptor(uri, "r")?.use {
                                it.length
                            } ?: 0L

                        fileSize > tenMB
                    }

                    // Show appropriate message if anything was removed
                    if (filteredUris.size != uris.size) {
                        showSnackBar(
                            getString(R.string.validation_some_images_are_not_supported_due_to_format_or_size),
                            Constants.STATUS_ERROR,
                            mActivity
                        )
                    }

                    onResult?.invoke(null, null, ArrayList(filteredUris), null)
                }
        }

        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success && photoURI != null && mCurrentPhotoPath != null) {
                    val file = File(mCurrentPhotoPath.toString())

                    val correctedBitmap = fixImageOrientation(file)
                    correctedBitmap?.let {
                        val savedUri =
                            saveBitmapToFile(it, file) // optional overwrite or create new file
                        onResult?.invoke(savedUri.toString(), null, null, null)
                    }

                    dismiss()
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
                    dispatchTakePictureIntent() //If camera permission is granted then only open camera for take picture
                }
            }
        }

        binding.textViewGallery onSafeClick {
            activityLauncher?.let { it1 ->
                CameraAndExternalStoragePermission(
                    context = requireActivity(),
                    activityLauncher = it1
                ).checkStoragePermission {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        openGallary(isMultipleImageSelection = isMultipleImageSelection) //If camera permission is granted then only open gallery
                    } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    } else {
                        if (ContextCompat.checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.READ_MEDIA_IMAGES
                            ) == PermissionChecker.PERMISSION_GRANTED
                        ) {
                            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        } else {
                            justTry {
                                mActivity.contentResolver?.let { it1 ->
                                    LimitedAccessImagesPickerBottomSheetDialog.newInstance(
                                        rootView = rootView,
                                        activity = mActivity,
                                        isCancel = true,
                                        isPreventBackButton = false,
                                        maxPickItem = if (isMultipleImageSelection) maxItems else DEFAULT_ONE,
                                        contentResolver = it1
                                    ).apply {
                                        onImageSelection = {
                                            this@ImagePickerNew.dismiss()
                                            onResult?.invoke(null, null, it, null)
                                        }
                                    }.show(
                                        mActivity.supportFragmentManager,
                                        LimitedAccessImagesPickerBottomSheetDialog::class.simpleName
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.textViewFiles onSafeClick {
            activityLauncher?.let { it1 ->
                CameraAndExternalStoragePermission(
                    context = requireContext(),
                    activityLauncher = it1
                ).checkStoragePermission(isMediaSelection = false) {
                    openPdfPicker(activityLauncher!!) { path, _ ->
                    }//No need callback here we pass data in imagePickerResult.onResult(_, _)
                }
            }
        }
    }

    private fun openGallary(isMultipleImageSelection: Boolean) {
        //-------Below code is not restrict gif for choose from gallery-------//
//        selectImageFromGalleryResult.launch("image/*")

        //-------Add restriction to add gif-------//
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        val mimeTypes = arrayOf("image/png", "image/jpg", "image/jpeg")
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultipleImageSelection)
        selectImageFromGalleryResult.launch(intent)
    }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult: ActivityResult? ->
            activityResult?.let {
                /*it.data?.data?.path?.let { it1 ->
                    imagePickerResult.onResult(
                        it.data!!.data.toString(),
                        it.data?.clipData
                    )
                }*/

//                it.data?.withNotNull {
                dismiss()
                onResult?.invoke(it.data?.data.toString(), it.data?.clipData, null, null)
//                }
            }
        }

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent

        // Create the File where the photo should go
        var photoFile: File? = null
        try {
            photoFile = createImageFile()
            photoURI = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".provider",
                photoFile
            )
        } catch (ex: IOException) {
            Logger.e(ex.message!!)
            Logger.e(requireContext().packageName)
        }

        // Continue only if the File was successfully created
        if (photoFile != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            photoURI?.let { takePictureLauncher.launch(it) }
        }
    }

    private fun openPdfPicker(
        launcher: ActivityLauncher<Intent, ActivityResult>,
        callBack: (String?, ClipData?) -> Unit
    ) {
        pickPdf(
            launcher
        ) { path, _ ->
            onResult?.invoke(
                null,
                null,
                null,
                path
            )
//            callBack.invoke(path, clipData)
            dismiss()
        }
    }

    private fun pickPdf(
        launcher: ActivityLauncher<Intent, ActivityResult>,
        callBack: (String?, ClipData?) -> Unit
    ) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
        }

        launcher.launch(intent) { activityResult: ActivityResult? ->
            activityResult?.let { result ->
                val uri = result.data?.data

                uri?.let uriLet@{
                    val fileSize = try {
                        mActivity.contentResolver.openAssetFileDescriptor(uri, "r")?.use { fd ->
                            fd.length
                        } ?: 0L
                    } catch (e: Exception) {
                        0L
                    }

                    if (fileSize > 10 * 1024 * 1024) {
                        showSnackBar(
                            mActivity.getString(R.string.validation_maximum_size_for_pdf_should_be_10_mb),
                            Constants.STATUS_ERROR,
                            mActivity
                        )
                        return@uriLet
                    }

                    callBack.invoke(uri.toString(), result.data?.clipData)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    var onResult: ((path: String?, clipData: ClipData?, uriList: ArrayList<Uri>?, pdfPath: String?) -> Unit)? =
        null

    companion object {

        fun newInstance(
            rootView: ViewGroup,
            activity: FragmentActivity,
            activityLauncher: ActivityLauncher<Intent, ActivityResult>,
            isMultipleImageSelection: Boolean,
            isPDFPickerShow: Boolean,
            isGalleryShow: Boolean = true,
            imageListSize: Int? = null,
            maxImageCount: Int = 0,
            isPreventBackButton: Boolean,
            title: String = "",
            description: String = ""
        ) = ImagePickerNew(isPreventBackButton).apply {
            this.rootView = rootView
            this.mActivity = activity
            this.title = title
            this.description = description
            this.activityLauncher = activityLauncher
            this.isGalleryShow = isGalleryShow
            this.isMultipleImageSelection = isMultipleImageSelection
            this.isPDFPickerShow = isPDFPickerShow
            if (imageListSize != null) {
                this.propertyImageListSize = imageListSize
            }
            this.maxImageCount = maxImageCount
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
        ContentResolver.SCHEME_CONTENT -> { // Query content resolver for display name (Only works for content:// URIs)
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

        ContentResolver.SCHEME_FILE -> { // Extract file name from file path for file:// URIs
            File(uri.path!!).name
        }

        else -> null
    }
}
