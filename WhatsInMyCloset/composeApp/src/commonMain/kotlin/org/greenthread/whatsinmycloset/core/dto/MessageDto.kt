package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto (
    val id: Int,
    val sender: MessageUserDto,
    val receiver: MessageUserDto,
    val content: String,
    val sentAt: String,
    val isRead: Boolean
)

@Serializable
data class MessageUserDto (
    var id: Int = 0,
    val username: String = "",
    val name: String = "",
    val profilePicture: String? = ""
)

@Serializable
data class SendMessageRequest(
    val senderId: Int,
    val receiverId: Int,
    val content: String
)

