package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem


class OutfitViewModel(savedStateHandle: SavedStateHandle? = null) : ViewModel() {
    private val selectedCategoryKey = "selected_category"
    private val selectedItemsKey = "selected_items"

    private val _clothingItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val clothingItems: StateFlow<List<ClothingItem>> = _clothingItems.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String>("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _categoryItems = MutableStateFlow<List<ClothingCategory>>(emptyList())
    val categoryItems: StateFlow<List<ClothingCategory>> = _categoryItems.asStateFlow()

    private val _calendarEvents = MutableStateFlow<List<String>>(emptyList())
    val calendarEvents: StateFlow<List<String>> = _calendarEvents.asStateFlow()

    init {
        savedStateHandle?.let { loadState(it) } // Only call if non-null
    }

    // Save state
    fun saveState(savedStateHandle: SavedStateHandle) {
        savedStateHandle[selectedCategoryKey] = _selectedCategory.value
        savedStateHandle[selectedItemsKey] = _clothingItems.value
    }

    // Load state
    private fun loadState(savedStateHandle: SavedStateHandle) {
        _selectedCategory.value = savedStateHandle.get<String>(selectedCategoryKey) ?: ""
        _clothingItems.value = savedStateHandle.get<List<ClothingItem>>(selectedItemsKey) ?: emptyList()
    }

    // Function to update clothing items (instead of accessing private properties)
    fun updateClothingItems(items: List<ClothingItem>) {
        _clothingItems.value = items
    }

    // Function to update category (instead of accessing private properties)
    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSelectedItems(items: List<ClothingItem>) {
        _clothingItems.value = items
    }

    fun saveOutfit(Outfit: String) {

    }

    fun addToCalendar(selectedDate: String) {

    }

    fun selectCategory(category: String) {

    }
}

