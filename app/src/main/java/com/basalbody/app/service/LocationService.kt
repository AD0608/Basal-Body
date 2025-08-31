package com.basalbody.app.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.basalbody.app.R
import com.basalbody.app.datastore.LocalDataRepository
import com.basalbody.app.ui.home.activity.HomeActivity
import com.basalbody.app.utils.Constants
import com.basalbody.app.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val DELAY_FOR_EMIT: Long = 10_000

@AndroidEntryPoint
class LocationService : Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        val service: LocationService get() = this@LocationService
    }

    companion object {
        private const val INTERVAL: Long = 2_000
        private const val FASTEST_INTERVAL: Long = 2_000
        private const val CHANNEL_ID = "LocationServiceChannel"
        private const val NOTIFICATION_ID = 1
        private const val LOCATION_DISPLACEMENT = 20f
    }

    @Inject
    lateinit var localDataRepository: LocalDataRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private val locationUpdateHandler by lazy { Handler(Looper.getMainLooper()) }
    private val locationRunnable = Runnable { socketEmit() }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.let { location ->
                Constants.latitude = location.latitude
                Constants.longitude = location.longitude
                Logger.e(
                    "CurrentLocation",
                    "Lat: ${location.latitude}, Long: ${location.longitude}"
                )
                locationUpdateHandler.postDelayed(locationRunnable, DELAY_FOR_EMIT)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        Logger.e("LocationService", "onCreate")
        initLocation()
        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        socketOn()
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        Logger.e("LocationService", "onDestroy")
        stopLocationUpdates()
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Logger.e("LocationService", "onTaskRemoved: App was removed from recent")
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    private fun initLocation() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL)
            .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
            .setMinUpdateDistanceMeters(LOCATION_DISPLACEMENT)
            .build()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        locationUpdateHandler.removeCallbacksAndMessages(null)
    }

    private fun socketOn() {
        socketEmit()
    }

    private fun socketEmit() {
        /*val shipmentId = localDataRepository.getUserDetails()?.ongoingShipment?.shipmentId ?: 0
        if (shipmentId != 0) {
            val driverId = localDataRepository.getUserDetails()?.id ?: 0
            SocketUtils.emitUpdateLocation(
                shipmentId = shipmentId,
                lat = Constants.latitude,
                lng = Constants.longitude,
                driverId = driverId
            )
        } else {
            Logger.e("Location Service", "Stopping service due to invalid shipment id")
            onDestroy()
        }*/
    }

    private fun createNotification(): Notification {
        createNotificationChannel()
        val notificationIntent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("${getString(R.string.app_name)} ${getString(R.string.is_running)}")
            .setContentText(getString(R.string.tap_to_open))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Location Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            setShowBadge(true)
        }

        (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)
            ?.createNotificationChannel(channel)
    }
}