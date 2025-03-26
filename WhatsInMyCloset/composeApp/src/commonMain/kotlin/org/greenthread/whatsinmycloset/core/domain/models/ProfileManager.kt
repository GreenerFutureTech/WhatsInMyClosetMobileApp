package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.greenthread.whatsinmycloset.core.dto.UserDto


class UserManager() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun updateUser(user: User?) {
        _currentUser.value = user
    }

    fun getUser() : User? {
        return currentUser.value
    }
}


