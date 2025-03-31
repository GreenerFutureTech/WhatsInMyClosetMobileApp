package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.dto.CalendarDto
import org.greenthread.whatsinmycloset.core.dto.CreatorDto
import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.dto.OutfitDto
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity
import org.greenthread.whatsinmycloset.core.persistence.OutfitItems
import org.greenthread.whatsinmycloset.features.tabs.social.data.OutfitState

@Serializable
data class Outfit(
    val id: String,
    val name: String = "",
    val itemIds: List<OutfitItems>,
    val userId: Int,
    val tags: List<String> = emptyList(),
    val createdAt: String? = null,
    val creator: CreatorDto? = null
) {
    companion object {
        fun fromDto(dto: OutfitDto): Outfit {
            return Outfit(
                id = dto.id,
                name = dto.name,
                itemIds = dto.itemIds,
                userId = dto.userId,
                tags = dto.tags,
                createdAt = dto.createdAt,
                creator = dto.creator
            )
        }

        fun fromEntity(entity: OutfitEntity): Outfit {
            return fromDto(entity.toDto())
        }
    }

    fun toDto(): OutfitDto {
        return OutfitDto(
            id = id,
            name = name,
            itemIds = itemIds,
            userId = userId,
            tags = tags,
            createdAt = createdAt,
            creator = creator
        )
    }

    fun toEntity(): OutfitEntity {
        return OutfitEntity.fromDto(this.toDto())
    }
}

private val json = Json { ignoreUnknownKeys = true }

// Extension function to convert Entity to Domain
fun OutfitEntity.toDomain(): Outfit {
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

fun Outfit.toOutfitState(
    username: String? = null,
    profilePicture: String? = null,
    isLoading: Boolean = false,
    items: List<ClothingItem> = emptyList()
): OutfitState {
    return OutfitState(
        outfitId = id,
        username = username,
        profilePicture = profilePicture,
        name = name,
        itemIds = itemIds,
        items = items,
        tags = tags,
        createdAt = createdAt,
        isLoading = isLoading,
        userId = userId,
        creator = creator
    )
}

@Serializable
class OffsetData(
    val x: Float,
    val y: Float
)
