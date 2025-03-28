package org.greenthread.whatsinmycloset.features.tabs.social.data

import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.persistence.OutfitItems

data class OutfitState(
    val outfitId: String,
    val name: String,
    val itemIds: List<OutfitItems> = emptyList(),
    val items: List<ItemDto> = emptyList(),
    val isLoading: Boolean = true
)