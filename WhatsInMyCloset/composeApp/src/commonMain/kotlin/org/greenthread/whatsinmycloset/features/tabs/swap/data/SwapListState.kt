package org.greenthread.whatsinmycloset.features.tabs.swap.State
import org.greenthread.whatsinmycloset.core.dto.OtherUserSwapDto
import org.greenthread.whatsinmycloset.core.dto.SwapDto

data class SwapListState(
    val isLoading: Boolean = true,
    val getAllSwapResults: List<SwapDto> = emptyList(),
    val getUserSwapResults: List<SwapDto> = emptyList(),
    val getOtherUserSwapResults: List<OtherUserSwapDto> = emptyList()
)
