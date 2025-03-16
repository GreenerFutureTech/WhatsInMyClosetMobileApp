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

    /*  This query joins the items table with the outfit_item_join table
        to fetch all ItemEntity objects associated with the given outfitId
    * */
    @Query("""
        SELECT items.* FROM items
        INNER JOIN outfit_item_join ON items.id = outfit_item_join.itemId
        WHERE outfit_item_join.outfitId = :outfitId
    """)
    suspend fun getItemsForOutfit(outfitId: String): List<ItemEntity>   // to dynamically get items associated with the outfit

    @Query("SELECT * FROM items WHERE wardrobeId = :wardrobeId AND itemType = :category")
    suspend fun getItemsForWardrobeAndCategory(wardrobeId: String, category: String): List<ItemEntity>  // to display items to user (per category)
    // when they are creating an outfit

    @Delete
    suspend fun delete(item: ItemEntity)
}