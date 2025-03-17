package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.data.daos.ItemDao
import org.greenthread.whatsinmycloset.core.dto.OutfitDto
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity
import org.greenthread.whatsinmycloset.core.persistence.toClothingItem

/*
* This class provide methods for conversion to/from OutfitDto and OutfitEntity
* */
class Outfit(
    val id: String,
    val userId: Int? = null,    // matches with User class
    val public: Boolean,
    val favorite: Boolean,
    val mediaURL: String = "",
    val name: String = "",
    val tags: List<String>? = null,
    val items: List<ClothingItem>,
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
            items = items,
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
            itemPositions = itemPositions,
            createdAt = createdAt
        )
    }

    // Update tags
    fun updateTags(newTags: List<String>): Outfit {
        return Outfit(
            id = id,
            userId = userId,
            public = public,
            favorite = favorite,
            mediaURL = mediaURL,
            name = name,
            tags = newTags,
            items = items,
            itemPositions = itemPositions,
            createdAt = createdAt
        )
    }

}

@Serializable
class OffsetData(
    val x: Float,
    val y: Float
)
