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
    // Insert an outfit to a date in calendar
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarEntry(entry: CalendarEntity)

    // this will get all outfits for current user from backend and insert into room
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<CalendarEntity>)

    // Get all calendar entries for a user
    @Query("SELECT * FROM calendar WHERE userId = :userId")
    fun getCalendarEntries(userId: String): Flow<List<CalendarEntity>>

    // Get outfits scheduled for a specific date
    @Transaction
    @Query("""
        SELECT outfits.* FROM outfits
        INNER JOIN calendar ON outfits.outfitId = calendar.outfitId
        WHERE calendar.date = :date AND calendar.userId = :userId
    """)
    fun getOutfitForDate(userId: String, date: String): Flow<List<OutfitEntity>>
}