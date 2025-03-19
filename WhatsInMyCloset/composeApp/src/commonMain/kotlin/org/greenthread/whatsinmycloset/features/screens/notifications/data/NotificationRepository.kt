package org.greenthread.whatsinmycloset.features.screens.notifications.data

import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.Notification

class NotificationRepository(
    private val remoteDataSource: KtorRemoteDataSource
) {

    suspend fun getNotifications(userId: Int): List<Notification> {
        return remoteDataSource.getUserNotifications(userId).getOrNull() ?: emptyList()
    }

    fun refreshNotifications(userId: Int) {
        // You can add logic to trigger a refresh here
    }
}