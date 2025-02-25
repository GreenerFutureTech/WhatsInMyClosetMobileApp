package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem

// used to manage clothing items selected when creating an outfit
open class ClothingItemViewModel : ViewModel() {
    private val _clothingItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val clothingItems: StateFlow<List<ClothingItem>> = _clothingItems

    fun addClothingItem(item: ClothingItem) {
        _clothingItems.value = _clothingItems.value + item
    }

    fun addClothingItems(items: List<ClothingItem>) {
        _clothingItems.value = _clothingItems.value + items
    }

    fun removeClothingItem(item: ClothingItem) {
        _clothingItems.value = _clothingItems.value - item
    }
}