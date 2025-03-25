package org.greenthread.whatsinmycloset.features.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.domain.DataError
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.domain.onSuccess
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.core.repository.ClosetRepository
import org.greenthread.whatsinmycloset.features.screens.login.presentation.LoginViewModel

class EditProfileViewModel(
    private val userManager: UserManager,
    private val userRepository: ClosetRepository
) : ViewModel() {

    val currentUser = userManager.currentUser

     fun updateUser(user: UserDto) {
        viewModelScope.launch {
            println("UPDATE USER : Updated user ${user.id}")
            userRepository
                .updateUser(user)
                .onSuccess { getResults ->
                    println("UPDATE USER  API SUCCESS: $getResults")
                }
                .onError { error ->
                    println("UPDATE USER  API ERROR $error")
                }
        }
    }
}

