package org.greenthread.whatsinmycloset.core.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity

@Dao
interface OutfitDao {
    // Insert a single outfit
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: OutfitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(outfits: List<OutfitEntity>)

    // Insert multiple outfits
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfits(outfits: List<OutfitEntity>)

    // Delete an outfit by ID
    @Query("DELETE FROM outfits WHERE id = :outfitId")
    suspend fun deleteOutfit(outfitId: String)

    // Fetch all outfits
    @Query("SELECT * FROM outfits WHERE userId = :userId")
    fun getOutfits(userId: Int): Flow<List<OutfitEntity>>

    // Fetch outfits by user ID
    @Query("SELECT * FROM outfits WHERE userId = :userId")
    fun getOutfitsByUserId(userId: Int): Flow<List<OutfitEntity>>

    // Fetch outfits by user ID and name
    @Query("SELECT * FROM outfits WHERE userId = :userId AND name LIKE :name")
    fun getOutfitsByName(userId: Int, name: String): Flow<List<OutfitEntity>>

    // Fetch a single outfit by ID
    @Query("SELECT * FROM outfits WHERE id = :outfitId LIMIT 1")
    suspend fun getOutfitById(outfitId: String): OutfitEntity?

    // Search outfits by name (case-insensitive)
    @Query("SELECT * FROM outfits WHERE name LIKE '%' || :query || '%'")
    fun searchOutfits(query: String): Flow<List<OutfitEntity>>
}