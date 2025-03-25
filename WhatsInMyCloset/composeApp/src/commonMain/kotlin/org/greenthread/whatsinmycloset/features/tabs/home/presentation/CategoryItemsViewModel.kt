package org.greenthread.whatsinmycloset.features.tabs.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager

class CategoryItemsViewModel(
    private val wardrobeManager: WardrobeManager
) : ViewModel() {
    private val _categoryItems = MutableStateFlow<List<ClothingItem>>(emptyList())
    val categoryItems: StateFlow<List<ClothingItem>> = _categoryItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadItemsByCategory(category: ClothingCategory) {
        viewModelScope.launch {
            _isLoading.value = true

            wardrobeManager.cachedItems.collectLatest { allItems ->
                val filtered = allItems.filter { it.itemType == category }
                _categoryItems.value = filtered
                _isLoading.value = false
            }
        }
    }
}