package org.greenthread.whatsinmycloset.features.tabs.swap.State
import androidx.compose.material3.SearchBarColors
import org.greenthread.whatsinmycloset.core.dto.OtherSwapDto
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.UserDto

data class SwapListState(
    val isLoading: Boolean = true,
    val getAllSwapResults: List<SwapDto> = emptyList(),
    val getUserSwapResults: List<SwapDto> = emptyList(),
    val getOtherUserSwapResults: List<OtherSwapDto> = emptyList(),
    val getSearchedUserSwapResults: List<SwapDto> = emptyList()
)
