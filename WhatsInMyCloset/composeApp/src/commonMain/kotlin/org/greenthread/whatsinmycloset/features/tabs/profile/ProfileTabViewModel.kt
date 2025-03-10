package org.greenthread.whatsinmycloset.features.tabs.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity
import org.greenthread.whatsinmycloset.core.repositories.WardrobeRepository
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe
import org.greenthread.whatsinmycloset.features.tabs.home.domain.HomeTabAction

class ProfileTabViewModel(
    private val userManager: UserManager
) : ViewModel() {
    val userState: StateFlow<Account?> = userManager.currentUser
}
