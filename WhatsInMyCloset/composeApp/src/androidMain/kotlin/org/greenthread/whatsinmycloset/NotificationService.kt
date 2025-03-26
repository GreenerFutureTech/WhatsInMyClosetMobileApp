package org.greenthread.whatsinmycloset

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.NotificationEventBus
import org.greenthread.whatsinmycloset.features.tabs.swap.domain.SwapEventBus

class NotificationService : FirebaseMessagingService() {

    companion object {
        var isAppInForeground = false
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("New FCM Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        println("Received FCM message: ${remoteMessage.notification?.title}")

        // If app is in use, don't show push notifications
        if (!isAppInForeground) {

            if (remoteMessage.data.isNotEmpty()) {
                val title = remoteMessage.data["title"] ?: "New Message"
                val message = remoteMessage.data["message"] ?: "You have a new notification"
                sendNotification(title, message)
            }
        }

        // Handle specific message types
        when (remoteMessage.data["type"]) {
            "new_message" -> {

                val messageId = remoteMessage.data["messageId"]
                CoroutineScope(Dispatchers.IO).launch {
                    SwapEventBus.emitNewNotification(messageId)
                }
            }
            else -> {
                CoroutineScope(Dispatchers.IO).launch {
                    NotificationEventBus.emitNewNotification()
                }
            }
        }
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val channelId = "fcm_default_channel"

        // Intent to navigate to notifications screen
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("NAVIGATE_TO", "notifications")
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationTitle = title ?: return
        val notificationMessage = messageBody ?: return

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notificationTitle)
            .setContentText(notificationMessage)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "FCM Notifications"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Use a unique ID for each notification
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}