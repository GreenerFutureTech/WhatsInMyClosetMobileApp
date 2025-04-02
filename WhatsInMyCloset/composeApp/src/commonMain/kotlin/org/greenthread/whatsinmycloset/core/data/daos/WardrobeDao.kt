package org.greenthread.whatsinmycloset.core.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity

@Dao
interface WardrobeDao {
    @Upsert
    suspend fun insertWardrobe(wardrobe: WardrobeEntity)

    @Query("""
        SELECT * FROM wardrobe 
        WHERE json_extract(user_data, '$.id') = :userId
    """)
    fun getWardrobes(userId: Int): Flow<List<WardrobeEntity>>

    @Delete
    suspend fun deleteWardrobe(wardrobe: WardrobeEntity)
}