package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.persistence.ClothingItemEntity

enum class ClothingCategory(val categoryName: String) {
    TOPS("Tops"),
    BOTTOMS("Bottoms"),
    FOOTWEAR("Footwear"),
    ACCESSORIES("Accessories");

    companion object {
        fun fromString(value: String): ClothingCategory {
            return when (value.lowercase()) {
                "tops" -> TOPS
                "bottoms" -> BOTTOMS
                "footwear" -> FOOTWEAR
                "accessories" -> ACCESSORIES
                else -> throw IllegalArgumentException("Unknown category: $value")
            }
        }
    }
}

@Serializable
data class ClothingItem(
    val id: String = "",
    val name: String = "", // Name of the item (e.g., "Red Dress")
    val wardrobeId: String = "",
    val itemType: ClothingCategory,
    val mediaUrl: String? = "",
    val tags: List<String> = listOf(""),
    val createdAt: String = ""
)

fun ClothingItem.toEntity(): ClothingItemEntity {
    return ClothingItemEntity(
        id = this.id,
        wardrobeId = this.wardrobeId,
        itemType = this.itemType.name, // Assuming ClothingCategory is an enum, convert it to String
        mediaUrl = this.mediaUrl ?: "",
        tags = this.tags,
        condition = "", // Default or map from another property if available
        brand = "", // Default or map from another property if available
        size = "", // Default or map from another property if available
        createdAt = this.createdAt
    )
}

// Function to give dummy set of clothing to create a test outfit
fun generateSampleClothingItems(): List<ClothingItem> {
    return listOf(
        ClothingItem(
            id = "1",
            name = "TOPS",
            itemType = ClothingCategory.TOPS,
            mediaUrl = null
        ),
        ClothingItem(
            id = "2",
            name = "BOTTOMS",
            itemType = ClothingCategory.BOTTOMS,
            mediaUrl = null
        ),
        ClothingItem(
            id = "3",
            name = "FOOTWEAR",
            itemType = ClothingCategory.FOOTWEAR,
            mediaUrl = null
        ),
        ClothingItem(
            id = "4",
            name = "ACCESSORIES",
            itemType = ClothingCategory.ACCESSORIES,
            mediaUrl = null,
            tags = listOf("fashionable", "accessory")
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
            itemType = clothingCategory,
            mediaUrl = null
        )
    }

    return items
}

