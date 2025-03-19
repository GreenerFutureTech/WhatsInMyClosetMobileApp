package org.greenthread.whatsinmycloset.features.screens.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.features.screens.notifications.data.NotificationRepository
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.Notification

class NotificationsViewModel (
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    init {
        loadNotifications(userId = 1)
    }

    private fun loadNotifications(userId: Int) {
        viewModelScope.launch {
            _notifications.value = notificationRepository.getNotifications(userId)
        }
    }

    fun markAsRead(notificationId: Int) {
        _notifications.value = _notifications.value.map { notification ->
            if (notification.id == notificationId) {
                notification.copy(isRead = true)
            } else {
                notification
            }
        }
    }

    fun dismissNotification(notificationId: Int) {
        _notifications.value = _notifications.value.filter { it.id != notificationId }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }
}