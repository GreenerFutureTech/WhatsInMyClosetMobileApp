package org.greenthread.whatsinmycloset.features.tabs.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.managers.OutfitManager
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.repositories.WardrobeRepository
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe
import org.greenthread.whatsinmycloset.features.tabs.social.data.OutfitState
import org.greenthread.whatsinmycloset.features.tabs.social.data.PostState

class HomeTabViewModel(
    private val wardrobeRepository: WardrobeRepository,
    private val wardrobeManager: WardrobeManager,
    private val outfitManager: OutfitManager
) : ViewModel() {

    val cachedWardrobes: StateFlow<List<Wardrobe>> = wardrobeManager.cachedWardrobes
    val cachedItems: StateFlow<List<ClothingItem>> = wardrobeManager.cachedItems
    val cachedOutfits: StateFlow<List<Outfit>> = outfitManager.cachedOutfits

    private val _state = MutableStateFlow(PostState())
    val state: StateFlow<PostState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(cachedOutfits, cachedItems) { outfits, items ->
                Pair(outfits, items)
            }.collectLatest { (outfits, items) ->
                updateOutfitsState(outfits, items)
            }

            CoroutineScope(Dispatchers.IO).launch {
                cachedOutfits.collectLatest {
                    refreshOutfits()
                }
            }
        }
    }

    private fun updateOutfitsState(outfits: List<Outfit>, items: List<ClothingItem>) {
        val outfitsList = outfits.map { outfit ->
            val outfitItems = items.filter { item ->
                outfit.itemIds.any { outfitItem -> outfitItem.id == item.id }
            }

            OutfitState(
                outfitId = outfit.id,
                name = outfit.name,
                itemIds = outfit.itemIds,
                items = outfitItems,
                tags = outfit.tags,
                createdAt = outfit.createdAt,
                isLoading = false,
                username = outfit.creator?.username,
                profilePicture = outfit.creator?.profilePicture,
                userId = outfit.userId
            )
        }
        _state.update {
            it.copy(
                outfits = outfitsList,
                isLoading = false
            )
        }
    }

    fun refreshOutfits() {
        viewModelScope.launch {
            updateOutfitsState(cachedOutfits.value, cachedItems.value)
        }
    }
}

data class HomeTabState(
    val isLoading: Boolean = true,
    val selectedWardrobeIndex: Int = 0,
)