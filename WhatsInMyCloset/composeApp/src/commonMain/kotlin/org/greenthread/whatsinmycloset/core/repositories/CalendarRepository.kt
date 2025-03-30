package org.greenthread.whatsinmycloset.core.repositories

import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import org.greenthread.whatsinmycloset.core.data.daos.CalendarDao
import org.greenthread.whatsinmycloset.core.data.daos.OutfitDao
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.isSuccess
import org.greenthread.whatsinmycloset.core.domain.models.CalendarEntry
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.toCalendarEntry
import org.greenthread.whatsinmycloset.core.domain.models.toDomain
import org.greenthread.whatsinmycloset.core.domain.models.toEntity
import org.greenthread.whatsinmycloset.core.dto.CalendarDto
import org.greenthread.whatsinmycloset.core.dto.toCalendarEntry
import org.greenthread.whatsinmycloset.core.dto.toOutfit
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource
import org.greenthread.whatsinmycloset.core.persistence.CalendarEntity
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity


open class CalendarRepository(
    private val calendarDao: CalendarDao,
    private val outfitDao: OutfitDao,
    val remoteSource: KtorRemoteDataSource
) {

    suspend fun saveOutfitToCalendar(calendarEntry: CalendarEntry): Boolean {
        return try {
            // Convert to DTO matching server requirements
            val dto = CalendarDto(
                outfitId = calendarEntry.outfitId,  // same as in backend
                userId = calendarEntry.userId,
                date = calendarEntry.date
            )

            println("Calendar DTO: $dto")

            val remoteResult = remoteSource.postOutfitToCalendar(dto)

            println("Server response: $remoteResult")

            if (remoteResult.isSuccess())
            {
                // insert outfit in calendar table in room
                calendarDao.insertCalendarEntry(calendarEntry.toEntity())
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    // get outfit for a specific date user selected
    fun getOutfitForDate(userId: Int, date: LocalDate): Flow<Outfit?> {
        return calendarDao.getOutfitForDate(userId.toString(), date.toString())
            .map { it?.toDomain() }
            .onStart {
                if (!calendarDao.hasEntryForDate(userId.toString(), date.toString())) {
                    try {
                        syncCalendarData(userId)
                    } catch (e: Exception) {
                        logSyncError(userId, e)
                    }
                }
            }
    }

    @Transaction
    suspend fun syncCalendarData(userId: Int, force: Boolean = false) {
        try {
            remoteSource.getAllOutfitsFromCalendar(userId.toString())
                .getOrNull()
                ?.let { dtos ->
                    val outfits = dtos.map { it.toOutfit() }
                    val entries = dtos.map { it.toCalendarEntry() }

                    outfitDao.insertOrUpdateAll(outfits.map { it.toEntity() })
                    calendarDao.insertAll(entries.map { it.toEntity() })
                }
        } catch (e: Exception) {
            logSyncError(userId, e)
            throw e
        }
    }


    private fun logSyncError(userId: Int, e: Exception) {
        println("""
            Sync failed for user $userId
            Error: ${e.message}
            Stacktrace: ${e.stackTraceToString()}
        """.trimIndent())
    }

}