package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.data.daos.ItemDao
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity
import org.greenthread.whatsinmycloset.core.persistence.toClothingItem
import org.greenthread.whatsinmycloset.core.repositories.WardrobeRepository
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

/*
* ClothingItemViewModel is responsible for managing the state and logic related to clothing items,
* including fetching items for a specific wardrobe and category.
* */
open class ClothingItemViewModel(
    private val wardrobeRepository: WardrobeRepository,
    wardrobeManager: WardrobeManager,
    private val itemDao: ItemDao // Inject ItemDao to fetch items
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

    private val _categoryItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val categoryItems: StateFlow<List<ClothingItem>> = _categoryItems.asStateFlow()

    // Fetch items for a specific wardrobe and category
    fun getItemsByCategoryAndWardrobe(category: String, wardrobe: Wardrobe?) {
        viewModelScope.launch {
            val selectedWardrobe = wardrobe ?: defaultWardrobe ?: return@launch
            val itemEntities = itemDao.getItemsForWardrobeAndCategory(selectedWardrobe.id, category)

            // Convert ItemEntity to ClothingItem
            val clothingItems = itemEntities.map { it.toClothingItem() }

            // Update the state
            _categoryItems.value = clothingItems
        }
    }

    fun addSelectedItems(items: List<ClothingItem>) {
        _selectedItems.value = (_selectedItems.value + items).distinctBy { it.id to it.itemType }
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

    // Function to initialize clothing items for each wardrobe
    fun initializeClothingItems(newItems: List<ClothingItem>, wardrobeId: String) {
        val updatedItems = newItems.map { it.copy(wardrobeId = wardrobeId) } // Associate items with the wardrobe
        _clothingItems.value = _clothingItems.value + updatedItems
    }

    // Clear selected items
    fun clearClothingItemState() {
        _clothingItems.value = emptyList()
        _selectedItems.value = emptyList()
    }

}