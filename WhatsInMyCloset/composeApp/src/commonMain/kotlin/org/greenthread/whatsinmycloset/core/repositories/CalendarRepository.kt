package org.greenthread.whatsinmycloset.core.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import org.greenthread.whatsinmycloset.core.data.daos.CalendarDao
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.isSuccess
import org.greenthread.whatsinmycloset.core.domain.models.CalendarEntry
import org.greenthread.whatsinmycloset.core.domain.models.toCalendarEntry
import org.greenthread.whatsinmycloset.core.dto.toCalendarEntry
import org.greenthread.whatsinmycloset.core.network.KtorRemoteDataSource


open class CalendarRepository(
    private val calendarDao: CalendarDao,
    val remoteSource: KtorRemoteDataSource
) {
    suspend fun saveOutfitToCalendar(calendarEntry: CalendarEntry): Boolean {
        return try {
            // 1. First save to backend
            val remoteResult = remoteSource.postOutfitToCalendar(calendarEntry.toDto())

            // 2. If successful, save to local database
            if (remoteResult.isSuccess()) {
                calendarDao.insertCalendarEntry(calendarEntry.toEntity())
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

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

}