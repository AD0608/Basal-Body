package com.basalbody.app.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.basalbody.app.R
import com.basalbody.app.extensions.withNotNull

class CameraAndExternalStoragePermission(
    private val context: Context,
    private val activityLauncher: ActivityLauncher<Intent, ActivityResult>
) {
    fun checkCameraPermission(onPermissionAllowed: () -> Unit) {
        Dexter.withContext(context)
            .withPermissions(Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report.withNotNull {
                        if (it.areAllPermissionsGranted()) {
                            onPermissionAllowed.invoke()
                        } else if (it.isAnyPermissionPermanentlyDenied) {
                            CommonUtils.showSettingsDialog(
                                context = context,
                                activityLauncher = activityLauncher,
                                permissionMessage = context.resources.getString(R.string.message_camera_permission)
                            )
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

    fun checkStoragePermission(onPermissionAllowed: () -> Unit) {
        Dexter.withContext(context).apply {
            when {
                // Android 6 - 12 (API 23 - 32) → Requires READ_EXTERNAL_STORAGE
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        onPermissionAllowed.invoke()
                    } else {
                        this.withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(createPermissionListener(onPermissionAllowed))
                            .check()
                    }
                }

                // Below Android 6 (API < 23) → No runtime permission needed
                else -> {
                    onPermissionAllowed.invoke()
                }
            }
        }
    }


    private fun createPermissionListener(onPermissionAllowed: () -> Unit): MultiplePermissionsListener {
        return object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report.withNotNull {
                    if (it.areAllPermissionsGranted()) {
                        onPermissionAllowed.invoke()
                    } else if (it.isAnyPermissionPermanentlyDenied) {
                        CommonUtils.showSettingsDialog(
                            context = context,
                            activityLauncher = activityLauncher,
                            permissionMessage = context.resources.getString(R.string.message_storage_permission)
                        )
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                token?.continuePermissionRequest()
            }
        }
    }
}
