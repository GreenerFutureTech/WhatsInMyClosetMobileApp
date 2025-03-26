package org.greenthread.whatsinmycloset.features.tabs.swap.domain

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

object SwapEventBus {
    private val _newNotificationEvent = MutableSharedFlow<String?>(replay = 1)
    val newNotificationEvent = _newNotificationEvent.asSharedFlow()

    // Add this property to track state
    private val _hasNewNotifications = MutableStateFlow(false)
    val hasNewNotifications = _hasNewNotifications.asStateFlow()

    suspend fun emitNewNotification(messageId: String? = null) {
        println("Message ID message bus: ${messageId}")

        _hasNewNotifications.value = true
        _newNotificationEvent.emit(messageId)
    }

    fun clearNewNotificationsState() {
        _hasNewNotifications.value = false
    }

    fun setHasNewNotifications(value: Boolean) {
        _hasNewNotifications.value = value
    }
}