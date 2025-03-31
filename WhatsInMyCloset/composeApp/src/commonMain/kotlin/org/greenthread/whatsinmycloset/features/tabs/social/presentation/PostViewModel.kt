package org.greenthread.whatsinmycloset.features.tabs.social.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.domain.onSuccess
import org.greenthread.whatsinmycloset.core.dto.toClothingItem
import org.greenthread.whatsinmycloset.core.managers.OutfitManager
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.repository.DefaultClosetRepository
import org.greenthread.whatsinmycloset.features.tabs.social.data.OutfitState
import org.greenthread.whatsinmycloset.features.tabs.social.data.PostState
import org.koin.compose.koinInject

open class PostViewModel(
    private val itemRepository: DefaultClosetRepository,
    private val userManager: UserManager,
    private val outfitManager: OutfitManager,
    private val wardrobeManager: WardrobeManager
) : ViewModel() {
    val currentUser = userManager.currentUser
    private val _state = MutableStateFlow(PostState())
    val state = _state

    val cachedOutfits: StateFlow<List<Outfit>> = outfitManager.cachedOutfits
    val cachedItems: StateFlow<List<ClothingItem>> = wardrobeManager.cachedItems

    // holds the refreshing state for UI updates when refreshing
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refresh() {
        fetchAllOutfitData()
    }

    fun fetchAllOutfitData() {
        viewModelScope.launch {
            _state.update{ it.copy( isLoading = true) }

            currentUser.value?.id?.let { it ->
                itemRepository.getFriendsOutfits(it)
                    .onSuccess { outfits ->
                        val outfitsList = outfits.map { outfit ->
                            OutfitState(
                                outfitId = outfit.id,
                                name = outfit.name,
                                itemIds = outfit.itemIds,
                                isLoading = true,
                                username = outfit.creator?.username,
                                profilePicture = outfit.creator?.profilePicture,
                                createdAt = outfit.createdAt
                            )
                        }.sortedByDescending { it.createdAt }
                        println("OUTFIT LIST: $outfitsList")

                        _state.update {
                            it.copy(
                                outfits = outfitsList,
                                isLoading = false
                            )
                        }
                        // Load items for each outfit
                        outfitsList.forEach { outfit ->
                            fetchItemsForOutfit(outfit.outfitId)
                        }
                    }
                    .onError { error ->
                        println("==============================ERROR FETCH API $error==============================")
                        _state.update { it.copy(isLoading = false) }
                    }
            }
        }
    }

    fun fetchItemsForOutfit(outfitId: String) {
        viewModelScope.launch {
            val outfit = _state.value.outfits.find { it.outfitId == outfitId } ?: return@launch
            // Mark as loading while fetching items from outfit
            loadOutfit(outfitId) { it.copy(isLoading = true) }
            val results = outfit.itemIds.mapNotNull { itemId ->
                itemId.id?.let { id ->
                    try {
                        itemRepository.getItemById(id).getOrNull()?.toClothingItem()
                    } catch (e: Exception) {
                        // Log conversion error but continue with other items
                        println("Error converting item $id: ${e.message}")
                        null
                    }
                }
            }
            loadOutfit(outfitId) {
                it.copy(
                    items = results,
                    isLoading = false
                )
            }
        }
    }

    private fun loadOutfit(outfitId: String, transform: (OutfitState) -> OutfitState) {
        _state.update { currentState ->
            currentState.copy(
                outfits = currentState.outfits.map { outfit ->
                    if (outfit.outfitId == outfitId) transform(outfit) else outfit
                }
            )
        }
    }

    fun fetchOutfitById(outfitId: String) {
        viewModelScope.launch {
            val currentOutfit = _state.value.outfits.find { it.outfitId == outfitId }

            if (currentOutfit == null) {
                _state.update { state ->
                    state.copy(
                        outfits = state.outfits + OutfitState(
                            outfitId = outfitId,
                            name = "",
                            itemIds = emptyList(),
                            isLoading = true,
                            username = "",
                            profilePicture = "",
                            createdAt = ""
                        )
                    )
                }
            }

            itemRepository.getOutfitById(outfitId)
                .onSuccess { outfitDto ->

                    _state.update {state ->
                        val updatedOutfits = state.outfits.map { outfit ->
                            if (outfit.outfitId == outfitId) {
                                val updatedOutfit = outfit.copy(
                                    name = outfitDto.name,
                                    itemIds = outfitDto.itemIds,
                                    tags = outfitDto.tags,
                                    createdAt = outfitDto.createdAt,
                                    isLoading = false
                                )

                                // Explicitly set the username and profilePicture
                                val finalOutfit = updatedOutfit.copy(
                                    username = outfitDto.creator?.username,
                                    profilePicture = outfitDto.creator?.profilePicture
                                )

                                finalOutfit
                            } else {
                                outfit
                            }
                        }
                        state.copy(outfits = updatedOutfits)
                    }

                    // Fetch items separately
                    fetchItemsForOutfit(outfitId)
                }
                .onError { error ->
                    _state.update { state ->
                        val updatedOutfits = state.outfits.map { outfit ->
                            if (outfit.outfitId == outfitId) {
                                outfit.copy(isLoading = false)
                            } else {
                                outfit
                            }
                        }
                        state.copy(outfits = updatedOutfits)
                    }
                    println("Error fetching outfit: $error")
                }

        }
    }

    fun fetchUserOutfits(userId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            itemRepository.getAllOutfitsForUser(userId)
                .onSuccess { outfits ->
                    val outfitsList = outfits.map { outfit ->
                        OutfitState(
                            outfitId = outfit.id,
                            name = outfit.name,
                            itemIds = outfit.itemIds,
                            isLoading = true,
                            username = outfit.creator?.username,
                            profilePicture = outfit.creator?.profilePicture,
                            userId = outfit.userId,
                            createdAt = outfit.createdAt
                        )
                    }.sortedByDescending { it.createdAt }

                    _state.update {
                        it.copy(
                            outfits = outfitsList,
                            isLoading = false
                        )
                    }

                    // Load items for each outfit
                    outfitsList.forEach { outfit ->
                        fetchItemsForOutfit(outfit.outfitId)
                    }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    println("Error fetching user outfits: $error")
                }
        }
    }
}