package org.greenthread.whatsinmycloset.core.viewmodels

import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem

class MockClothingItemViewModel : ClothingItemViewModel() {
    init {
        // Add some mock data for the preview
        addClothingItems(
            listOf(
                ClothingItem(id = "1", name = "Top 1", category = ClothingCategory.TOPS),
                ClothingItem(id = "2", name = "Bottom 1", category = ClothingCategory.BOTTOMS),
                ClothingItem(id = "3", name = "Shoes 1", category = ClothingCategory.FOOTWEAR)
            )
        )
    }
}