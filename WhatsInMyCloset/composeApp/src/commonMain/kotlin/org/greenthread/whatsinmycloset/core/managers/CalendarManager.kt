package org.greenthread.whatsinmycloset.core.managers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import org.greenthread.whatsinmycloset.core.domain.models.CalendarEntry
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.models.toCalendarEntry
import org.greenthread.whatsinmycloset.core.repositories.CalendarRepository
import org.greenthread.whatsinmycloset.core.persistence.Converters

open class CalendarManager(
    private val calendarRepository: CalendarRepository,
    private val userManager: UserManager // Inject current user's info
)
{

    private val currentUser = userManager.currentUser // Get the current user

    suspend fun getOutfitForDate(date: LocalDate): Outfit? {
        return withContext(Dispatchers.IO) {
            try {
                currentUser.value?.id?.let { userId ->
                    calendarRepository.getOutfitForDate(userId, date)
                        .first()
                        ?.let { outfit ->
                            // Apply any additional transformations if needed
                            outfit
                        }
                }
            } catch (e: Exception) {
                println("Error getting outfit: ${e.message}")
                null
            }
        }
    }

    suspend fun saveOutfitToCalendar(calendarEntry: CalendarEntry): Boolean {

        return calendarRepository.saveOutfitToCalendar(calendarEntry)
    }

}
