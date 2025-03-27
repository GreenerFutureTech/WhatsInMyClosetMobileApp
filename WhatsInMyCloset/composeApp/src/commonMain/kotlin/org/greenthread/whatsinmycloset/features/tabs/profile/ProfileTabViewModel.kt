package org.greenthread.whatsinmycloset.features.tabs.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.domain.onSuccess
import org.greenthread.whatsinmycloset.core.repository.DefaultClosetRepository
import org.greenthread.whatsinmycloset.features.tabs.profile.data.FriendshipStatus
import org.greenthread.whatsinmycloset.features.tabs.profile.data.ProfileState

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

    private val _friendRequestStatus = MutableStateFlow<FriendshipStatus>(FriendshipStatus.NOT_FRIENDS)
    val friendRequestStatus = _friendRequestStatus.asStateFlow()

    // Search related state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    private val _searchResult = MutableStateFlow<User?>(null)
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

    private suspend fun checkFriendRequestStatus(targetUserId: Int) {
        userRepository.getSentFriendRequests(currentUser.value?.id ?: return)
            .onSuccess { requests ->
                _state.update {
                    it.copy(
                        friendshipStatus = if (requests.any { it.receiverId == targetUserId }) {
                            FriendshipStatus.PENDING
                        } else {
                            FriendshipStatus.NOT_FRIENDS
                        }
                    )
                }
            }
    }

    // Search functions
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchUser() {
        viewModelScope.launch {
            if (_searchQuery.value.isNotBlank()) {
                _state.update { it.copy(isLoading = true, error = null) }

                userRepository.getUserByUserName(_searchQuery.value)
                    .onSuccess { user ->
                        _searchResult.value = user.toModel()
                        _state.update { it.copy(isLoading = false) }
                    }
                    .onError { error ->
                        _searchResult.value = null
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
        _searchResult.value = null
    }

    fun sendFriendRequest(receiverId: Int) {
        viewModelScope.launch {
            val senderId = currentUser.value?.id ?: return@launch
            _isLoading.value = true

            userRepository.sendFriendRequest(senderId, receiverId)
                .onSuccess {
                    _friendRequestStatus.value = FriendshipStatus.PENDING // Update status
                    _error.value = null
                }
                .onError { error ->
                    _error.value = "Failed to send request: $error"
                }

            _isLoading.value = false
        }
    }
}
