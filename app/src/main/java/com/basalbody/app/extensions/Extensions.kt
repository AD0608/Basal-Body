package com.basalbody.app.extensions

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.basalbody.app.R
import com.basalbody.app.model.BaseResponse
import com.basalbody.app.utils.ImageUtilNew
import com.basalbody.app.utils.ImageUtilNew.saveBitmapToFile
import com.basalbody.app.utils.LoadingDialog
import com.basalbody.app.utils.Logger
import com.basalbody.app.utils.language.LocaleHelper
import com.basalbody.app.utils.showSnackBar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Date
import java.util.UUID

/**
 *   Convert string to model class
 *
 */

fun getDeviceType(): String {
    return "android"
}

inline fun <reified T> String.toObject(): T? {
    justTry {
        return Gson().fromJson(this, T::class.java)
    }
    return null
}

inline fun <reified T : Serializable> BaseResponse<*>.safeCast(): BaseResponse<T>? {
    return if (this.data is T) {
        @Suppress("UNCHECKED_CAST")
        this as BaseResponse<T>
    } else {
        null
    }
}

/**
 *  Convert Model to String
 */
fun Any.toJson(): String? {
    justTry {
        return Gson().toJson(this)
    }
    return null
}

/*fun List<Any>?.notNullAndNotEmpty(): Boolean {
    return this.notNull() && this!!.isNotEmpty()
}*/

/**
 *  Check Collection is not null or not empty
 */
fun <T> Collection<T>?.notNullAndNotEmpty(): Boolean {
    return this.notNull() && this!!.isNotEmpty()
}

/**
 *  Check [String] is not null or not empty or not "null"
 */
fun String?.notNullAndNotEmpty(): Boolean {
    return this.notNull() && this!!.isNotEmpty() && this.lowercase() != "null"
}

/**
 *  Check [String] is not null then execute.
 */
inline fun <T : Any, R> T?.withNotNull(block: (T) -> R): R? {
    return this?.let(block)
}

/**
 *  Version Exception handling in Bundle parcelable.
 */
inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key)
            as? T
}

/**
 * Implementation of lazy that is not thread safe. Useful when you know what thread you will be
 * executing on and are not worried about synchronization.
 */
fun <T> lazyFast(operation: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    operation()
}

/** Convenience for callbacks/listeners whose return value indicates an event was consumed. */
inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

/**
 * Allows calls like
 *
 * `viewGroup.inflate(R.layout.foo)`
 */
fun ViewGroup.inflate(@LayoutRes layout: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layout, this, attachToRoot)
}

/**
 * Allows calls like
 *
 * `supportFragmentManager.inTransaction { add(...) }`
 */
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commitAllowingStateLoss()
}


// endregion
// region Parcelables, Bundles

/** Write an enum value to a Parcel */
fun <T : Enum<T>> Parcel.writeEnum(value: T) = writeString(value.name)

/** Read an enum value from a Parcel */
inline fun <reified T : Enum<T>> Parcel.readEnum(): T = enumValueOf(readString()!!)

/** Write an enum value to a Bundle */
fun <T : Enum<T>> Bundle.putEnum(key: String, value: T) = putString(key, value.name)

/** Read an enum value from a Bundle */
inline fun <reified T : Enum<T>> Bundle.getEnum(key: String): T = enumValueOf(getString(key)!!)

// endregion
// region LiveData

/** Uses `Transformations.map` on a LiveData */
fun <X, Y> LiveData<X>.map(body: (X) -> Y): LiveData<Y> {
    return this.map(body)
}

/** Uses `Transformations.switchMap` on a LiveData */
fun <X, Y> LiveData<X>.switchMap(body: (X) -> LiveData<Y>): LiveData<Y> {
    return this.switchMap(body)
}

fun <T> MutableLiveData<T>.setValueIfNew(newValue: T) {
    if (this.value != newValue) value = newValue
}

fun <T> MutableLiveData<T>.postValueIfNew(newValue: T) {
    if (this.value != newValue) postValue(newValue)
}
// endregion

