package com.basalbody.app.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.basalbody.app.R
import com.basalbody.app.datastore.LocalDataRepository
import com.basalbody.app.ui.home.activity.HomeActivity
import com.basalbody.app.utils.Constants.EMPTY_STRING
import com.basalbody.app.utils.EnumUtils
import com.basalbody.app.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var title = EMPTY_STRING
    private var type = EMPTY_STRING
    private var message = EMPTY_STRING
//    private var notificationData: PushNotificationData? = null

    @Inject
    lateinit var localDataRepository: LocalDataRepository

    @Inject
    lateinit var gson: Gson

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val remoteMessageData = remoteMessage.data
        Logger.e("Data Payload: $remoteMessageData")
        title = remoteMessageData["title"] ?: getString(R.string.app_name)
        message = remoteMessageData["message"].toString()
        val data = remoteMessageData["data"] ?: ""
//        if (data.isNotEmpty()) {
//            notificationData = gson.fromJson(data, PushNotificationData::class.java)
//        }

        val channel = getString(R.string.default_notification_channel)
        val notificationBuilder =
            getNotificationBuilder(
                channel,
                title,
                message,
                /*notificationData?.type*/
            )
        val random = (0..100000).random()
        /*when (notificationData?.type) {
            EnumUtils.NotificationType.LOGOUT.type -> {
                localDataRepository.resetUserData()
                val intent = Intent(this, RoleSelectionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                this.startActivity(intent)

            }
        }*/

        // No need to show notification for current ongoing chat
        /*if (notificationData?.type == EnumUtils.NotificationType.CHAT.type && Constants.CHAT_SHIPMENT_ID == notificationData?.id)
            return

        if (notificationData?.type != EnumUtils.NotificationType.LOGOUT.type) {
            Constants.UNREAD_NOTIFICATION_COUNT++
            val intent = Intent(Constants.ACTION_UPDATE_NOTIFICATION_COUNT)
            intent.setPackage(packageName)
            sendBroadcast(intent)
        }*/
        sendNotification(random, channel, notificationBuilder)
    }

    override fun onNewToken(token: String) {
        //-------HERE GET UPDATED NEW FCM TOKEN-----//
        localDataRepository.saveFcmToken(token)
    }

    private fun getNotificationBuilder(
        channelId: String,
        title: String,
        messageBody: String,
        notificationType: String? = EnumUtils.NotificationType.GENERAL.type
    ): NotificationCompat.Builder {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val contentIntent =
            if (notificationType != EnumUtils.NotificationType.LOGOUT.type) returnPendingIntent(
                returnIntent()
            ) else null
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setColor(ContextCompat.getColor(this, R.color.colorSecondary))
            .setColorized(true)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setSound(defaultSoundUri)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_MAX)
    }

    private fun sendNotification(
        random: Int,
        channelId: String,
        notificationBuilder: NotificationCompat.Builder,
    ) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel =
            NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(random, notificationBuilder.build())
    }

    private fun returnPendingIntent(intent: Intent): PendingIntent {
        return PendingIntent.getActivity(applicationContext, 0, intent, getPendingIntentFlag())
    }

    private fun getPendingIntentFlag() =
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    private fun returnIntent(
    ): Intent {
        val destinationClass: Class<*> = HomeActivity::class.java/*when (notificationData?.type) {
            EnumUtils.NotificationType.NEW_SHIPMENT_REQUEST.type,
            EnumUtils.NotificationType.SHIPMENT_REQUESTED.type,
            EnumUtils.NotificationType.SHIPMENT_ASSIGNED.type,
            EnumUtils.NotificationType.SHIPMENT_CANCELLED.type -> {
                LoadDetailActivity::class.java
            }

            EnumUtils.NotificationType.PAYMENT_REQUEST_APPROVED.type,
            EnumUtils.NotificationType.PAYMENT_REQUEST_REJECTED.type -> {
                MyEarningsActivity::class.java
            }

            EnumUtils.NotificationType.LICENSE_EXPIRY_REMINDER.type -> {
                LegalDocsActivity::class.java
            }

            EnumUtils.NotificationType.CHAT.type -> {
                ChatDetailsActivity::class.java
            }

            else -> {
                HomeActivity::class.java
            }
        }*/
        val intent = Intent(applicationContext, destinationClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        /*intent.putExtra(Constants.BUNDLE_KEY_NOTIFICATION_TYPE, notificationData?.type)
        intent.putExtra(Constants.BUNDLE_KEY_NOTIFICATION_ID, notificationData?.id)
        if (notificationData?.type == EnumUtils.NotificationType.CHAT.type) {
            intent.putExtra(Constants.BUNDLE_KEY_FROM_NOTIFICATION, true)
            intent.putExtra(
                Constants.BUNDLE_KEY_CHAT_NOTIFICATION_DATA,
                gson.toJson(notificationData)
            )
        }*/
        return intent
    }
}