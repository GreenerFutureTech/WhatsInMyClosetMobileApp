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

class NotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle the new FCM token here
        println("New FCM Token: $token")
        // Send the token to your backend server
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle the incoming FCM message here
        println("Received FCM message: ${remoteMessage.notification?.title}")

        // Display notification to the user
        remoteMessage.notification?.let { notification ->
            sendNotification(notification.title, notification.body)
        }

        // If data payload is present, handle it
        if (remoteMessage.data.isNotEmpty()) {
            // Process data payload if needed
            val title = remoteMessage.data["title"] ?: "New Message"
            val message = remoteMessage.data["message"] ?: "You have a new notification"
            sendNotification(title, message)
        }
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val channelId = "fcm_default_channel"
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Create this icon in your drawable folder
            .setContentTitle(title ?: "Notification")
            .setContentText(messageBody ?: "You have a new notification")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
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