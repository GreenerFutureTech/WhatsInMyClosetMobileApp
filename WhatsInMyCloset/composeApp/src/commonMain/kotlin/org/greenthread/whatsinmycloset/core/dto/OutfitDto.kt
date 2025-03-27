package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.persistence.OutfitItems

/*
*   Represents the data transfer object (DTO) for Outfit
*   used in API communication and serialization.
*
*   Maps between the domain model (Outfit) and the persistence model (OutfitEntity).
* */

@Serializable
data class OutfitDto(
    val name: String = "",
    val itemIds: List<OutfitItems>,
    val userId: String,
    val tags: List<String> = emptyList(),
)

@Serializable
data class OutfitResponse(
    val name: String,
    val userId: String,
    val itemIds: List<OutfitItems>,
    val tags: List<String>,
    val id: String,
    val createdAt: String
)

