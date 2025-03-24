package org.greenthread.whatsinmycloset.features.tabs.swap.presentation.AddSwap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.domain.onSuccess
import org.greenthread.whatsinmycloset.core.dto.CreateSwapRequestDto
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.repository.ClosetRepository
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState

class AddSwapViewModel(
    private val swapRepository: ClosetRepository,
    private val wardrobeManager: WardrobeManager,
    private val userManager: UserManager
) : ViewModel() {
    private val _state = MutableStateFlow(SwapListState())

    val cachedWardrobes: StateFlow<List<Wardrobe>> = wardrobeManager.cachedWardrobes
    val cachedItems: StateFlow<List<ClothingItem>> = wardrobeManager.cachedItems
    val currentUser = userManager.currentUser

    fun createSwap(itemIds: List<String>) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val userId = currentUser.value?.id

        if (userId == null) {
             println("User ID is null")
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            itemIds.forEach { itemId ->
                val swap = CreateSwapRequestDto(
                    userId = userId,
                    status = "available",
                    registeredAt = now.toString(),
                    updatedAt = now.toString(),
                    itemId = itemId
                )

                swapRepository
                    .createSwap(swap)
                    .onSuccess { getResults ->
                        println("CREATE SWAP API success: $getResults")
                    }
                    .onError { error ->
                        println("CREATE SWAP API ERROR: $error")
                    }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }
}