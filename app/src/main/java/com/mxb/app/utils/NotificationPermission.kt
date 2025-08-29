package com.mxb.app.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.annotation.RequiresApi
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mxb.app.R
import com.mxb.app.extensions.withNotNull

class NotificationPermission(
    private val context: Context,
    private val activityLauncher: ActivityLauncher<Intent, ActivityResult>
) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkNotificationPermission(onPermissionAllowed: () -> Unit) {
        Dexter.withContext(context)
            .withPermissions(Manifest.permission.POST_NOTIFICATIONS)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report.withNotNull {
                        if (it.areAllPermissionsGranted()) {
                            onPermissionAllowed.invoke()
                        } else if (it.isAnyPermissionPermanentlyDenied) {
                            // show alert dialog navigating to Settings
                            println("PermissionCheck_onPermissionDenied = {onPermissionDenied}")
                            CommonUtils.showSettingsDialog(
                                context = context,
                                activityLauncher = activityLauncher,
                                permissionMessage = context.resources.getString(R.string.message_notification_permission)
                            ) {
                                onPermissionAllowed.invoke()
                            }
                        } else {
                            onPermissionAllowed.invoke()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }
            }).check()
    }
}
