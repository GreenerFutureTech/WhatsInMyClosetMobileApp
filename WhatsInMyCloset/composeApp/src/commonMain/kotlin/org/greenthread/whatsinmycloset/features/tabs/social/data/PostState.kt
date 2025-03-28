package org.greenthread.whatsinmycloset.features.tabs.social.data

data class PostState(
    val isLoading: Boolean = true,
    val outfits: List<OutfitState> = emptyList() // Store multiple outfits
)