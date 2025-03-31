package org.greenthread.whatsinmycloset.core.dto

import androidx.room.Embedded
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.domain.models.CalendarEntry
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.persistence.CalendarEntity
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity

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

fun CalendarDto.toOutfit(): Outfit {
    return Outfit(
        id = outfitId,
        name = "Outfit $date", // Default name based on date
        userId = userId,
        itemIds = emptyList(), // Will be populated separately
        tags = emptyList(), // Will be populated separately
        createdAt = date // Using calendar date as creation date
    )
}

@Serializable
data class CalendarResponse(
    val outfitId: String,
    val userId: Int,
    val date: String,
    val id: String
) {
    fun toCalendarEntry(): CalendarEntry = CalendarEntry(
        outfitId = outfitId,
        userId = userId,
        date = date
    )

    fun toOutfit(): Outfit {
        return Outfit(
            id = outfitId,
            name = "Outfit for $date",
            userId = userId,
            itemIds = emptyList(),
            tags = emptyList(),
            createdAt = date
        )
    }
}

data class CalendarWithOutfit(
    val calendarId: Long,
    val calendarOutfitId: String,
    val userId: Int,
    val date: String,

    val id: String,
    val name: String,
    val itemIds: String,
    val tags: String,
    val createdAt: String
) {
    fun toCalendarEntry(): CalendarEntry = CalendarEntry(
        outfitId = calendarOutfitId,
        userId = userId,
        date = date
    )

    fun toOutfit(): Outfit {
        val json = Json { ignoreUnknownKeys = true }
        return Outfit(
            id = id,
            name = name,
            userId = userId,
            itemIds = json.decodeFromString(itemIds),
            tags = json.decodeFromString(tags),
            createdAt = createdAt,
            creator = null
        )
    }
}
