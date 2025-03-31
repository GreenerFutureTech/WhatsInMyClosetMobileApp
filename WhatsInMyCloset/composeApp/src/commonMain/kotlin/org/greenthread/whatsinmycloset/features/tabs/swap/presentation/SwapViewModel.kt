package org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.domain.onSuccess
import org.greenthread.whatsinmycloset.core.dto.CreateSwapRequestDto
import org.greenthread.whatsinmycloset.core.repository.ClosetRepository
import org.greenthread.whatsinmycloset.features.tabs.swap.Action.SwapAction
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState

class SwapViewModel(
    private val swapRepository: ClosetRepository,
    private val userManager: UserManager
) : ViewModel() {
    val currentUser = userManager.currentUser

    private val _state = MutableStateFlow(SwapListState())
    val state =_state
        .onStart {
            fetchSwapData(currentUser.value?.id.toString())
            fetchFriendsSwapData(currentUser.value?.id.toString())
        }

    fun fetchAllSwapData() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            swapRepository
                .getAllSwaps()
                .onSuccess { getResults ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            getAllSwapResults = getResults
                        )
                    }
                }
                .onError { error ->
                    println("FETCHALLSWAP API ERROR ${error}")
                    _state.update {
                        it.copy(
                            getAllSwapResults = emptyList(),
                            isLoading = false
                        )
                    }
                }
        }
    }
    fun fetchSwapData(userId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            swapRepository
                .getSwaps(userId)
                .onSuccess { getResults ->
                    _state.update {
                        if (userId == currentUser.value?.id.toString()) {
                            it.copy(
                                isLoading = false,
                                getUserSwapResults = getResults
                            )
                        } else {
                            it.copy(
                                isLoading = false,
                                getSearchedUserSwapResults = getResults
                            )
                        }
                    }
                }
                .onError { error ->
                    println("FETCHSWAP API ERROR ${error}")
                    _state.update {
                        if (userId == currentUser.value?.id.toString()) {
                            it.copy(
                                getUserSwapResults = emptyList(),
                                isLoading = false
                            )
                        } else {
                            it.copy(
                                getSearchedUserSwapResults = emptyList(),
                                isLoading = false
                            )
                        }
                    }
                }
        }
    }

    fun fetchFriendsSwapData(userId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            swapRepository
                .getFriendsSwaps(userId)
                .onSuccess { getResults ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            getOtherUserSwapResults = getResults
                        )
                    }
                }
                .onError { error ->
                    println("FETCHOTHERSWAP API ERROR ${error}")
                    _state.update {
                        it.copy(
                            getOtherUserSwapResults = emptyList(),
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun updateSwap(itemId: String) {
        viewModelScope.launch {
            println("UPDATE SWAP : Completed Swap: $itemId")
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            swapRepository
                .updateStatus(itemId)
                .onSuccess { getResults ->
                    println("UPDATE SWAP API success: $getResults")
                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
                .onError { error ->
                    println("UPDATE SWAP API ERROR ${error}")
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun deleteSwap(itemId: String) {
        viewModelScope.launch {
            println("DELETE SWAP : DELETED Swap: $itemId")
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            swapRepository
                .deleteSwap(itemId)
                .onSuccess { getResults ->
                    println("DELETE SWAP API success: $getResults")
                    _state.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                }
                .onError { error ->
                    println("DELETE SWAP API ERROR ${error}")
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun getUser(userId: Int) {
        viewModelScope.launch {
            println("GET USER BY ID : get user $userId")
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            swapRepository
                .getUserById(userId)
                .onSuccess { getResults ->
                    println("GET USER BY ID API success: $getResults")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            searchedUserInfo = getResults
                        )
                    }
                }
                .onError { error ->
                    println("GET USER BY ID API ERROR ${error}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            searchedUserInfo = null
                        )
                    }
                }
        }
    }


}
