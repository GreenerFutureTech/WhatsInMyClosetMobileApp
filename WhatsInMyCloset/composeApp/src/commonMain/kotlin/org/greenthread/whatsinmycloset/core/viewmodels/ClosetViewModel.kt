package org.greenthread.whatsinmycloset.core.viewmodels

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.generateSampleClothingItems

class ClosetViewModel : ViewModel() {
    // Private mutable StateFlow for managing clothing items
    private val _clothingItems = MutableStateFlow<List<ClothingItem>>(emptyList())

    // Public immutable StateFlow to observe clothing items
    val clothingItems: StateFlow<List<ClothingItem>> = _clothingItems.asStateFlow()

    // Filter clothing items by category
    @Composable
    fun filterItems(category: String) {
        val categoryEnum = try {
            enumValueOf<ClothingCategory>(category)
        } catch (e: IllegalArgumentException) {
            null
        }

        if (categoryEnum != null) {
            _clothingItems.update { items -> items.filter { it.category == categoryEnum } }
        }
    }

}


