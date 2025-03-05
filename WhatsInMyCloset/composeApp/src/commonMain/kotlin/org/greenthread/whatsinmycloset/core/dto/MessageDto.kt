package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto (
    val id: Int,
    val sender: UserDto,
    val receiver: UserDto,
    val content: String,
    val sentAt: String,
    val isRead: Boolean
)



