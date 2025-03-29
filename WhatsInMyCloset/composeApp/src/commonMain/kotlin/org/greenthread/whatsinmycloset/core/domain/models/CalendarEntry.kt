package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.dto.CalendarDto
import org.greenthread.whatsinmycloset.core.persistence.CalendarEntity

// Domain Model (for app logic)
@Serializable
data class CalendarEntry(
    val outfitId: String,
    val userId: Int,
    val date: String
)

fun CalendarEntry.toEntity(): CalendarEntity {
    return CalendarEntity(
        outfitId = this.outfitId,
        userId = this.userId,
        date = this.date
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

