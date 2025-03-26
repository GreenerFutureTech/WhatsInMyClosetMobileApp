package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.features.tabs.social.data.OutfitState

/*
*   Represents the data transfer object (DTO) for Outfit
*   used in API communication and serialization.
*
*   Maps between the domain model (Outfit) and the persistence model (OutfitEntity).
* */

@Serializable
data class ItemPosition(
    val x: Int? = null,
    val y: Int? = null,
    val id: String? = null
)

@Serializable
data class OutfitDto(
    val id: String,
    val userId: Int? = null,
    val name: String = "",
    val itemIds: List<ItemPosition?>? = null,
    val tags: List<String>? = null,
    val createdAt: String = ""
) {
    fun toOutfitState(): OutfitState {
        return OutfitState(
            outfitId = this.id,
            itemIds = this.itemIds?.filterNotNull() ?: emptyList(), // Keep the position data
            items = emptyList(),
            isLoading = true
        )
    }
}