package org.greenthread.whatsinmycloset.core.domain.models

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.greenthread.whatsinmycloset.core.dto.OutfitDto
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity

/*
* This class provide methods for conversion to/from OutfitDto and OutfitEntity
* */
class Outfit(
    val id: String,
    val userId: String,
    val public: Boolean,
    val favorite: Boolean,
    val mediaURL: String = "",
    val name: String = "",
    val tags: List<String>? = null,
    val items: List<ClothingItem>,
    val createdAt: String = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()).toString()
) {
    companion object {
        fun createOutfit(
            userId: String,
            clothingItems: List<ClothingItem>,
            name: String = "Summer Look",
            public: Boolean = true,
            favorite: Boolean = true,
            mediaURL: String = "",
            tags: List<String>? = null
        ): Outfit {
            val outfitId = "outfit_${Clock.System.now().toEpochMilliseconds()}" // Generate a unique ID
            return Outfit(
                id = outfitId,
                userId = userId,
                public = public,
                favorite = favorite,
                mediaURL = mediaURL,
                name = name,
                tags = tags,
                items = clothingItems
            )
        }

        // Convert from DTO to Domain Model
        fun fromDto(outfitDto: OutfitDto): Outfit {
            return Outfit(
                id = outfitDto.id,
                userId = outfitDto.userId,
                public = outfitDto.public,
                favorite = outfitDto.favorite,
                mediaURL = outfitDto.mediaURL,
                name = outfitDto.name,
                tags = outfitDto.tags,
                items = outfitDto.items
            )
        }

        // Convert from Entity to Domain Model
        fun fromEntity(outfitEntity: OutfitEntity): Outfit {
            return Outfit(
                id = outfitEntity.id,
                userId = outfitEntity.userId,
                public = outfitEntity.public,
                favorite = outfitEntity.favorite,
                mediaURL = outfitEntity.mediaURL,
                name = outfitEntity.name,
                tags = outfitEntity.tags,
                items = outfitEntity.items
            )
        }
    }

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
            createdAt = createdAt
        )
    }

    // Convert Domain Model to Entity
    fun toEntity(): OutfitEntity {
        return OutfitEntity(
            id = id,
            userId = userId,
            public = public,
            favorite = favorite,
            mediaURL = mediaURL,
            name = name,
            tags = tags,
            items = items,
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
            createdAt = createdAt
        )
    }

    // Add a tag
    fun addTag(tag: String): Outfit {
        val updatedTags = tags?.toMutableList() ?: mutableListOf()
        if (!updatedTags.contains(tag)) {
            updatedTags.add(tag)
        }
        return updateTags(updatedTags)
    }

    // Remove a tag
    fun removeTag(tag: String): Outfit {
        val updatedTags = tags?.toMutableList() ?: mutableListOf()
        updatedTags.remove(tag)
        return updateTags(updatedTags)
    }
}
