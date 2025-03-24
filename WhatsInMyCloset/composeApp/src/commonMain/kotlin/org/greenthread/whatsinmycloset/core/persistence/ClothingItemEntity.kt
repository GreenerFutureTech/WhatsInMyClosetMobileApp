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
    val id: String,
    val wardrobeId: String,
    val itemType: String,
    val mediaUrl: String,
    val tags: List<String>,
    val condition: String,
    val brand: String,
    val size: String,
    val createdAt: String
)

fun ClothingItemEntity.toItem(): ClothingItem {
    return ClothingItem(
        id = this.id,
        name = "", // Name is not available in ClothingItemEntity, set a default or retrieve from elsewhere
        wardrobeId = this.wardrobeId,
        itemType = ClothingCategory.valueOf(this.itemType), // Convert string back to ClothingCategory enum
        mediaUrl = this.mediaUrl,
        tags = this.tags,
        position = null, // Position is not available in ClothingItemEntity
        createdAt = this.createdAt
    )
}
