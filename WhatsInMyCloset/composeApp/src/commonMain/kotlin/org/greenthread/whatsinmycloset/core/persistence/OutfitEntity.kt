package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.greenthread.whatsinmycloset.core.data.daos.ItemDao
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData

/* Represents the persistence model for outfits in the Room database.
    Maps between the domain model (Outfit) and the database entity.
*/

@Entity(
    tableName = "outfits",
    indices = [Index("outfitId")]
)
// TODO match with ERD
data class OutfitEntity(
    @PrimaryKey val outfitId: String, // Matches backend UUID
    val userId: Int? = null,    // matches with User.kt
    val public: Boolean,
    val favorite: Boolean,
    val mediaURL: String = "",
    val name: String = "", // Name of the outfit (e.g., "Summer Look")
    val tags: List<String>? = null,
    val createdAt: String = ""
)

suspend fun OutfitEntity.toOutfit(itemDao: ItemDao): Outfit {
    val itemEntities = itemDao.getItemsForOutfit(outfitId) // fetches the associated items using the join table

    val itemPositionsList = itemDao.getItemPositionsForOutfit(outfitId) // Fetch positions as List<ItemPosition>

    // Convert List<ItemPosition> to Map<String, OffsetData>
    val itemPositions = itemPositionsList.associate { it.itemId to it.position }

    return Outfit(
        id = outfitId,
        userId = userId,
        name = name,
        public = public,
        itemIds = itemEntities.map { it.id },
        itemPositions = itemPositions,
        favorite = favorite,
        tags = tags
    )
}