package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.dto.OutfitDto
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity
import org.greenthread.whatsinmycloset.core.persistence.toClothingItem

/*
* This class provide methods for conversion to/from OutfitDto and OutfitEntity
* */
// TODO match with ERD
class Outfit(
    val id: String,
    val userId: Int? = null,    // matches with User class
    val public: Boolean = false,
    val favorite: Boolean = false,
    val mediaURL: String = "",
    val name: String = "",
    val tags: List<String>? = null,
    val itemIds: List<String>, // Store item IDs instead of full objects
    // TODO - we may not need the position below
    val itemPositions: Map<String, OffsetData> = emptyMap(), // Map of item IDs to their positions
    val createdAt: String = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()).toString()
) {
    // Convert Domain Model to DTO
    fun toDto(): OutfitDto {
        return OutfitDto(
            id = id,
            userId = userId,
            public = public,
            favorite = favorite,
            mediaURL = mediaURL,
            name = name,
            tags = tags,
            itemIds = itemIds,
            itemPositions = itemPositions,
            createdAt = createdAt
        )
    }

    // Convert Domain Model to Entity
    fun toEntity(userId: Int?= null): OutfitEntity {
        return OutfitEntity(
            outfitId = id,
            userId = userId,
            public = public,
            favorite = favorite,
            mediaURL = mediaURL,
            name = name,
            tags = tags,
            createdAt = createdAt
        )
    }

}

@Serializable
class OffsetData(
    val x: Float,
    val y: Float
)
