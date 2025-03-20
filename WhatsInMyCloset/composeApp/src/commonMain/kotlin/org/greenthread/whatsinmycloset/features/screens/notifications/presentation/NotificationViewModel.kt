package org.greenthread.whatsinmycloset.features.screens.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.features.screens.notifications.data.NotificationRepository
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.Notification
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.NotificationType

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository,
    userManager: UserManager,
) : ViewModel() {

    // holds the list of notifications
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    // holds the refreshing state for UI updates when refreshing
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // current user ID to fetch notifications
    private val currentUserId = userManager.currentUser.value?.retrieveUserId()

    init {
        loadNotifications(currentUserId)
    }

    /**
     * Loads notifications from the repository based on the user's ID.
     *
     * @param userId The ID of the current user.
     *
     */
    private fun loadNotifications(userId: Int?) {
        if (userId == null) {
            _notifications.value = emptyList()
            return
        }

        viewModelScope.launch {
            _notifications.value = notificationRepository.getNotifications(userId)
        }
    }

    /**
     * Refreshes the notifications by re-fetching them from the repository.
     *
     */
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

    /**
     * Navigates to the appropriate screen based on the notification type.
     *
     * @param navController The navigation controller used to navigate between screens.
     * @param notification The notification object containing the type and extra data.
     *
     */
    fun gotoNotification(navController: NavController, notification: Notification) {

        when(notification.type) {
            NotificationType.FRIEND_REQUEST -> {
                /*
                val userId = extraData?.get("userId")
                userId?.let {
                    navController.navigate(Routes.ProfileTab)
                }
                */
                navController.navigate(Routes.ProfileTab)
            }
            NotificationType.SWAP_REQUEST -> {
                navController.navigate(Routes.SwapTab)
            }
            // Default Case
            else -> {
                println("Type Not Mapped")
                navController.navigate(Routes.HomeTab)
            }
        }
    }

    /**
     * Dismisses the notification by removing it from the list and deleting it from the backend.
     *
     * @param notificationId The ID of the notification to be dismissed.
     *
     */
    fun dismissNotification(notificationId: Int) {
        viewModelScope.launch {
            _notifications.value = _notifications.value.filter { it.id != notificationId }

            notificationRepository.deleteNotification(notificationId)
        }
    }

    /**
     * Clears all notifications for the current user.
     *
     */
    fun clearAllNotifications() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                _notifications.value = emptyList()
                notificationRepository.clearNotification(userId)
            }
        }
    }

}