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
class Outfit(
    val id: String,
    val name: String = "",
    val creatorId: Int,
    val items: Map<String, OffsetData> = emptyMap(), // itemId to position mapping
    val tags: List<String> = emptyList(),
    val calendarDates: List<LocalDate> = emptyList(),
    val createdAt: String = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()).toString()
){
    companion object {
        private val json = Json { ignoreUnknownKeys = true }
    }

    // Get just the item IDs
    val itemIds: List<String> get() = items.keys.toList()
}

// Extension functions for conversions
fun OutfitEntity.toDomain(): Outfit {
    return Outfit(
        id = outfitId,
        name = name,
        creatorId = creatorId,
        items = Json.decodeFromString<List<OutfitItem>>(items)
            .associate { it.id to OffsetData(it.x, it.y) },
        tags = Json.decodeFromString(tags),
        calendarDates = Json.decodeFromString<List<String>>(calendarDates)
            .map { LocalDate.parse(it) },
        createdAt = createdAt
    )
}

fun Outfit.toEntity(): OutfitEntity {
    return OutfitEntity(
        outfitId = id,
        name = name,
        creatorId = creatorId,
        items = Json.encodeToString(
            items.map { (itemId, offset) ->
                OutfitItem(itemId, offset.x, offset.y)
            }
        ),
        tags = Json.encodeToString(tags),
        calendarDates = Json.encodeToString(calendarDates.map { it.toString() }),
        createdAt = createdAt
    )
}

@Serializable
class OffsetData(
    val x: Float,
    val y: Float
)
