package org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.domain.onSuccess
import org.greenthread.whatsinmycloset.core.repository.ClosetRepository
import org.greenthread.whatsinmycloset.features.tabs.swap.Action.SwapAction
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState

class SwapViewModel(
    private val swapRepository: ClosetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SwapListState())
    val state =_state
        .onStart {
            // NEED TO UPDATE : to current user id
            fetchSwapData("1")
            fetchOtherSwapData("1")
        }

    fun onAction(action: SwapAction) {
        when (action) {
            is SwapAction.OnSwapClick -> {

            }
        }
    }

    fun fetchAllSwapData() {
        viewModelScope.launch {
            println("FETCHALLSWAP : Fetching All Swap data")
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            swapRepository
                .getAllSwaps()
                .onSuccess { getResults ->
                    println("FETCHALLSWAP API success: $getResults")
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
            println("FETCHSWAP : Fetching data for user: $userId")
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            swapRepository
                .getSwaps(userId)
                .onSuccess { getResults ->
                    println("FETCHSWAP API success: $getResults")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            getUserSwapResults = getResults
                        )
                    }
                }
                .onError { error ->
                    println("FETCHSWAP API ERROR ${error}")
                    _state.update {
                        it.copy(
                            getUserSwapResults = emptyList(),
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun fetchOtherSwapData(userId: String) {
        viewModelScope.launch {
            println("FETCHOTHERSWAP : Fetching other users' swap data for user: $userId")
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            swapRepository
                .getOtherUsersSwaps(userId)
                .onSuccess { getResults ->
                    println("FETCHOTHERSWAP API success: $getResults")
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
}
