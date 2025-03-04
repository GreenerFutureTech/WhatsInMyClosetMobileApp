package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem

// used to manage clothing items selected when creating an outfit
@Serializable
open class ClothingItemViewModel : ViewModel() {

    private val _clothingItems = MutableStateFlow<List<ClothingItem>>(emptyList())

    // all items that will be displayed for the selected category
    val clothingItems: StateFlow<List<ClothingItem>> = _clothingItems.asStateFlow()

    private val _selectedItems = MutableStateFlow<List<ClothingItem>>(emptyList())

    // all selected items that from the selected category
    val selectedItems: StateFlow<List<ClothingItem>> = _selectedItems.asStateFlow()

    fun addSelectedItems(items: List<ClothingItem>) {
        _selectedItems.value = (_selectedItems.value + items).distinctBy { it.id to it.itemType }
    }

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
    // this info will come from DB eventually
    fun getClothingItemDetails(itemId: String,  category: ClothingCategory): ClothingItem? {

        println("DEBUG, Searching for item: id=$itemId, category=$category")
        println("DEBUG, Current items: ${_clothingItems.value}")

        // get the item from the all the items shown on screen
        return _clothingItems.value.find { it.id == itemId && it.itemType == category }
    }

    private val _categoryItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val categoryItems: StateFlow<List<ClothingItem>> = _categoryItems.asStateFlow()

    // this info will come from DB eventually
    fun getItemsByCategory(category: String)
    {
        println("DEBUG: Fetching items for category: '$category'")
        println("DEBUG: Current clothing items (${_clothingItems.value.size} items): ${_clothingItems.value}")

        viewModelScope.launch {
            clothingItems.collectLatest { items ->
                // Perform filtering
                val filteredItems = items.filter {
                    val itemCategory = it.itemType.toString().trim().lowercase()
                    println("DEBUG: itemCategory = '$itemCategory' with search = '$category'")
                    itemCategory == category.trim().lowercase()
                }

                // Update the filteredStateFlow
                _categoryItems.value = filteredItems
                println("DEBUG: Filtered items count = ${filteredItems.size}")
            }
        }
    }

    // Function to initialize clothing items for testing routing
    fun initializeClothingItems(newItems: List<ClothingItem>) {
        _clothingItems.value = _clothingItems.value + newItems
    }

}