package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.persistence.ClothingItemEntity

enum class ClothingCategory(val categoryName: String) {
    TOPS("Tops"),
    BOTTOMS("Bottoms"),
    FOOTWEAR("Footwear"),
    ACCESSORIES("Accessories"),
    ALL("All");

    companion object {
        fun fromString(value: String): ClothingCategory {
            return when (value.lowercase()) {
                "tops" -> TOPS
                "bottoms" -> BOTTOMS
                "footwear" -> FOOTWEAR
                "accessories" -> ACCESSORIES
                "all" -> ALL
                else -> throw IllegalArgumentException("Unknown category: $value")
            }
        }
    }
}

@Serializable
data class ClothingItem(
    val id: String = "",
    val name: String = "",
    val wardrobeId: String = "",
    val itemType: ClothingCategory,
    val mediaUrl: String? = "",
    val tags: List<String> = listOf(""),
    val condition: String? = "",
    val brand: String? = "",
    val size: String? = "",
    val createdAt: String = ""
)

fun ClothingItem.toEntity(): ClothingItemEntity {
    return ClothingItemEntity(
        id = this.id,
        wardrobeId = this.wardrobeId,
        itemType = this.itemType.name, // Assuming ClothingCategory is an enum, convert it to String
        mediaUrl = this.mediaUrl ?: "",
        tags = this.tags,
        condition = this.condition ?:"", // Default or map from another property if available
        brand = this.brand ?: "", // Default or map from another property if available
        size = this.size ?: "", // Default or map from another property if available
        createdAt = this.createdAt
    )
}