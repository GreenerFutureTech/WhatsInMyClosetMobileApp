package org.greenthread.whatsinmycloset.features.tabs.swap.State
import org.greenthread.whatsinmycloset.features.tabs.swap.dto.SwapDto

data class SwapListState(
    val isLoading: Boolean = true,
    val getResults: List<SwapDto> = emptyList()
)
