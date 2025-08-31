@file:Suppress("unused")

package com.basalbody.app.utils

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.drawable.Drawable
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
import androidx.core.app.ActivityCompat
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.basalbody.app.BuildConfig
import com.basalbody.app.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


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

    fun Context.isLocationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getDirectionsUrl(
        origin: LatLng,
        dest: LatLng,
        markerPoints: java.util.ArrayList<LatLng> = arrayListOf(),
    ): String {
        val strOrigin = "origin=" + origin.latitude + "," + origin.longitude
        val strDestination = "destination=" + dest.latitude + "," + dest.longitude
        val sensor = "mode=driving&alternatives=false"
        val waypoints = if (markerPoints.isNotEmpty()) {
            "&waypoints=optimize:false|" + markerPoints.joinToString(separator = "|") { "${it.latitude},${it.longitude}" }
        } else {
            ""
        }
        val parameters: String = "$strOrigin$waypoints&$strDestination&$sensor"
        val output = "json"
        val key =
            "&key=${BuildConfig.MAPS_API_KEY}"
        //return url
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters$key"
    }


    fun distance(start: LatLng, end: LatLng): Float {

        val locationA = android.location.Location("point A")
        locationA.latitude = start.latitude
        locationA.longitude = start.longitude

        val locationB = android.location.Location("point B")
        locationB.latitude = end.latitude
        locationB.longitude = end.longitude
        return locationA.distanceTo(locationB)

    }

    fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor {
        val canvas = Canvas()
        val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun getBearingMap(begin: LatLng, end: LatLng): Float {
        val beginLatRadians = Math.toRadians(begin.latitude)
        val endLatRadians = Math.toRadians(end.latitude)
        val deltaLongitude = Math.toRadians(end.longitude - begin.longitude)

        val y = sin(deltaLongitude) * cos(endLatRadians)
        val x = (cos(beginLatRadians) * sin(endLatRadians)) -
                (sin(beginLatRadians) * cos(endLatRadians) * cos(deltaLongitude))
        var bearing = atan2(y, x)
        bearing = Math.toDegrees(bearing)
        return ((bearing + 360) % 360).toFloat()
    }
}