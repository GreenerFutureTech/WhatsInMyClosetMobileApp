package org.greenthread.whatsinmycloset.features.tabs.social.data

import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.dto.ItemPosition

data class OutfitState(
    val outfitId: String,
    val itemIds: List<ItemPosition> = emptyList(),
    val items: List<ItemDto> = emptyList(),
    val isLoading: Boolean = true
)