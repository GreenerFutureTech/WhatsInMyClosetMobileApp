package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.dto.CalendarDto
import org.greenthread.whatsinmycloset.core.persistence.CalendarEntity

// Domain Model (for app logic)
data class CalendarEntry(
    val outfitId: String,
    val userId: String,
    val date: String = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
) {
    fun toDto(): CalendarDto = CalendarDto(
        outfitId = outfitId,
        userId = userId,
        date = date
    )

    fun toEntity(): CalendarEntity = CalendarEntity(
        outfitId = outfitId,
        userId = userId,
        date = date
    )
}

// Extension functions for conversions
fun CalendarEntity.toCalendarEntry(): CalendarEntry = CalendarEntry(
    outfitId = outfitId,
    userId = userId,
    date = date
)

fun CalendarEntry.toOutfit(): Outfit {
    return Outfit(
        id = outfitId,
        name = "Outfit $outfitId", // Placeholder, replace with actual lookup
        creatorId = userId.toInt(),
        createdAt = date
    )
}

