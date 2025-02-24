package org.greenthread.whatsinmycloset.features.tabs.swap.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.greenthread.whatsinmycloset.core.dto.SwapDto

class SelectedSwapViewModel: ViewModel() {

    private val _selectedSwap = MutableStateFlow<SwapDto?>(null)
    val selectedSwap = _selectedSwap.asStateFlow()

    fun onSelectSwap(swap: SwapDto?) {
        _selectedSwap.value = swap
    }

}