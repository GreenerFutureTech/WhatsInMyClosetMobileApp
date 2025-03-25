package org.greenthread.whatsinmycloset.core.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.greenthread.whatsinmycloset.core.persistence.ClothingItemEntity
import org.greenthread.whatsinmycloset.core.persistence.ItemPosition

@Dao
interface ClothingItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ClothingItemEntity)

    @Query("SELECT * FROM items WHERE wardrobeId = :wardrobeId")
    suspend fun getItemsForWardrobe(wardrobeId: String): List<ClothingItemEntity>

    /*  This query joins the items table with the outfit_item_join table
    to fetch all ItemEntity objects associated with the given outfitId
* */
    @Query("""
        SELECT items.* FROM items
        INNER JOIN outfit_item_join ON items.id = outfit_item_join.itemId
        WHERE outfit_item_join.outfitId = :outfitId
    """)
    suspend fun getItemsForOutfit(outfitId: String): List<ClothingItemEntity>   // to dynamically get items associated with the outfit

    @Query("SELECT * FROM items WHERE wardrobeId = :wardrobeId AND itemType = :category")
    suspend fun getItemsForWardrobeAndCategory(wardrobeId: String, category: String): List<ClothingItemEntity>  // to display items to user (per category)
    // when they are creating an outfit

    @Query("SELECT * FROM items")
    fun getItems(): Flow<List<ClothingItemEntity>>

    @Delete
    suspend fun delete(item: ClothingItemEntity)

    // Function to fetch item positions for an outfit
    @Query("""
        SELECT itemId, position FROM outfit_item_join
        WHERE outfitId = :outfitId
    """)
    suspend fun getItemPositionsForOutfit(outfitId: String): List<ItemPosition>

    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun getItemById(itemId: String): ClothingItemEntity?
}