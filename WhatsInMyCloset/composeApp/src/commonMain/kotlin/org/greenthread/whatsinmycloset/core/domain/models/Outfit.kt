package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.dto.CalendarDto
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity

/*
* This class provide methods for conversion to/from OutfitDto and OutfitEntity
* DOMAIN MODEL
* */
@Serializable
data class Outfit(
    val id: String = "",
    val name: String,
    val creatorId: Int,
    val items: Map<String, OffsetData> = emptyMap(),
    val tags: List<String> = emptyList(),
    val createdAt: String = ""
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
        tags = tags
    )
}

@Serializable
class OffsetData(
    val x: Float,
    val y: Float
)
