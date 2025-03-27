package org.greenthread.whatsinmycloset.features.tabs.profile.domain

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val status: RequestStatus, // Different from your existing FriendshipStatus
    val createdAt: String
)

enum class RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}