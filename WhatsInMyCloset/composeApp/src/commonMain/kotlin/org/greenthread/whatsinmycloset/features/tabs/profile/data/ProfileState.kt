package org.greenthread.whatsinmycloset.features.tabs.profile.data

import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.dto.OtherSwapDto

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
    val isOwnProfile: Boolean = false,
    val searchResults: User? = null,
    val friendshipStatus: FriendshipStatus = FriendshipStatus.NOT_FRIENDS,
    val searchedUserSwapItems: List<OtherSwapDto> = emptyList()
)

enum class FriendshipStatus {
    NOT_FRIENDS,
    REQUEST_SENT,
    REQUEST_RECEIVED,
    FRIENDS
}