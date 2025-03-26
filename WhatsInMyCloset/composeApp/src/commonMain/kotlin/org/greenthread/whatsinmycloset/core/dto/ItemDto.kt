package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem

@Serializable
data class ItemDto(
    val id: String,
    val name: String,
    val wardrobeId: String,
    val itemType: String,
    val mediaUrl: String,
    val tags: List<String>,
    val condition: String,
    val brand: String,
    val size: String,
    val createdAt: String
)

fun ItemDto.toClothingItem(): ClothingItem {
    return ClothingItem(
        id = this.id,
        name = this.brand + " " + this.itemType, // Use brand + itemType as name (modify as needed)
        wardrobeId = this.wardrobeId,
        itemType = ClothingCategory.valueOf(this.itemType.uppercase()), // Ensure case match for enum
        mediaUrl = this.mediaUrl.ifEmpty { null }, // Convert empty string to null
        tags = this.tags.ifEmpty { listOf() }, // Ensure empty list if tags are missing
        condition = this.condition,
        brand = this.brand,
        size = this.size,
        createdAt = this.createdAt
    )
}