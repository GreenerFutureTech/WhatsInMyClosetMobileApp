package org.greenthread.whatsinmycloset.core.persistence

/*
* Outfits and clothing items typically have a many-to-many relationship:
    One outfit can include multiple clothing items.
    One clothing item can belong to multiple outfits.
*
* ItemEntity is an independent entity that can exist outside of outfits.
    Using a join table allows:
    Query and update clothing items (ItemEntity) independently.
    Associate the same clothing item with multiple outfits.

    Storing a list of ItemEntity directly in OutfitEntity can lead to performance issues as the dataset grows.
        Hence, a join table allows for efficient querying and indexing
* */
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData

@Entity(
    tableName = "outfit_item_join",
    primaryKeys = ["outfitId", "itemId"],
    foreignKeys = [
        ForeignKey(
            entity = OutfitEntity::class,
            parentColumns = ["outfitId"],
            childColumns = ["outfitId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("outfitId"), Index("itemId")]
)
data class OutfitItemJoin(
    val outfitId: String, // Foreign key to OutfitEntity
    val itemId: String,   // Foreign key to ItemEntity
    val position: OffsetData // Position of the item in the outfit
)

data class ItemPosition(
    val itemId: String,
    val position: OffsetData
)
