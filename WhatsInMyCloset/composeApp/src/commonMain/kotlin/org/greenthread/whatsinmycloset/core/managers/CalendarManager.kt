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
import org.greenthread.whatsinmycloset.core.domain.models.toOutfit
import org.greenthread.whatsinmycloset.core.persistence.Converters

open class CalendarManager(
    private val calendarRepository: CalendarRepository,
    private val userManager: UserManager // Inject current user's info
)
{

    private val currentUser = userManager.currentUser // Get the current user

    // Get outfits for the current user
    // For initial load/all outfits
    open suspend fun getOutfitsFromCalendar(): List<Outfit> {
        return withContext(Dispatchers.IO) {
            currentUser.value?.id?.let { userId ->
                calendarRepository.getOutfitsFromCalendar(userId)
                    .first()
                    .map { it.toOutfit() }
            } ?: emptyList()
        }
    }

    // For date-specific lookups
    suspend fun getOutfitForDate(date: LocalDate): Outfit? {
        return withContext(Dispatchers.IO) {
            try {
                userManager.currentUser.value?.id?.let { userId ->
                    calendarRepository.getOutfitForDate(userId, date)
                        .first()
                        ?.let { outfitEntity ->
                            val converters = Converters()
                            Outfit(
                                id = outfitEntity.outfitId,
                                name = outfitEntity.name,
                                creatorId = outfitEntity.creatorId,
                                items = converters.stringToOffsetMap(outfitEntity.items),
                                tags = converters.stringToStringList(outfitEntity.tags),
                                createdAt = outfitEntity.createdAt
                            )
                        }
                }
            } catch (e: Exception) {
                // Log error if needed
                println("Error converting outfit: ${e.message}")
                null
            }
        }
    }

    suspend fun saveOutfitToCalendar(calendarEntry: CalendarEntry): Boolean {

        return calendarRepository.saveOutfitToCalendar(calendarEntry)
    }

}
