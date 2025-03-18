package org.greenthread.whatsinmycloset.core.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.greenthread.whatsinmycloset.core.persistence.ClothingItemEntity

@Dao
interface ClothingItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ClothingItemEntity)

    @Query("SELECT * FROM items WHERE wardrobeId = :wardrobeId")
    suspend fun getItemsForWardrobe(wardrobeId: String): List<ClothingItemEntity>

    @Query("SELECT * FROM items")
    fun getItems(): Flow<List<ClothingItemEntity>>

    @Delete
    suspend fun delete(item: ClothingItemEntity)
}