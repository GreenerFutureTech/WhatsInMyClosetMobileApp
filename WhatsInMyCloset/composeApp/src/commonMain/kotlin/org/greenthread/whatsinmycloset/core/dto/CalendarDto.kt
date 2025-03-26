package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.CalendarEntry

// Data Transfer Object (for API communication)
data class CalendarDto(
    val outfitId: String,
    val userId: String,
    val date: String
)

fun CalendarDto.toCalendarEntry(): CalendarEntry = CalendarEntry(
    outfitId = outfitId,
    userId = userId,
    date = date
)
