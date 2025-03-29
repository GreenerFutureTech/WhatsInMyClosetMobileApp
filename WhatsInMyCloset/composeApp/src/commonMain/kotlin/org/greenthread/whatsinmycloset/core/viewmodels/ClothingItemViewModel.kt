package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.data.daos.ClothingItemDao
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.persistence.toClothingItem
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

/*
* ClothingItemViewModel is responsible for managing the state and logic related to clothing items,
* including fetching items for a specific wardrobe and category.
* */
open class ClothingItemViewModel(
    private val wardrobeManager: WardrobeManager,
    private val itemDao: ClothingItemDao // Inject ItemDao to fetch items
    ) :
ViewModel() {

    val cachedWardrobes: StateFlow<List<Wardrobe>> = wardrobeManager.cachedWardrobes
    private val cachedItems: StateFlow<List<ClothingItem>> = wardrobeManager.cachedItems

    // Filter states
    private val _selectedWardrobe = MutableStateFlow<Wardrobe?>(null)
    val selectedWardrobe: StateFlow<Wardrobe?> = _selectedWardrobe.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ClothingCategory?>(null)
    val selectedCategory: StateFlow<ClothingCategory?> = _selectedCategory.asStateFlow()

    // Filtered results
    private val _filteredItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val filteredItems: StateFlow<List<ClothingItem>> = _filteredItems.asStateFlow()

    private val _selectedItems = MutableStateFlow<List<ClothingItem>>(emptyList())

    // all selected items that from the selected category
    val selectedItems: StateFlow<List<ClothingItem>> = _selectedItems.asStateFlow()

    //private val _categoryItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    //val categoryItems: StateFlow<List<ClothingItem>> = _categoryItems.asStateFlow()

    init {
        // Initialize with first wardrobe
        viewModelScope.launch {
            cachedWardrobes.collect { wardrobes ->
                if (wardrobes.isNotEmpty() && _selectedWardrobe.value == null) {
                    _selectedWardrobe.value = wardrobes.first()
                }
                applyFilters()
            }
        }

        // React to item changes
        viewModelScope.launch {
            cachedItems.collect {
                applyFilters()
            }
        }
    }

    private fun applyFilters() {
        _filteredItems.value = cachedItems.value
            .filter { item ->
                _selectedCategory.value?.let { category ->
                    item.itemType == category
                } ?: true
            }
            .filter { item ->
                _selectedWardrobe.value?.let { wardrobe ->
                    item.wardrobeId == wardrobe.id
                } ?: true
            }
    }

    // Public API
    fun setCategoryFilter(category: ClothingCategory?) {
        _selectedCategory.value = category
        applyFilters()
    }

    fun setWardrobeFilter(wardrobe: Wardrobe?) {
        _selectedWardrobe.value = wardrobe
        applyFilters()
    }

    fun addSelectedItems(items: List<ClothingItem>) {
        _selectedItems.value = (_selectedItems.value + items).distinctBy { it.id }
    }

    fun clearClothingItemState() {
        _selectedItems.value = emptyList()
    }

    // For detailed item view
    suspend fun getItemDetail(itemId: String): ClothingItem? {
        return cachedItems.value.find { it.id == itemId }
    }

}