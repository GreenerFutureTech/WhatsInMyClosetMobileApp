package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.dto.OutfitDto
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity
import org.greenthread.whatsinmycloset.core.persistence.OutfitItem

/*
* This class provide methods for conversion to/from OutfitDto and OutfitEntity
* DOMAIN MODEL
* */
@Serializable
data class Outfit(
    val id: String,
    val name: String,
    val creatorId: Int,
    val items: Map<String, OffsetData> = emptyMap(),
    val tags: List<String> = emptyList(),
    val calendarDates: List<LocalDate> = emptyList(),
    val createdAt: String = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()).toString()
) {
    val itemIds: List<String> get() = items.keys.toList()
}

private val json = Json { ignoreUnknownKeys = true }

// Extension function to convert Entity to Domain
fun OutfitEntity.toDomain(): Outfit {
    return Outfit(
        id = outfitId,
        name = name,
        creatorId = creatorId,
        items = json.decodeFromString(items),
        tags = json.decodeFromString(tags),
        calendarDates = getCalendarDates().map { LocalDate.parse(it) },
        createdAt = createdAt
    )
}


// Extension function to convert Domain to Entity
fun Outfit.toEntity(): OutfitEntity {
    return OutfitEntity.create(
        outfitId = id,
        name = name,
        creatorId = creatorId,
        items = items,
        tags = tags,
        calendarDates = calendarDates.map { it.toString() }
    )
}


@Serializable
class OffsetData(
    val x: Float,
    val y: Float
)
