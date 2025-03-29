package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.persistence.OutfitItems
import org.greenthread.whatsinmycloset.features.tabs.social.data.OutfitState

/*
*   Represents the data transfer object (DTO) for Outfit
*   used in API communication and serialization.
*
*   Maps between the domain model (Outfit) and the persistence model (OutfitEntity).
* */

@Serializable
data class OutfitDto(
    val id: String,
    val name: String = "",
    val itemIds: List<OutfitItems>,
    val userId: Int,
    val tags: List<String> = emptyList(),
    val createdAt: String? = null
    val creator: CreatorDto? = null
)

@Serializable
data class CreatorDto(
    val username: String
)

@Serializable
data class OutfitResponse(
    val name: String,
    val userId: Int,
    val itemIds: List<OutfitItems>,
    val tags: List<String>,
    val id: String,
    val createdAt: String,
    val creator: CreatorDto? = null
)

