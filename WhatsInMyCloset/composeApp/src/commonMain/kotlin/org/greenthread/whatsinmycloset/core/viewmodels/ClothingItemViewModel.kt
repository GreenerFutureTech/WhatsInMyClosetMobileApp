package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity
import org.greenthread.whatsinmycloset.core.repositories.WardrobeRepository
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

// used to manage clothing items selected when creating an outfit
open class ClothingItemViewModel(
    private val wardrobeRepository: WardrobeRepository,
    private val wardrobeManager: WardrobeManager
    ) :
ViewModel() {

    var defaultWardrobe: Wardrobe? = wardrobeManager.cachedWardrobes.firstOrNull()

    private val _wardrobes = MutableStateFlow<List<Wardrobe>>(emptyList())
    val wardrobes: StateFlow<List<Wardrobe>> = _wardrobes.asStateFlow()

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
    fun getClothingItemDetails
                (wardrobeId: String, itemId: String, category: ClothingCategory)
    : ClothingItem?
    {
        println("DEBUG, Searching for item: id=$itemId, category=$category, wardrobeId=$wardrobeId")
        println("DEBUG, Current items: ${_clothingItems.value}")

        // Get the item from all items that match the ID, category, and wardrobe ID
        return _clothingItems.value.find { item ->
            item.id == itemId && item.itemType == category && item.wardrobeId == wardrobeId
        }
    }

    private val _categoryItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val categoryItems: StateFlow<List<ClothingItem>> = _categoryItems.asStateFlow()

    val sampleWardrobes = listOf(
        WardrobeEntity(
            id = "123",
            wardrobeName = "Summer Closet",
            createdAt = "2025-03-01",
            lastUpdate = "2025-03-07",
            userId = "user1"),

        WardrobeEntity(
            id = "2",
            wardrobeName = "Winter Closet",
            createdAt = "2025-03-07",
            lastUpdate = "2025-03-07",
            userId = "user1"),

        WardrobeEntity(
            id = "3",
            wardrobeName = "Fall Closet",
            createdAt = "2025-03-04",
            lastUpdate = "2025-03-07",
            userId = "user1")
    )

    // Insert sample wardrobes
    init {
        viewModelScope.launch {
            for (wardrobe in sampleWardrobes) {
                wardrobeRepository.insertWardrobe(wardrobe)
            }

            val fetchedWardrobes = wardrobeRepository.getWardrobes().firstOrNull() ?: emptyList()
            _wardrobes.value = fetchedWardrobes

            if (fetchedWardrobes.isNotEmpty()) {
                defaultWardrobe = fetchedWardrobes.first()
            }
        }
    }

    // Fetch items by category and filter them by the selected wardrobe
    fun getItemsByCategoryAndWardrobe(category: String, wardrobe: Wardrobe?) {
        println("DEBUG: Fetching items for category: '$category' and wardrobe: '${wardrobe?.id}'")

        viewModelScope.launch {
            val selectedWardrobe = wardrobe ?: defaultWardrobe ?: return@launch

            try {
                clothingItems.collectLatest { items ->
                    val filteredItems = items.filter {
                        val itemCategory = it.itemType.toString().trim().lowercase()
                        val belongsToWardrobe = selectedWardrobe.id == it.wardrobeId

                        println("DEBUG: itemCategory = '$itemCategory', wardrobe = '${it.wardrobeId}'")
                        itemCategory == category.trim().lowercase() && belongsToWardrobe
                    }

                    _categoryItems.value = filteredItems
                    println("DEBUG: Filtered items count = ${filteredItems.size}")
                }
            } catch (e: Exception) {
                println("ERROR: ${e.message.toString()}")
            }
        }
    }

    // Function to initialize clothing items for each wardrobe
    fun initializeClothingItems(newItems: List<ClothingItem>, wardrobeId: String) {
        val updatedItems = newItems.map { it.copy(wardrobeId = wardrobeId) } // Associate items with the wardrobe
        _clothingItems.value = _clothingItems.value + updatedItems
    }

    open fun clearClothingItemState() {
        println("DEBUG: Clearing clothing item state")
        _clothingItems.value = emptyList() // Clear selected clothing items
        _selectedItems.value = emptyList()
    }


}