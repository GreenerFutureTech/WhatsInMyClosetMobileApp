package org.greenthread.whatsinmycloset.core.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity

@Dao
interface WardrobeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wardrobe: WardrobeEntity)

    @Query("SELECT * FROM wardrobe WHERE id = :id")
    suspend fun getWardrobe(id: String): WardrobeEntity?

    @Delete
    suspend fun delete(wardrobe: WardrobeEntity)
}