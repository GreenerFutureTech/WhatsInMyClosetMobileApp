package org.greenthread.whatsinmycloset.app

import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.features.tabs.swap.dto.SwapDto

sealed interface Routes {
    @Serializable
    data object HomeGraph : Routes
    @Serializable
    data object ProfileGraph : Routes
    @Serializable
    data object SwapGraph : Routes
    @Serializable
    data object SocialGraph : Routes
    @Serializable
    data object LoginGraph : Routes

    @Serializable
    data object LoginTab : Routes
    @Serializable
    data object SignUpTab : Routes
    @Serializable
    data object HomeTab : Routes

    @Serializable
    data object CreateOutfitScreen : Routes
    @Serializable
    data class CategoryItemScreen(val category: String) : Routes

    @Serializable
    data object ProfileTab : Routes
    @Serializable
    data object SwapTab : Routes
    @Serializable
    data object SocialTab : Routes

    @Serializable
    data object ProfileDetailsScreen : Routes
    @Serializable
    data class SwapDetailsScreen(val swap: String) : Routes
    @Serializable
    data object SocialDetailsScreen : Routes

    @Serializable
    data class WardrobeItemsScreen(val id: String) : Routes
}