package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem

/*
* For each clothing item needing independent logic
* */

class ClothingItemViewModel : ViewModel() {
    private val _clothingItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val clothingItems: StateFlow<List<ClothingItem>> = _clothingItems

    fun addClothingItem(item: ClothingItem) {
        _clothingItems.value = _clothingItems.value + item
    }

    fun removeClothingItem(item: ClothingItem) {
        _clothingItems.value = _clothingItems.value - item
    }
}
