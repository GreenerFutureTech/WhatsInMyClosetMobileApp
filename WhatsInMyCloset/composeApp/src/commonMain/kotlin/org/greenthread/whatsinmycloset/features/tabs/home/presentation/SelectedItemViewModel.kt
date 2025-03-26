package org.greenthread.whatsinmycloset.features.tabs.home.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem

class SelectedItemViewModel:ViewModel() {
    private val _selectedItem = MutableStateFlow<ClothingItem?>(null)
    val selectedItem = _selectedItem.asStateFlow()

    fun onSelectItem(item: ClothingItem?) {
        _selectedItem.value = item
    }
}
