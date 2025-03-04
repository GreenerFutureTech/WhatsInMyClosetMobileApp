package org.greenthread.whatsinmycloset.core.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.greenthread.whatsinmycloset.core.persistence.ItemEntity

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemEntity)

    @Query("SELECT * FROM items WHERE wardrobeId = :wardrobeId")
    suspend fun getItemsForWardrobe(wardrobeId: String): List<ItemEntity>

    @Delete
    suspend fun delete(item: ItemEntity)
}