package org.greenthread.whatsinmycloset.features.tabs.profile.data

import org.greenthread.whatsinmycloset.core.domain.models.User

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
    val isOwnProfile: Boolean = false,
    val searchResults: User? = null,
    val friendshipStatus: FriendshipStatus = FriendshipStatus.NOT_FRIENDS,
)

enum class FriendshipStatus {
    NOT_FRIENDS,
    PENDING,
    REQUEST_RECEIVED,
    FRIENDS,
}