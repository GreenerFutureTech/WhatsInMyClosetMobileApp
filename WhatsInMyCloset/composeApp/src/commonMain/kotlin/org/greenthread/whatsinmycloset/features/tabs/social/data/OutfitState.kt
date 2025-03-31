package org.greenthread.whatsinmycloset.features.tabs.social.data

import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.dto.CreatorDto
import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.persistence.OutfitItems

data class OutfitState(
    val outfitId: String,
    val username: String?,
    val profilePicture: String?,
    val name: String,
    val itemIds: List<OutfitItems> = emptyList(),
    val items: List<ClothingItem> = emptyList(),
    val tags: List<String> = emptyList(),
    val createdAt: String? = null,
    val isLoading: Boolean = true,
    val userId: Int? = null,
    val creator: CreatorDto? = null,
) {
    companion object {
        fun fromOutfit(outfit: Outfit, items: List<ClothingItem> = emptyList()): OutfitState {
            return OutfitState(
                outfitId = outfit.id,
                username = outfit.creator?.username,
                profilePicture = outfit.creator?.profilePicture,
                name = outfit.name,
                itemIds = outfit.itemIds,
                items = items,
                tags = outfit.tags,
                createdAt = outfit.createdAt,
                isLoading = false,
                userId = outfit.userId,
                creator = outfit.creator
            )
        }
    }
}

fun List<Outfit>.toOutfitState(itemsMap: Map<String, List<ClothingItem>> = emptyMap()): List<OutfitState> {
    return this.map { outfit ->
        OutfitState.fromOutfit(
            outfit = outfit,
            items = itemsMap[outfit.id] ?: emptyList()
        )
    }
}

