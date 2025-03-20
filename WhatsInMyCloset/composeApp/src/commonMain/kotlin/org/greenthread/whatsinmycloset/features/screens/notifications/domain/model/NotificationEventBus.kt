package org.greenthread.whatsinmycloset.features.screens.notifications.domain.model

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationEventBus {
    private val _newNotificationEvent = MutableSharedFlow<Unit>(replay = 1)
    val newNotificationEvent = _newNotificationEvent.asSharedFlow()

    // Add this property to track state
    private val _hasNewNotifications = MutableStateFlow(false)
    val hasNewNotifications = _hasNewNotifications.asStateFlow()

    suspend fun emitNewNotification() {
        _hasNewNotifications.value = true
        _newNotificationEvent.emit(Unit)
    }

    fun clearNewNotificationsState() {
        _hasNewNotifications.value = false
    }

    fun setHasNewNotifications(value: Boolean) {
        _hasNewNotifications.value = value
    }
}