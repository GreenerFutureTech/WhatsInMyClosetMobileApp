package org.greenthread.whatsinmycloset.features.tabs.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.domain.onSuccess
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.core.repository.DefaultClosetRepository
import org.greenthread.whatsinmycloset.features.tabs.profile.data.FriendshipStatus
import org.greenthread.whatsinmycloset.features.tabs.profile.data.ProfileState
import org.greenthread.whatsinmycloset.features.tabs.profile.domain.FriendRequest
import org.greenthread.whatsinmycloset.features.tabs.profile.domain.RequestStatus

// Confirmation types for destructive actions: Remove friend, Cancel request, and Decline request
sealed class ConfirmationType {
    object RemoveFriend : ConfirmationType()
    object CancelRequest : ConfirmationType()
    object DeclineRequest : ConfirmationType()
}

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

    // Confirmation dialog state
    private val _showConfirmationDialog = MutableStateFlow<Pair<ConfirmationType, Int>?>(null)
    val showConfirmationDialog = _showConfirmationDialog.asStateFlow()

    init {
        // Start a coroutine to observe changes in the current user
        CoroutineScope(Dispatchers.IO).launch {
            userManager.currentUser.collectLatest { user ->
                if (user != null) {

                    _state.value = ProfileState()
                }
            }
        }
    }

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

        // First force refresh friends list
        userRepository.getFriendsByUserId(currentUserId, forceRefresh = true).onSuccess { friends ->
            if (friends.any { it.id == targetUserId }) {
                _state.update { it.copy(friendshipStatus = FriendshipStatus.FRIENDS) }
                println("================================== CHECKING IF ALREADY FRIENDS Friendship STATUS = ${_state.value.friendshipStatus}")
                return@onSuccess
            }

            // Check requests with force refresh
            userRepository.getSentFriendRequests(currentUserId, forceRefresh = true).onSuccess { sentRequests ->
                userRepository.getReceivedFriendRequests(currentUserId, forceRefresh = true).onSuccess { receivedRequests ->
                    when {
                        sentRequests.any { it.receiverId == targetUserId } -> {
                            _state.update { it.copy(friendshipStatus = FriendshipStatus.PENDING) }
                            println("================================== CHECKING SENT REQUEST Friendship STATUS = ${_state.value.friendshipStatus}")
                        }
                        receivedRequests.any { it.senderId == targetUserId } -> {
                            _currentProfileRequest.value = receivedRequests.first { it.senderId == targetUserId }.toDomain()
                            _state.update { it.copy(friendshipStatus = FriendshipStatus.REQUEST_RECEIVED) }
                            println("================================== CHECKING RECEIVED REQUEST Friendship STATUS = ${_state.value.friendshipStatus}")
                        }
                        else -> {
                            _state.update { it.copy(friendshipStatus = FriendshipStatus.NOT_FRIENDS) }
                            println("================================== CHECKING NOT FRIENDS Friendship STATUS = ${_state.value.friendshipStatus}")
                        }
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
                        // Refresh both users' data
                        val currentUserId = currentUser.value?.id ?: 0
                        loadProfile(currentUserId)

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

    private suspend fun refreshBothUsers(senderId: Int, receiverId: Int) {
        // Refresh both users' data from backend
        val currentUserId = currentUser.value?.id ?: return

        // Refresh current user's data
        userRepository.getUserById(currentUserId).onSuccess { currentUserDto ->
            val updatedUser = currentUserDto.toModel()

            // Update local state
            _state.update {
                it.copy(
                    user = updatedUser,
                    friendshipStatus = FriendshipStatus.FRIENDS,
                    isLoading = false
                )
            }

            // Update UserManager
            userManager.updateUser(updatedUser)

            loadProfile(currentUserId)
        }.onError { error ->
            _state.update {
                it.copy(
                    error = "Failed to refresh user data: $error",
                    isLoading = false
                )
            }
        }
    }

    // Show confirmation dialog
    fun showConfirmation(type: ConfirmationType, targetUserId: Int) {
        _showConfirmationDialog.value = type to targetUserId
    }

    // Dismiss confirmation dialog
    fun dismissConfirmation() {
        _showConfirmationDialog.value = null
    }
}


