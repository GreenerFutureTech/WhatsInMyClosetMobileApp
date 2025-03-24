package org.greenthread.whatsinmycloset.features.screens.notifications.data

import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.Notification

class NotificationRepository(
    private val remoteDataSource: KtorRemoteDataSource
) {

    suspend fun getNotifications(userId: Int, forceRefresh: Boolean = false): List<Notification> {
        // You can implement caching here if needed
        return remoteDataSource.getUserNotifications(userId).getOrNull() ?: emptyList()
    }

    fun refreshNotifications(userId: Int) {
        // You can add logic to trigger a refresh here
    }

    suspend fun deleteNotification(notificationId: Int) {
        remoteDataSource.dismissNotification(notificationId)
    }

    suspend fun clearNotification(userId: Int) {
        remoteDataSource.clearNotification(userId)
    }

}