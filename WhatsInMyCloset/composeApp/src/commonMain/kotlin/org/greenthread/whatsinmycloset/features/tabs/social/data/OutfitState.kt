package org.greenthread.whatsinmycloset.features.tabs.social.data

import org.greenthread.whatsinmycloset.core.dto.ItemDto
data class OutfitState(
    val outfitId: String,
    val itemIds: List<String>,
    val items: List<ItemDto> = emptyList(),
    val isLoading: Boolean = true
)