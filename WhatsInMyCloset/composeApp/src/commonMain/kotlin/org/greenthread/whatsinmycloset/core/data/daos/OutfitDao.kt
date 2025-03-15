package org.greenthread.whatsinmycloset.core.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity

/* For Room database operations */
@Dao
interface OutfitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(outfit: OutfitEntity)

    @Query("SELECT * FROM outfits WHERE userId = :user123")
    suspend fun getOutfits(userId: String): List<OutfitEntity>

    @Delete
    suspend fun delete(outfit: OutfitEntity)
}