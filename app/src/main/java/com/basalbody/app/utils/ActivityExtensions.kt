package com.basalbody.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.basalbody.app.extensions.hideKeyboard
import com.basalbody.app.extensions.inTransaction
import com.basalbody.app.extensions.notNullAndNotEmpty
import com.basalbody.app.extensions.showKeyboard
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Date


fun <F> AppCompatActivity.replaceFragment(
    container: Int,
    fragment: F,
    toBackStack: Boolean
) where F : Fragment {
    supportFragmentManager.inTransaction {
        if (toBackStack) {
            addToBackStack(fragment::class.java.simpleName)
        }
        replace(container, fragment)
    }
}

inline fun <reified A : Class<*>> AppCompatActivity.finishAndStartNewActivity(newActivity: A) {
    val i = Intent(this, newActivity)
    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(i)
    onBackPressedDispatcher.onBackPressed()
}


/**
 * Start an activity with a launcher, allowing for a result callback.
 *
 * @param context The calling activity.
 * @param className The target activity's class.
 * @param bundle Optional data bundle to pass to the new activity.
 * @param onResult Callback to handle the activity result.
 */
inline fun <T> ActivityLauncher<Intent, ActivityResult>.startActivityWithLauncher(
    context: Activity,
    className: Class<T>,
    bundle: Bundle? = null,
    crossinline onResult: (ActivityResult) -> Unit
) {
    context.hideKeyboard()
    val intent = Intent(context, className).apply {
        bundle?.let {
            putExtras(it)
        }
    }

    launch(intent) {
        onResult(ActivityResult(it.resultCode, it.data))
    }
}


/**
 * Finish the activity with a launcher result, optionally passing a bundle.
 *
 * @param bundle Optional data bundle to pass as the result.
 */
fun Activity.finishActivityWithLauncherResult(
    /*resultCode: Int = RESULT_OK,*/ // If there is any scenario need to change result code
    bundle: Bundle? = null,
) {
    hideKeyboard()
    Intent().apply {
        bundle?.let {
            putExtras(it)
        }
        setResult(AppCompatActivity.RESULT_OK, this)
        finish()
    }
}

fun AppCompatActivity.finishActivityWithResult(bundle: Bundle) {
    val resultIntent = Intent()
    resultIntent.putExtras(bundle)
    setResult(Activity.RESULT_OK, resultIntent)
    finish()
}

fun AppCompatActivity.finishActivityWithMessage(message: String) {
    val resultIntent = Intent()
    val b = bundleOf("message" to message)
    resultIntent.putExtras(b)
    setResult(Activity.RESULT_OK, resultIntent)
    finish()
}

fun Context.openPermissionSettings() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = Uri.fromParts("package", packageName, null)
    startActivity(intent)
}

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

/**
 * Share text on WhatsApp.
 */
fun AppCompatActivity.shareWithWhatsApp(message: String, context: Context) {
    try {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        sendIntent.type = "text/plain"
        sendIntent.setPackage("com.whatsapp")
        startActivity(sendIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        showSnackBar("WhatsApp not installed!", 2, context)
        // Toast.makeText(this, "WhatsApp not installed!", Toast.LENGTH_LONG).show()
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

/**
 * Open Play Store URL.
 */
fun AppCompatActivity.openPlayStore(packageName: String) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=$packageName".toUri()
        )
    )
}

/*@SuppressLint("PackageManagerGetSignatures")
fun AppCompatActivity.generateFacebookHashKey(): String {
    var hashKey = ""
    try {
        val info =
            packageManager.getPackageInfo(application.packageName, PackageManager.GET_SIGNATURES)
        for (signature in info.signatures!!) {
            val md = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT)
            ("HASH KEY IS >> $hashKey")
        }
    } catch (e: PackageManager.NameNotFoundException) {
        hashKey = "ERROR"
        e.printStackTrace()
    } catch (e: NoSuchAlgorithmException) {
        hashKey = "ERROR"
        e.printStackTrace()
    }
    return hashKey
}*/

/**
 * Open Default Email app.
 */
@SuppressLint("IntentReset")
fun AppCompatActivity.openEmail(context: Context) {
    try {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        /*        sendIntent.addCategory(Intent.CATEGORY_APP_EMAIL)
                sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK*/
        sendIntent.data = Uri.parse("mailto:")
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@appliport.com"))
        startActivity(sendIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        showSnackBar("There is no email client installed.", 2, context)
    }
}

fun AppCompatActivity.showLocationInMap(
    context: Context,
    latitude: Double,
    longitude: Double,
    title: String
) {
    try {
        val gmmIntentUri =
            if (title.notNullAndNotEmpty()) Uri.parse("geo:0,0?q=$latitude,$longitude($title)") else Uri.parse(
                "geo:0,0?q=$latitude,$longitude"
            )

        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        showSnackBar("There is no map related app", 2, context)
    }
}

fun AppCompatActivity.showPathInMap(context: Context, latitude: Double, longitude: Double) {
    try {
        val gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        startActivity(mapIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        showSnackBar("There is no map related app", 2, context)
    }
}

// Hide keyboard in Activity
fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

// Show keyboard in Activity
fun Activity.showKeyboard() {
    showKeyboard(currentFocus ?: View(this))
}

fun Activity.setKeyboardVisibilityListener(onKeyboardToggle: (Boolean) -> Unit) {
    val rootView = findViewById<View>(android.R.id.content)
    rootView.viewTreeObserver.addOnGlobalLayoutListener {
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        val screenHeight = rootView.rootView.height
        val keypadHeight = screenHeight - rect.bottom
        onKeyboardToggle(keypadHeight > screenHeight * 0.15)
    }
}