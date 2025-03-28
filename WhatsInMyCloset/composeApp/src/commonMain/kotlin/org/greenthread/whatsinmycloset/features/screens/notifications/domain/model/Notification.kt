package org.greenthread.whatsinmycloset.features.screens.notifications.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
enum class NotificationType {
    @SerialName("post_like")
    POST_LIKE,

    @SerialName("swap_request")
    SWAP_REQUEST,

    @SerialName("friend_request")
    FRIEND_REQUEST,

    @SerialName("new_message")
    NEW_MESSAGE
}

@Serializable
data class Notification(
    val id: Int,
    val userId: Int,
    val type: NotificationType,
    val title: String,
    val body: String,
    val extraData: Map<String, String>? = null,
    val isRead: Boolean,
    val createdAt: String
)

@Serializable
data class NotificationDto (
    val userId: Int,
    val title: String,
    val body: String,
    val type: NotificationType,
    val extraData: Map<String, String>? = null,
)

@Serializable
data class SendNotificationRequest (
    val userId: Int,
    val title: String,
    val body: String,
    val type: NotificationType,
    val extraData: Map<String, String>? = null
)