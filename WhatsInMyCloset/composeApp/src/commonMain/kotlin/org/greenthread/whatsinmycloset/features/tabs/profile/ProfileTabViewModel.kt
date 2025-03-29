package org.greenthread.whatsinmycloset.features.tabs.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.domain.models.Friend
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.domain.onSuccess
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.core.repository.DefaultClosetRepository
import org.greenthread.whatsinmycloset.features.tabs.profile.data.FriendshipStatus
import org.greenthread.whatsinmycloset.features.tabs.profile.data.ProfileState
import org.greenthread.whatsinmycloset.features.tabs.profile.domain.FriendRequest
import org.greenthread.whatsinmycloset.features.tabs.profile.domain.RequestStatus

class ProfileTabViewModel(
    private val userRepository: DefaultClosetRepository,
    private val userManager: UserManager
) : ViewModel() {
    val currentUser = userManager.currentUser

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Track the specific friend request for the current profile
    private val _currentProfileRequest = MutableStateFlow<FriendRequest?>(null)
    val currentProfileRequest = _currentProfileRequest.asStateFlow()

    // Search related state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    private val _searchResult = MutableStateFlow<List<UserDto?>>(emptyList())
    val searchResult = _searchResult.asStateFlow()

    // Load profile
    fun loadProfile(userId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            userRepository
                .getUserById(userId)
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            user = user.toModel(),
                            isOwnProfile = userId == currentUser.value?.id,    // Update ProfileState
                        )
                    }

                    if (userId != currentUser.value?.id) {
                        checkFriendRequestStatus(targetUserId = userId)
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            // TODO handle error
                            error = error.toString()
                        )
                    }
                }

            _state.update {
                it.copy(isLoading = false)
            }

        }
    }


    // Check friendship status
    private suspend fun checkFriendRequestStatus(targetUserId: Int) {
        val currentUserId = currentUser.value?.id ?: return

        // Check if already friends
        userRepository.getFriendsByUserId(currentUserId).onSuccess { friends ->
            if (friends.any { it.id == targetUserId }) {
                _state.update { it.copy(friendshipStatus = FriendshipStatus.FRIENDS) }
                return@onSuccess
            }

            // Check sent requests
            userRepository.getSentFriendRequests(currentUserId).onSuccess { sentRequests ->
                if (sentRequests.any { it.receiverId == targetUserId }) {
                    _state.update { it.copy(friendshipStatus = FriendshipStatus.PENDING) }
                    return@onSuccess
                }

                // Check received requests
                userRepository.getReceivedFriendRequests(currentUserId).onSuccess { receivedRequests ->
                    receivedRequests.firstOrNull { it.senderId == targetUserId }?.let { request ->
                        _currentProfileRequest.value = request.toDomain()
                        _state.update { it.copy(friendshipStatus = FriendshipStatus.REQUEST_RECEIVED) }
                    } ?: run {
                        _state.update { it.copy(friendshipStatus = FriendshipStatus.NOT_FRIENDS) }
                    }
                }
            }
        }
    }

    fun loadUserFriends() {
        val userId = currentUser.value?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            userRepository.getUserById(userId)
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            user = user.toModel(),
                            isLoading = false,
                            isOwnProfile = true
                        )
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            error = error.toString(),
                            isLoading = false
                        )
                    }
                }
        }
    }

    // Search functions
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        println("query : $query")
        println("search value: ${_searchQuery.value}")
    }

    fun searchUser() {
        viewModelScope.launch {
            if (_searchQuery.value.isNotBlank()) {
                _state.update { it.copy(isLoading = true, error = null) }

                userRepository.searchUserByUsername(_searchQuery.value)
                    .onSuccess { users ->
                        _searchResult.value = users
                        _state.update { it.copy(isLoading = false) }
                    }
                    .onError { error ->
                        _searchResult.value = emptyList()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = error.toString()
                            )
                        }
                    }
            }
        }
    }

    fun clearSearchResults() {
        _searchResult.value = emptyList()
        _searchQuery.value = ""
    }

    // Send friendship request
    fun sendFriendRequest(receiverId: Int) {
        viewModelScope.launch {
            val senderId = currentUser.value?.id ?: return@launch
            _isLoading.value = true

            userRepository.sendFriendRequest(senderId, receiverId)
                .onSuccess {
                    _state.update {
                        it.copy(friendshipStatus = FriendshipStatus.PENDING)
                    }
                    _error.value = null
                }
                .onError { error ->
                    _error.value = "Failed to send request: $error"
                }

            _isLoading.value = false
        }
    }

    // Respond to friendship request
    fun respondToRequest(accept: Boolean) {
        val request = _currentProfileRequest.value ?: return
        val currentState = _state.value

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Convert boolean to RequestStatus
            val status = when {
                accept -> RequestStatus.ACCEPTED
                else -> RequestStatus.REJECTED
            }

            userRepository.respondToFriendRequest(request.id, status)
                .onSuccess {
                    if (accept) {
                        // Fetch sender's details to create a proper Friend object
                        userRepository.getUserById(request.senderId).onSuccess { senderUser ->
                            val newFriend = Friend(
                                id = senderUser.id,
                                username = senderUser.username,
                                name = senderUser.name,
                                profilePicture = senderUser.profilePicture
                            )

                            // Update friends list
                            val updatedUser = currentState.user?.copy(
                                friends = currentState.user.friends.orEmpty() + newFriend
                            )

                            _state.update {
                                it.copy(
                                    user = updatedUser,
                                    friendshipStatus = FriendshipStatus.FRIENDS,
                                    isLoading = false
                                )
                            }

                            // Also update current user in UserManager
                            currentUser.value?.let { user ->
                                userManager.updateUser(
                                    user.copy(
                                        friends = user.friends.orEmpty() + newFriend
                                    )
                                )
                            }
                        }.onError { error ->
                            _state.update {
                                it.copy(
                                    error = "Failed to fetch user details: $error",
                                    isLoading = false
                                )
                            }
                        }
                    } else {
                        // Just update status if declined
                        _state.update {
                            it.copy(
                                friendshipStatus = FriendshipStatus.NOT_FRIENDS,
                                isLoading = false
                            )
                        }
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            error = "Failed to respond to request: $error",
                            isLoading = false
                        )
                    }
                }


        }
    }

    // Remove a friend
    fun removeFriend(friendId: Int) {
        viewModelScope.launch {
            val currentUserId = currentUser.value?.id ?: return@launch
            _state.update { it.copy(isLoading = true, error = null) }

            userRepository.removeFriend(currentUserId, friendId)
                .onSuccess {
                    // Update the current user's friends list
                    val updatedUser = state.value.user?.copy(
                        friends = state.value.user?.friends?.filter { it.id != friendId }
                    )

                    // Update UserManager
                    currentUser.value?.let { user ->
                        userManager.updateUser(
                            user.copy(friends = user.friends?.filter { it.id != friendId })
                        )
                    }

                    // Update state
                    _state.update {
                        it.copy(
                            user = updatedUser,
                            friendshipStatus = FriendshipStatus.NOT_FRIENDS,
                            isLoading = false
                        )
                    }
                }
                .onError { error ->
                    _state.update { it.copy(
                        error = "Failed to remove friend: $error",
                        isLoading = false
                    ) }
                }

        }
    }

    // Cancel sent request
    fun cancelRequest(receiverId: Int) {
        viewModelScope.launch {
            val senderId = currentUser.value?.id ?: return@launch
            _state.update { it.copy(isLoading = true) }

            userRepository.cancelFriendRequest(senderId, receiverId)
                .onSuccess {
                    _state.update {
                        it.copy(friendshipStatus = FriendshipStatus.NOT_FRIENDS)
                    }
                }
                .onError { error ->
                    _state.update { it.copy(error = error.toString()) }
                }

            _state.update { it.copy(isLoading = false) }
        }
    }
}


