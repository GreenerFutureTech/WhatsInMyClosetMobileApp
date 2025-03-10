package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class UserManager() {
    private val _currentUser = MutableStateFlow<Account?>(null)
    val currentUser: StateFlow<Account?> = _currentUser

    fun updateUser(user: Account) {
        _currentUser.value = user
    }

    fun getUser() : Account? {
        return currentUser.value
    }
}


