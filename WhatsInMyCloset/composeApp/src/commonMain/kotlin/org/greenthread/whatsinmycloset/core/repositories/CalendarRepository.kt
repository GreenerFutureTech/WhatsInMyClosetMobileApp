package org.greenthread.whatsinmycloset.core.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import org.greenthread.whatsinmycloset.core.data.daos.CalendarDao
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.isSuccess
import org.greenthread.whatsinmycloset.core.domain.models.CalendarEntry
import org.greenthread.whatsinmycloset.core.domain.models.toCalendarEntry
import org.greenthread.whatsinmycloset.core.domain.models.toEntity
import org.greenthread.whatsinmycloset.core.dto.CalendarDto
import org.greenthread.whatsinmycloset.core.dto.toCalendarEntry
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource
import org.greenthread.whatsinmycloset.core.persistence.CalendarEntity
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity


open class CalendarRepository(
    private val calendarDao: CalendarDao,
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

    // get all outfits for the logged in user
    open fun getOutfitsFromCalendar(userId: Int): Flow<List<CalendarEntry>> {
        return calendarDao.getCalendarEntries(userId.toString())
            .map { entities -> entities.map { entity -> entity.toCalendarEntry() } }
            .onStart {
                try {
                    remoteSource.getAllOutfitsFromCalendar(userId.toString())
                        .getOrNull()
                        ?.let { dtos ->
                            val entries = dtos.map { dto -> dto.toCalendarEntry() }
                            calendarDao.insertAll(entries.map { entry -> entry.toEntity() })
                        }
                } catch (e: Exception) {
                    println("Sync failed: ${e.message}")
                }
            }
    }

    // get outfit for a specific date user selected
    open fun getOutfitForDate(userId: Int, date: LocalDate): Flow<OutfitEntity?> {
        return calendarDao.getOutfitForDate(userId.toString(), date.toString())
            .map { results -> results.firstOrNull() } // Get first outfit or null
            .onStart {
                try {
                    // Sync from remote if needed
                    remoteSource.getAllOutfitsFromCalendar(userId.toString())
                        .getOrNull()
                        ?.let { dtos ->
                            val entries = dtos.map { dto -> dto.toCalendarEntry() }
                            calendarDao.insertAll(entries.map { it.toEntity() })
                        }
                } catch (e: Exception) {
                    println("Sync failed: ${e.message}")
                }
            }
    }

}