package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity
import org.greenthread.whatsinmycloset.core.persistence.OutfitItem

/*
*   Represents the data transfer object (DTO) for Outfit
*   used in API communication and serialization.
*
*   Maps between the domain model (Outfit) and the persistence model (OutfitEntity).
* */

@Serializable
data class OutfitDto(
    val id: String,
    val creatorId: Int,
    val name: String = "",
    val items: Map<String, OffsetDataDto>,
    val tags: List<String> = emptyList(),
    val calendarDates: List<String> = emptyList(),
    val createdAt: String = ""
)

@Serializable
data class OffsetDataDto(
    val x: Float,
    val y: Float
)