/**
 * Helper to force a when statement to assert all options are matched in a when statement.
 *
 * By default, Kotlin doesn't care if all branches are handled in a when statement. However, if you
 * use the when statement as an expression (with a value) it will force all cases to be handled.
 *
 * This helper is to make a lightweight way to say you meant to match all of them.
 *
 * Usage:
 *
 * ```
 * when(sealedObject) {
 *     is OneType -> //
 *     is AnotherType -> //
 * }.checkAllMatched
 */
val <T> T.checkAllMatched: T
    get() = this

// region UI utils

/**
 * Retrieves a color from the theme by attributes. If the attribute is not defined, a fall back
 * color will be returned.
 */
@ColorInt
fun Context.getThemeColor(
    @AttrRes attrResId: Int,
    @ColorRes fallbackColorResId: Int
): Int {
    val tv = TypedValue()
    return ((theme.resolveAttribute(attrResId, tv, true)) then { tv.data })
        ?: ContextCompat.getColor(
            this,
            fallbackColorResId
        )

}

// endregion

/**
 * Helper to throw exceptions only in Debug builds, logging a warning otherwise.
 */

fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

/**
 * Get value in dp
 */
val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

/**
 * Get value in px
 */
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

infix fun <T : Any> Boolean.then(param: () -> T): T? = if (this) param() else null

fun Double?.decimalCeil(): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toDouble()
}

fun Double.decimalFloor(): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this).toDouble()
}

/**
 * Set load in child fragment
 */
fun Fragment.loadChildFragment(placeholder: Int, fragment: Fragment) {
    childFragmentManager.beginTransaction().replace(placeholder, fragment).commit()
}

/**
 * Create File name of @param [imagePath]
 */
fun createFileName(imagePath: String): String {
    return Date().time.toString() + "." + MimeTypeMap.getFileExtensionFromUrl(imagePath)
}

// Function for hiding keyboard
fun Context.hideKeyboard(view: View?) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
}

// Function for showing keyboard
fun Context.showKeyboard(view: View?) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, 0)
}

// Function for is hide keyboard or not
fun Context.isKeyboardHide(): Boolean {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return imm.isAcceptingText
}

/**Show Toast message*/
fun Any.showToast(context: Context, message: String) =
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

/**Show Toast message*/
fun Context.showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

/**Show Toast message length long*/
fun Any.showLongToast(context: Context, message: String) =
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()

/**Null value check*/
fun UUID?.nullSafe(): UUID {
    return this ?: UUID.randomUUID()
}

/**Null value check*/
fun String?.nullSafe(defaultValue: String = ""): String {
    return this ?: defaultValue
}

/**Null value check*/
fun Int?.nullSafe(defaultValue: Int = 0): Int {
    return this ?: defaultValue
}

/**Null value check*/
fun Float?.nullSafe(defaultValue: Float = 0.0f): Float {
    return this ?: defaultValue
}

/**Null value check*/
fun Long?.nullSafe(defaultValue: Long = 0L): Long {
    return this ?: defaultValue
}

/**Null value check*/
fun Double?.nullSafe(defaultValue: Double = 0.0): Double {
    return this ?: defaultValue
}

/**Null value check*/
fun BigDecimal?.nullSafe(defaultValue: BigDecimal = BigDecimal(0)): BigDecimal {
    return this ?: defaultValue
}

/**Null value check*/
fun Boolean?.nullSafe(defaultValue: Boolean = false): Boolean {
    return this ?: defaultValue
}

/**Get Image Multipart, Using @param [uri], @param [name] */
fun Context.getImageMultipart(uri: Uri, name: String): MultipartBody.Part? {
    val file = ImageUtilNew.from(this, uri)
    val selectedFile = file?.let { saveBitmapToFile(it) }
    val mime = contentResolver.getType(uri) ?: "image/jpeg"

    val requestFile: RequestBody? =
        selectedFile?.asRequestBody(mime.toMediaTypeOrNull())
    return requestFile?.let { MultipartBody.Part.createFormData(name, selectedFile.name, it) }
}

