package org.greenthread.whatsinmycloset

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
    }
}