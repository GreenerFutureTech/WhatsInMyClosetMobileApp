package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem

// used to manage clothing items selected when creating an outfit
@Serializable
open class ClothingItemViewModel : ViewModel() {

    private val _clothingItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val clothingItems: StateFlow<List<ClothingItem>> = _clothingItems.asStateFlow()

    fun addClothingItems(items: List<ClothingItem>) {
        _clothingItems.value = _clothingItems.value + items
    }

    fun removeClothingItem(item: ClothingItem) {
        _clothingItems.value = _clothingItems.value - item
    }

    // clear all selected items
    fun clearClothingItems() {
        _clothingItems.value = emptyList()
    }

    // Fetch the details of a specific clothing item by its ID
    fun getClothingItemDetails(itemId: String,  category: ClothingCategory): ClothingItem? {

        println("DEBUG, Searching for item: id=$itemId, category=$category")
        println("DEBUG, Current items: ${_clothingItems.value}")

        return _clothingItems.value.find { it.id == itemId && it.category == category }
    }

    // Function to initialize clothing items for testing routing
    fun initializeClothingItems(items: List<ClothingItem>) {

        println("DEBUG, Initializing items: $items")
        _clothingItems.value = items
    }
}