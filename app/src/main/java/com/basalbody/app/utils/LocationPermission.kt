package com.basalbody.app.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.basalbody.app.R
import com.basalbody.app.extensions.withNotNull

object LocationPermission {
    fun checkLocationAndStartService(
        context: Context,
        deviceLocationRequestLauncher: ActivityResultLauncher<IntentSenderRequest>,
        settingsDialogActivityLauncher: ActivityLauncher<Intent, ActivityResult>,
        onServiceStart: () -> Unit
    ) {
        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report.withNotNull {
                        if (it.areAllPermissionsGranted()) {
                            checkDeviceLocationEnabled(
                                context = context,
                                activityLauncher = deviceLocationRequestLauncher,
                                onLocationEnabled = {
                                    onServiceStart.invoke()
                                }
                            )
                        } else if (it.isAnyPermissionPermanentlyDenied) {
                            CommonUtils.showSettingsDialog(
                                context = context,
                                activityLauncher = settingsDialogActivityLauncher,
                                permissionMessage = context.getString(R.string.message_location_permission)
                            )
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    CommonUtils.showOkDialog(
                        context = context,
                        message = context.getString(R.string.message_location_permission_use),
                        onButtonClick = {
                            token?.continuePermissionRequest()
                        })
                }
            }).check()
    }

    fun checkDeviceLocationEnabled(
        context: Context,
        activityLauncher: ActivityResultLauncher<IntentSenderRequest>,
        onLocationEnabled: () -> Unit
    ) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        val settingsClient = LocationServices.getSettingsClient(context)
        settingsClient.checkLocationSettings(settingsRequest)
            .addOnSuccessListener {
                onLocationEnabled()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(exception.resolution).build()
                        activityLauncher.launch(intentSenderRequest)
                    } catch (sendEx: Exception) {
                        sendEx.printStackTrace()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Unable to detect location settings",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}