package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity

/*
*   Represents the data transfer object (DTO) for Outfit
*   used in API communication and serialization.
*
*   Maps between the domain model (Outfit) and the persistence model (OutfitEntity).
* */

@Serializable
data class OutfitDto(
    val id: String, // Unique identifier for the outfit
    val userId: Int? = null,    // matches with User.kt
    val public: Boolean,
    val favorite: Boolean,
    val mediaURL: String = "",
    val name: String = "", // Name of the outfit (e.g., "Summer Look")
    val tags: List<String>? = null,
    val items: List<ClothingItem>, // use the ClothingItem class as an outfit will consist of
    // one or more clothing items
    val createdAt: String = ""
)

fun OutfitDto.toEntity(userId: Int): OutfitEntity {
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