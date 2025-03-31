package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.persistence.OutfitItems

@Serializable
data class OutfitDto(
    val id: String,
    val name: String = "",
    val itemIds: List<OutfitItems>,
    val userId: Int,
    val tags: List<String> = emptyList(),
    val createdAt: String? = null,
    val creator: CreatorDto? = null
)

fun OutfitDto.toOutfit(): Outfit {
    return Outfit(
        id = id,
        name = name,
        itemIds = itemIds,
        userId = userId,
        tags = tags,
        createdAt = createdAt,
        creator = creator
    )
}

fun List<OutfitDto>.toOutfitList(): List<Outfit> {
    return this.map { it.toOutfit() }
}

@Serializable
data class CreatorDto(
    val username: String,
    val profilePicture: String? = "",
)