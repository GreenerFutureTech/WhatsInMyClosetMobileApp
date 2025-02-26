package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.serialization.Serializable

enum class ClothingCategory(val categoryName: String) {
    TOPS("Tops"),
    BOTTOMS("Bottoms"),
    FOOTWEAR("Footwear"),
    ACCESSORIES("Accessories");

    companion object {
        fun fromString(name: String): ClothingCategory? {
            return values().find { it.categoryName.equals(name, ignoreCase = true) }
        }
    }
}

@Serializable
data class ClothingItem(
    val id: String, // Unique identifier for the item
    val name: String, // Name of the item (e.g., "Red Dress")
    val category: ClothingCategory, // Category the item belongs to
    // Image of the clothing item from resources folder -- Stores drawables resource ID
    val clothingImage: Unit? = null,
    val tags: Set<String>? = null // Tags for characteristics (e.g., "red", "fancy", "rainy")
)

// Function to give dummy set of clothing to create a test outfit
fun generateSampleClothingItems(): List<ClothingItem> {
    return listOf(
        ClothingItem(
            id = "1",
            name = "TOPS",
            category = ClothingCategory.TOPS,
            clothingImage = null
        ),
        ClothingItem(
            id = "2",
            name = "BOTTOMS",
            category = ClothingCategory.BOTTOMS,
            clothingImage = null
        ),
        ClothingItem(
            id = "3",
            name = "FOOTWEAR",
            category = ClothingCategory.FOOTWEAR,
            clothingImage = null
        ),
        ClothingItem(
            id = "4",
            name = "ACCESSORIES",
            category = ClothingCategory.ACCESSORIES,
            clothingImage = null,
            tags = setOf("fashionable", "accessory")
        )
    )
}


fun generateRandomClothingItems(category: String, numberOfItems: Int): List<ClothingItem> {
    val items = List(numberOfItems) { index ->
        val itemId = (index + 1).toString()

        val clothingCategory = try {
            ClothingCategory.valueOf(category.uppercase())
        } catch (e: IllegalArgumentException) {
            ClothingCategory.TOPS // Default fallback
        }

        val itemName = when (clothingCategory) {
            ClothingCategory.TOPS -> "T-shirt ${index + 1}"
            ClothingCategory.BOTTOMS -> "Jeans ${index + 1}"
            ClothingCategory.FOOTWEAR -> "Sneakers ${index + 1}"
            ClothingCategory.ACCESSORIES -> "Hat ${index + 1}"
        }

        val tags = when (clothingCategory) {
            ClothingCategory.TOPS -> setOf("casual", "comfortable")
            ClothingCategory.BOTTOMS -> setOf("casual", "denim")
            ClothingCategory.FOOTWEAR -> setOf("sporty", "comfortable")
            ClothingCategory.ACCESSORIES -> setOf("fashionable", "outdoor")
        }

        ClothingItem(
            id = itemId,
            name = itemName,
            category = clothingCategory,
            clothingImage = null, // Store null as placeholder
            tags = tags
        )
    }

    return items
}

