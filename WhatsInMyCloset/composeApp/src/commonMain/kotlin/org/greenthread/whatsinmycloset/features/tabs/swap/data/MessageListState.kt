package org.greenthread.whatsinmycloset.features.tabs.swap.data

import org.greenthread.whatsinmycloset.core.dto.MessageDto
import org.greenthread.whatsinmycloset.core.dto.UserDto

data class MessageListState(
    val isLoading: Boolean = true,
    val getLatestMessageResults: List<MessageDto> = emptyList(),
    val getUserInfo: UserDto? = null,
    val getChatHistory: List<MessageDto> = emptyList()
)
