package org.greenthread.whatsinmycloset.core.domain.models

enum class ClothingCategory {
    TOPS,
    BOTTOMS,
    FOOTWEAR,
    ACCESSORIES
}

data class ClothingItem(
    val id: String, // Unique identifier for the item
    val name: String, // Name of the item (e.g., "Red Dress")
    val category: ClothingCategory, // Category the item belongs to
    val pictureUrl: String? = null, // URL or path to the picture (nullable)
    val tags: Set<String>? = null // Tags for characteristics (e.g., "red", "fancy", "rainy")
)

// for testing
fun generateSampleClothingItems(): List<ClothingItem> {
    return listOf(
        ClothingItem(id = "1", name = "T-shirt", category = ClothingCategory.TOPS),
        ClothingItem(id = "2", name = "Jeans", category = ClothingCategory.BOTTOMS),
        ClothingItem(id = "3", name = "Sneakers", category = ClothingCategory.FOOTWEAR),
        ClothingItem(id = "4", name = "Hat", category = ClothingCategory.ACCESSORIES,
            tags = setOf("fashionable", "outdoor"))
    )
}

fun generateRandomClothingItems(category: String, numberOfItems: Int): List<ClothingItem> {
    val items = mutableListOf<ClothingItem>()

    // Create the items based on the category and the number requested
    repeat(numberOfItems) { index ->
        val itemName = when (category) {
            "Tops" -> "T-shirt ${index + 1}"
            "Bottoms" -> "Jeans ${index + 1}"
            "Footwear" -> "Sneakers ${index + 1}"
            "Accessories" -> "Hat ${index + 1}"
            else -> "Item ${index + 1}"
        }

        val tags = when (category) {
            "Tops" -> setOf("casual", "comfortable")
            "Bottoms" -> setOf("casual", "denim")
            "Footwear" -> setOf("sporty", "comfortable")
            "Accessories" -> setOf("fashionable", "outdoor")
            else -> emptySet()
        }

        // Generate unique item IDs
        val itemId = (index + 1).toString()

        // Assign category
        val clothingCategory = when (category) {
            "Tops" -> ClothingCategory.TOPS
            "Bottoms" -> ClothingCategory.BOTTOMS
            "Footwear" -> ClothingCategory.FOOTWEAR
            "Accessories" -> ClothingCategory.ACCESSORIES
            else -> ClothingCategory.TOPS // Default fallback
        }

        // Add the new ClothingItem to the list
        items.add(ClothingItem(itemId, itemName, clothingCategory, tags = tags))
    }

    return items
}
