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

    // Fetch the details of a specific clothing item by its ID, wardrobeId, and category
    suspend fun getItemDetail(wardrobeId: String, itemId: String, category: ClothingCategory): ClothingItem? {
        println("DEBUG, Searching for item: id=$itemId, category=$category, wardrobeId=$wardrobeId")

        // Fetch the item from the database using ItemDao
        val itemEntity = itemDao.getItemById(itemId)

        // If the item exists and matches the wardrobeId and category, convert it to ClothingItem
        return if (itemEntity != null && itemEntity.wardrobeId == wardrobeId && itemEntity.itemType == category.toString()) {
            itemEntity.toClothingItem()
        } else {
            null
        }
    }

    // get selected item details - for testing
    fun getItemDetailTest(itemId: String): ClothingItem? {
        println("DEBUG, Searching for item: id=$itemId")

        // Get the current list of categoryItems
        val items = _categoryItems.value

        // Find the item with the matching itemId
        return items.find { it.id == itemId }
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
    // Clear selected items
    fun clearClothingItemState() {
        _selectedItems.value = emptyList()
    }

    // TODO to replace with proper functionality
    fun fetchSampleClothingItems() {
        viewModelScope.launch {
            val sampleItems = listOf(
                ClothingItem(
                    id = "1",
                    name = "Test Shirt",
                    mediaUrl = "https://greenthreaditems.blob.core.windows.net/images/test_shirt.png",
                    itemType = ClothingCategory.TOPS
                ),
                ClothingItem(
                    id = "2",
                    name = "Test Pants",
                    mediaUrl = "https://greenthreaditems.blob.core.windows.net/images/test_pants.png",
                    itemType = ClothingCategory.BOTTOMS
                ),
                ClothingItem(
                    id = "3",
                    name = "Test Hat",
                    mediaUrl = "https://greenthreaditems.blob.core.windows.net/images/test_hat.png",
                    itemType = ClothingCategory.ACCESSORIES
                )
            )
            _categoryItems.value = sampleItems
        }
    }

}