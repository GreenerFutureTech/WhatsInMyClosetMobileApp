package org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.domain.onSuccess
import org.greenthread.whatsinmycloset.core.repository.SwapRepository
import org.greenthread.whatsinmycloset.features.tabs.swap.Action.SwapAction
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState

class SwapViewModel(
    private val swapRepository: SwapRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SwapListState())
    val state =_state
        .onStart {
            fetchSwapData("1")
        }

    fun onAction(action: SwapAction) {
        when (action) {
            is SwapAction.OnSwapClick -> {

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
                            getResults = getResults
                        )
                    }
                }
                .onError { error ->
                    println("FETCHSWAP API ERROR ${error}")
                    _state.update {
                        it.copy(
                            getResults = emptyList(),
                            isLoading = false
                        )
                    }
                }
        }
    }
}
