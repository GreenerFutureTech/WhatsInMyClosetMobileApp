package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ClothingItem(
    val id: String = "",
    val name: String, // Name of the item (e.g., "Red Dress")
    val wardrobeId: String = "",
    val itemType: String = "",
    val mediaUrl: String? = "",
    val tags: List<String> = listOf(""),
    val createdAt: String = ""
)

// for testing
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

        // Add the new ClothingItem to the list
        items.add(ClothingItem(itemId, itemName))
    }

    return items
}
