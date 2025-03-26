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
import org.greenthread.whatsinmycloset.features.tabs.profile.data.ProfileState

class ProfileTabViewModel(
    private val userRepository: DefaultClosetRepository,
    private val userManager: UserManager
) : ViewModel() {
    val currentUser = userManager.currentUser
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    // Search related state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    private val _searchResult = MutableStateFlow<User?>(null)
    val searchResult = _searchResult.asStateFlow()

    // Load profile
    fun loadProfile(userId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Check who's the user
            // OwnProfile: user logged in
            // When isOwnProfile is false, it's someone else's profile
            val isOwnProfile = userId == currentUser.value?.id

            userRepository
                .getUserById(userId)
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            user = user.toModel(),
                            isLoading = false,
                            isOwnProfile = isOwnProfile,    // Update ProfileState
                        )
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            // TODO handle error
                            error = error.toString()
                        )
                    }
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
}
