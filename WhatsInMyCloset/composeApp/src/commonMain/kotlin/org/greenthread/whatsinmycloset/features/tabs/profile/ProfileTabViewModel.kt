package org.greenthread.whatsinmycloset.features.tabs.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.domain.models.UserManager

class ProfileTabViewModel(
    private val userManager: UserManager
) : ViewModel() {
    val userState: StateFlow<User?> = userManager.currentUser
}
