package org.greenthread.whatsinmycloset.features.tabs.home.domain

sealed interface HomeTabAction {
    data class OnViewWardrobeItems(val index: Int): HomeTabAction
    data class OnSetWardrobe(val index: Int): HomeTabAction
    data class OnAddNewItem(val index: Int): HomeTabAction
    data class CreateNewOutfit(val index: Int): HomeTabAction
}