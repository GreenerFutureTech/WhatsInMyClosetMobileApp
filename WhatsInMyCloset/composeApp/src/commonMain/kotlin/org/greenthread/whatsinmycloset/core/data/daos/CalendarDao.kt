package org.greenthread.whatsinmycloset.core.data.daos

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.greenthread.whatsinmycloset.core.persistence.CalendarEntity
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity

@Dao
interface CalendarDao {
    // Insert a calendar entry (links an outfit to a specific date)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarEntry(entry: CalendarEntity)

    // from backend to room
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<CalendarEntity>)

    // Get all calendar entries for a user
    @Query("SELECT * FROM calendar WHERE userId = :userId")
    fun getCalendarEntries(userId: String): Flow<List<CalendarEntity>>

    // Get outfits scheduled for a specific date
    @Transaction
    @Query("""
        SELECT outfits.* FROM outfits
        INNER JOIN calendar ON outfits.id = calendar.outfitId
        WHERE calendar.date = :date AND calendar.userId = :userId
    """)
    fun getOutfitsForDate(userId: String, date: String): Flow<List<OutfitEntity>>

    // Get all scheduled outfits with their dates for a user
    @Transaction
    @Query("""
        SELECT outfits.*, calendar.date FROM outfits
        INNER JOIN calendar ON outfits.id = calendar.outfitId
        WHERE calendar.userId = :userId
        ORDER BY calendar.date
    """)
    fun getScheduledOutfits(userId: String): Flow<List<OutfitWithDate>>
}

// Helper class for joined results
data class OutfitWithDate(
    @Embedded val outfit: OutfitEntity,
    val date: String
)