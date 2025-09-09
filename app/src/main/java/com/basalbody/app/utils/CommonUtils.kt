@file:Suppress("unused")

package com.basalbody.app.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.basalbody.app.R
import kotlin.math.roundToInt


object CommonUtils {
    fun ifGPSOn(context: Context): Boolean {
        val mLocManager =
            context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == context.packageName) {
                        isInBackground = false
                    }
                }
            }
        }
        return isInBackground
    }

    fun Context.checkInternetConnected(): Boolean {
        var isConnected = false
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                isConnected = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
        return isConnected
    }

    fun showOkDialog(
        context: Context,
        title: String = context.getString(R.string.app_name),
        message: String,
        isFinish: Boolean = false,
        onButtonClick: (() -> Unit)? = null
    ) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("Ok") { dialog, _ ->
            if (isFinish) {
                val activity = context as Activity
                activity.finish()
            } else {
                onButtonClick?.invoke()
                dialog.dismiss()
            }

        }
        alertDialog.show()
    }

    fun showSettingsDialog(
        context: Context,
        activityLauncher: ActivityLauncher<Intent, ActivityResult>,
        permissionMessage: String,
        onButtonClick: ((Boolean) -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.message_need_permission))
        builder.setMessage(permissionMessage)
        builder.setNegativeButton(
            context.getString(R.string.button_cancel).uppercase()
        ) { dialog, i ->
            dialog.dismiss()
            onButtonClick?.invoke(false)
        }
        builder.setPositiveButton(
            context.getString(R.string.button_go_to_settings).uppercase()
        ) { dialog, which ->
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            activityLauncher.launch(intent) {
                Log.e("PrinceEWW>>>", "permission dialog result $it")
                onButtonClick?.invoke(true)
            }
        }
        builder.setCancelable(true)
        builder.show()
    }

    fun dpToPx(context: Context, dp: Int) = (dp * context.getPixelScaleFactor()).roundToInt()

    fun pxToDp(context: Context, px: Int) = (px / context.getPixelScaleFactor()).roundToInt()

    private fun Context.getPixelScaleFactor(): Float {
        val displayMetrics = resources.displayMetrics
        return displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT
    }

    fun fromHtml(source: String?): Spanned {
        return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
    }

    /**
     * Return image size in "Long"
     */
    private fun getImageSizeFromUri(context: Context, imageUri: Uri): Long {
        val contentResolver: ContentResolver = context.contentResolver
        val cursor = contentResolver.query(imageUri, null, null, null, null)
        cursor?.use {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            it.moveToFirst()
            return if (sizeIndex != -1) {
                val size = it.getLong(sizeIndex)
                size
            } else {
                0L
            }
        }
        return 0L
    }

    /**
     * Use - "SignUpStep2Activity"
     * Image size should not greater than [size] mb
     * If image size is greater than [size] MB then show snackBar and return false else return true
     */
    fun checkImageSizeAndShowValidation(context: Context, imageUri: Uri, size: Long): Boolean {
        val imageSize: Long = getImageSizeFromUri(context, imageUri)
        val maxSizeBytes: Long = size * 1024 * 1024 // 10MB in bytes

        return imageSize <= maxSizeBytes //If image size is greater than 20 MB then show snackBar and return false
    }

    fun getTypeface(context: Context, textStyle: Int): Typeface? {
        when (textStyle) {
            0 -> {
                return ResourcesCompat.getFont(context, R.font.geist_regular)
            }

            1 -> {
                return ResourcesCompat.getFont(context, R.font.geist_bold)
            }

            2 -> {
                return ResourcesCompat.getFont(context, R.font.geist_medium)
            }

            3 -> {
                return ResourcesCompat.getFont(context, R.font.geist_semi_bold)
            }

            4 -> {
                return ResourcesCompat.getFont(context, R.font.geist_extra_bold)
            }

            5 -> {
                return ResourcesCompat.getFont(context, R.font.geist_extra_light)
            }

            6 -> {
                return ResourcesCompat.getFont(context, R.font.geist_extra_light)
            }
        }
        return ResourcesCompat.getFont(context, R.font.geist_regular)
    }
}