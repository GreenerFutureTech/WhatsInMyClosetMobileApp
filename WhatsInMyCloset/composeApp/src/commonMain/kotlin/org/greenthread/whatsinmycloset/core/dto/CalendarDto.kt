package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.CalendarEntry

// Data Transfer Object (for API communication)
@Serializable
data class CalendarDto(
    val outfitId: String,
    val userId: Int,
    val date: String
)

fun CalendarDto.toCalendarEntry(): CalendarEntry = CalendarEntry(
    outfitId = outfitId,
    userId = userId,
    date = date
)

@Serializable
data class CalendarResponse(
    val outfitId: String,
    val userId: Int,
    val date: String,
    val id: String
)
