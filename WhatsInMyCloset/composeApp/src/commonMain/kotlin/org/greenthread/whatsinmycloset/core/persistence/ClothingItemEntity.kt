package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = ClothingItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["wardrobeId"],
            onDelete = ForeignKey.CASCADE // Ensures items are deleted if the wardrobe is deleted
        )
    ],
    indices = [Index("wardrobeId")]
)

@Serializable
data class ClothingItemEntity(
    @PrimaryKey
    val id: String = "",
    val name: String, // Name of the item (e.g., "Red Dress")
    val wardrobeId: String = "",
    val itemType: ClothingCategory,
    val mediaUrl: String? = "",
    val tags: List<String> = listOf(""),
    val createdAt: String = ""
)

fun ClothingItemEntity.toItem(): ClothingItem {
    return ClothingItem(
        id = id,
        name = name, // Name of the item (e.g., "Red Dress")
        wardrobeId = wardrobeId,
        itemType = itemType,
        mediaUrl = mediaUrl,
        tags = tags,
        createdAt = createdAt
    )
}
