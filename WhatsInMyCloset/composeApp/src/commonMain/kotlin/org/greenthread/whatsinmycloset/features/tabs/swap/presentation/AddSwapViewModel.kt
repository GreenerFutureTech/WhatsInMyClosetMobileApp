package org.greenthread.whatsinmycloset.features.tabs.swap.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

class AddSwapViewModel(
    private val wardrobeManager: WardrobeManager,
) : ViewModel() {

    private val _wardrobes = MutableStateFlow<List<Wardrobe>>(emptyList())
    val wardrobes: StateFlow<List<Wardrobe>> get() = _wardrobes

    init {
        fetchWardrobes()
    }

    private fun fetchWardrobes() {
        _wardrobes.value = wardrobeManager.cachedWardrobes
    }
}
