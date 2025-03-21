package org.greenthread.whatsinmycloset.features.tabs.swap.State
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.UserDto

data class SwapListState(
    val isLoading: Boolean = true,
    val getAllSwapResults: List<SwapDto> = emptyList(),
    val getUserSwapResults: List<SwapDto> = emptyList(),
    val getOtherUserSwapResults: List<SwapDto> = emptyList(),
    val swapUserInfoResults: UserDto = UserDto()
)
