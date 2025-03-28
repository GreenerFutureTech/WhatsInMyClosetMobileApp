package org.greenthread.whatsinmycloset.core.managers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.models.toCalendarEntry
import org.greenthread.whatsinmycloset.core.repositories.CalendarRepository
import org.greenthread.whatsinmycloset.core.domain.models.toOutfit

open class CalendarManager(
    private val calendarRepository: CalendarRepository,
    private val userManager: UserManager // Inject current user's info
)
{

    private val currentUser = userManager.currentUser // Get the current user

    // Get outfits for the current user
    open suspend fun getOutfitsFromCalendar(): List<Outfit> {
        return withContext(Dispatchers.IO) {
            currentUser.value?.id?.let {
                calendarRepository.getOutfitsFromCalendar(it)
                    .first()  // Collects the first emitted value from Flow
                    .map { calendarEntry -> calendarEntry.toOutfit() }
            }!! // Convert CalendarEntry to Outfit
        }
    }



    suspend fun saveOutfitToCalendar(outfit: Outfit): Boolean {
        val entry = outfit.toCalendarEntry(currentUser.value?.id.toString())
        return calendarRepository.saveOutfitToCalendar(entry)
    }

}
