package org.greenthread.whatsinmycloset.app

import kotlinx.serialization.Serializable

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
    data object MessageGraph : Routes

    @Serializable
    data object LoginTab : Routes
    @Serializable
    data object SignUpTab : Routes
    @Serializable
    data object HomeTab : Routes

    // Routes for outfit creation screens
    @Serializable
    data object CreateOutfitScreen: Routes
    @Serializable
    data class CategoryItemScreen(
        val category: String) : Routes    // shows all items in that category, for example "Tops"
    @Serializable
    data object OutfitSaveScreen: Routes
    @Serializable
    data class CategoryItemDetailScreen
        (val clickedItemWardrobeID: String,
         val clickedItemID: String,
         val clickedItemCategory: String): Routes // show the clicked items details

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
    data object AddSwapScreen : Routes

    @Serializable
    data object SocialDetailsScreen : Routes

    @Serializable
    data object AllSwapScreen : Routes

    @Serializable
    data object MessageListScreen : Routes

    @Serializable
    data object ChatScreen : Routes

    @Serializable
    data class WardrobeItemsScreen(val id: String) : Routes

    @Serializable
    data object AddItemScreen : Routes

    @Serializable
    data object SettingsScreen: Routes

    @Serializable
    data object NotificationsScreen: Routes
}