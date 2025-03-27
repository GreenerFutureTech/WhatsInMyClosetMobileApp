package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.features.tabs.profile.domain.FriendRequest
import org.greenthread.whatsinmycloset.features.tabs.profile.domain.RequestStatus

@Serializable
data class FriendRequestDto(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val status: String,
    val createdAt: String
) {
    fun toDomain(): FriendRequest {
        return FriendRequest(
            id = id,
            senderId = senderId,
            receiverId = receiverId,
            status = when (status.uppercase()) {
                "ACCEPTED" -> RequestStatus.ACCEPTED
                "REJECTED" -> RequestStatus.REJECTED
                else -> RequestStatus.PENDING
            },
            createdAt = createdAt
        )
    }
}