//to create multipart from file
fun File.prepareFilePart(fileName: String, name: String): MultipartBody.Part {
    val requestBody = this
        .asRequestBody(
            "application/pdf".toMediaTypeOrNull() // Set MIME type to "application/pdf"
        )
    return MultipartBody.Part.createFormData(name, fileName, requestBody)
}

fun Context.getMimeType(uri: Uri): String? {
    val mimeType = getFileType(uri, this)
    Logger.e("AjayEww-->", "mimeType : $mimeType")
    return mimeType
}

fun getFileType(uri: Uri, context: Context): String? {
    val file = File(uri.path.toString())
    return if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
        context.contentResolver.getType(uri)
    } else {
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension.lowercase())
    }
}

fun Context.getMediaMultipart(uri: Uri, name: String): MultipartBody.Part? {
    val mimeType = getMimeType(uri) ?: return null
    return if (mimeType == "application/pdf") {
        getFileMultipart(uri, name)
    } else {
        getImageMultipart(uri, name)
    }
}

fun Context.getFileMultipart(uri: Uri, name: String): MultipartBody.Part? {
    val mimeType = contentResolver.getType(uri) ?: return null
    // Only proceed if it's a PDF
    if (mimeType != "application/pdf") return null

    val fileName = getFileNameFromUri(this, uri) ?: "File.pdf"
    val pdfFile = getPdfFile(this, fileName, uri.toString())
    return pdfFile?.prepareFilePart(fileName, name)
}

private fun getPdfFile(context: Context, filename: String?, uri: String): File? {
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri.toUri())
        if (inputStream != null) {
            val pdfFile =
                File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                    filename ?: "File.pdf"
                )
            val outputStream = FileOutputStream(pdfFile)
            val buffer = ByteArray(4 * 1024) // Adjust the buffer size as needed

            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()

            return pdfFile
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return null
}

fun getFileNameFromUri(context: Context, uri: Uri): String? {
    return when (uri.scheme) {
        ContentResolver.SCHEME_CONTENT -> {
            // Query content resolver for display name (Only works for content:// URIs)
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

        ContentResolver.SCHEME_FILE -> {
            // Extract file name from file path for file:// URIs
            File(uri.path!!).name
        }

        else -> null
    }
}


fun Context.getFileFromUri(uri: Uri): File? {
    val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: return null
    val inputStream = contentResolver.openInputStream(uri) ?: return null

    val tempFile = File(cacheDir, fileName)
    tempFile.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return tempFile
}

/**Open Default App setting*/
fun Context.openPermissionSettings() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = Uri.fromParts("package", packageName, null)
    startActivity(intent)
}

/**Open Default Call app*/
fun Context.call(phone: String, context: Context) {
    val callIntent = Intent(Intent.ACTION_DIAL)
    callIntent.data = Uri.parse("tel:$phone")
    if (callIntent.resolveActivity(packageManager) != null) {
        startActivity(callIntent)
    } else {
        //Toast.makeText(this, "Call functionality not available!", Toast.LENGTH_SHORT).show()
        showSnackBar("Call functionality not available!", 2, context)
    }
}

/**Check is Internet Connected or not*/
fun Context.isNetworkConnected(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
    return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}

/**Open Default Message app*/
fun Context.message(phone: String, text: String, context: Context) {
    val smsIntent = Intent(Intent.ACTION_SENDTO)
    smsIntent.data = Uri.parse("smsto:$phone")
    smsIntent.putExtra("sms_body", text)
    if (smsIntent.resolveActivity(packageManager) != null) {
        startActivity(smsIntent)
    } else {
        //    Toast.makeText(this, "Message functionality not available!", Toast.LENGTH_SHORT).show()
        showSnackBar("Message functionality not available!", 2, context)
    }
}


fun Context.background(drawable: Int): Drawable? {
    return ContextCompat.getDrawable(this, drawable)
}

