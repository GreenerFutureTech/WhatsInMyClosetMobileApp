package org.greenthread.whatsinmycloset.core.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.greenthread.whatsinmycloset.core.persistence.ClothingItemEntity
import org.greenthread.whatsinmycloset.core.persistence.ItemEntity
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity

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
    @Insert(onConflict = OnConflictStrategy.REPLACE)    // to avoid outfit id conflict
    suspend fun insertOutfit(outfit: OutfitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(outfits: List<OutfitEntity>)

    // Delete an outfit
    @Query("DELETE FROM outfits WHERE outfitId = :outfitId")
    suspend fun deleteOutfit(outfitId: String)

    // Fetch all outfits for a specific user
    @Query("SELECT * FROM outfits WHERE creatorId = :creatorId")
    fun getOutfitsByUserId(creatorId: Int): Flow<List<OutfitEntity>>

    // Fetch all outfits for a specific user by name
    @Query("SELECT * FROM outfits WHERE creatorId = :creatorId AND name = :name")
    fun getOutfitsByOutfitName(creatorId: Int, name: String): Flow<List<OutfitEntity>>
}