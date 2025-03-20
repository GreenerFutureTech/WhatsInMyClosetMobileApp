package org.greenthread.whatsinmycloset.features.screens.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.features.screens.notifications.data.NotificationRepository
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.Notification

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository,
    userManager: UserManager,
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    // Add a refreshing state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val currentUserId = userManager.currentUser.value?.retrieveUserId()

    init {
        loadNotifications(currentUserId)
    }

    private fun loadNotifications(userId: Int?) {
        if (userId == null) {
            _notifications.value = emptyList()
            return
        }

        viewModelScope.launch {
            _notifications.value = notificationRepository.getNotifications(userId)
        }
    }

    // Add a refresh function
    fun refresh() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _isRefreshing.value = true
                try {
                    _notifications.value = notificationRepository.getNotifications(userId, forceRefresh = true)
                } finally {
                    _isRefreshing.value = false
                }
            }
        }
    }

    // Existing functions remain the same
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
        viewModelScope.launch {
            // First remove from local list for immediate UI feedback
            _notifications.value = _notifications.value.filter { it.id != notificationId }

            // Then delete from backend
            notificationRepository.deleteNotification(notificationId)
        }
    }

    fun clearAllNotifications() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _notifications.value = emptyList()
                notificationRepository.clearNotification(userId)
            }
        }
    }

}