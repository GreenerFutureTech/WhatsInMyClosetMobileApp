package org.greenthread.whatsinmycloset.core.dto

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData
import org.greenthread.whatsinmycloset.core.persistence.OutfitEntity
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
    val id: String, // Unique identifier for the outfit
    val userId: Int? = null,    // matches with User.kt
    val name: String = "",
    val itemIds: List<ItemPosition?>? = null,
    val tags: List<String>? = null, // Name of the outfit (e.g., "Summer Look")
    val createdAt: String = ""

){
    fun toOutfitState(): OutfitState {
        return OutfitState(
            outfitId = this.id,
            itemIds = this.itemIds?.mapNotNull { it?.id } ?: emptyList(),
            items = emptyList(),
            isLoading = true
        )
    }
}