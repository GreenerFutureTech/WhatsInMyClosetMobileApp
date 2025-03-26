package org.greenthread.whatsinmycloset.core.data.daos

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Junction
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.greenthread.whatsinmycloset.core.persistence.ClothingItemEntity
import org.greenthread.whatsinmycloset.core.persistence.ItemEntity
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity
import org.greenthread.whatsinmycloset.core.persistence.OutfitItemJoin

/* For Room database operations
*   Handles database operations for OutfitEntity
*   and its relationships with ItemEntity using a join table (OutfitItemJoin).
*
*   Provides methods to insert outfits, items, and their relationships,
*   as well as fetch outfits with their associated items.
*
* */
@Dao
interface OutfitDao {
    // Insert an outfit
    @Insert
    suspend fun insertOutfit(outfit: OutfitEntity)

    // Insert a relationship between an outfit and a clothing item
    @Insert
    suspend fun insertOutfitItemJoin(join: OutfitItemJoin)

    // Fetch all outfits with their associated clothing items
    @Transaction
    @Query("SELECT * FROM outfits WHERE outfitId = :outfitId")
    suspend fun getOutfitWithItems(outfitId: String): OutfitWithItems?

    // Delete an outfit
    @Query("DELETE FROM outfits WHERE outfitId = :outfitId")
    suspend fun deleteOutfit(outfitId: String)

    // Fetch all outfits for a specific user
    @Query("SELECT * FROM outfits WHERE userId = :userId")
    fun getOutfits(userId: Int): Flow<List<OutfitEntity>>
}

// Data class to represent an outfit with its associated clothing items
data class OutfitWithItems(
    @Embedded val outfit: OutfitEntity,
    @Relation(
        parentColumn = "outfitId", // Refers to the outfitId column in OutfitEntity
        entityColumn = "id",       // Refers to the id column in ItemEntity
        associateBy = Junction(
            value = OutfitItemJoin::class,
            parentColumn = "outfitId", // Refers to the outfitId column in OutfitItemJoin
            entityColumn = "itemId"    // Refers to the itemId column in OutfitItemJoin
        )
    )
    val items: List<ClothingItemEntity>
)