/*fun Context.showKeyboard(view: View) {
    this as Activity
    if (view.requestFocus()) {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}*/

/*fun Context.hideKeyBord() {
    this as Activity
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}*/
/**Save in File Storage as Jpg format*/
fun Context.saveInStorage(bitmap: Bitmap): String {
    val wrapper = ContextWrapper(this)
    var file = wrapper.getDir("images", Context.MODE_PRIVATE)
    file = File(file, "images.jpg")

    try {
        val stream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return file.absolutePath
}

/**Save in Cache as Jpg format*/
fun Context.storeInCache(bitmapImage: Bitmap?): String {
    val bytes = ByteArrayOutputStream()
    val fileName = Date().time.toString() + ".jpg"
    bitmapImage?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val file = File(this.cacheDir.path + File.separator + fileName)
    file.createNewFile()
    val fos = FileOutputStream(file)
    fos.write(bytes.toByteArray())
    fos.close()
    return file.absolutePath
}

// Share app function
fun Context.shareApp() {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
        var shareMessage = "\nLet me recommend you this application\n\n"
        shareMessage =
            "${shareMessage}https://play.google.com/store/apps/details?id=${"BuildConfig.APPLICATION_ID"}\n\n".trimIndent()
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "choose one"))

    } catch (e: Exception) {
        e.printStackTrace()
    }
}


/**
 * Share text.
 */

fun Context.defaultShare(title: String, message: String) {
    try {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, title))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


@SuppressLint("Range")
fun Context.getAbsolutePath(uri: Uri): String? {
    val docId = DocumentsContract.getDocumentId(uri)
    val id = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
    val imageColumns = arrayOf(MediaStore.Images.Media.DATA)
    val imageOrderBy: String? = null
    val imageCursor =
        contentResolver.query(
            uri,
            imageColumns,
            MediaStore.Images.Media._ID + "=" + id,
            null,
            imageOrderBy
        )

    return if (imageCursor!!.moveToFirst()) {
        imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA))

    } else {
        null
    }
}

/**
 * Copy text to clipboard.
 */
fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Text", text)
    clipboard.setPrimaryClip(clip)
}


// Open link in browser
fun Context.openUrlInBrowser(url: String?) {
    val openURL = Intent(Intent.ACTION_VIEW)
    openURL.data = Uri.parse(url?.fixHttp())
    startActivity(openURL)
}

// Fix url protocol
fun String.fixHttp(): String {
    return if (this.startsWith("http") || this.startsWith("https")) this else "https://$this"
}

// Service is Running or not
@Suppress("DEPRECATION") // Deprecated for third party Services.
fun <T> Context.isServiceRunning(service: Class<T>) =
    (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }

fun ImageView.supportRTL() {
    if (LocaleHelper.isRtl(this.context)) {
        this.scaleX = -1F
    } else {
        this.scaleX = 1F
    }
}

fun Context.showLoader() {
    LoadingDialog.showLoadDialog(this)
}

fun Context.hideLoader() {
    LoadingDialog.hideLoadDialog()
}

fun Context.showInternetDialog(
    isCancelAble: Boolean = false,
    positiveClick: (() -> Unit)? = null
) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(this.getString(R.string.app_name))
    builder.setMessage(this.getString(R.string.network_connection_error))
    builder.setPositiveButton(this.getString(R.string.button_retry)) { _, _ ->
        positiveClick?.invoke()
    }
    builder.setCancelable(isCancelAble)
    val alertDialog = builder.create()
    alertDialog.show()
}

fun View.shake() {
    val shake = ObjectAnimator.ofFloat(
        this,
        "translationX",
        -30f, 30f, -30f, 30f, -20f, 20f, -10f, 10f, -5f, 5f, 0f
    )
    shake.duration = 600
    shake.interpolator = LinearInterpolator()
    shake.start()
}

fun String.getFileNameFromUrl(): String {
    return this.substringAfterLast('/')
}