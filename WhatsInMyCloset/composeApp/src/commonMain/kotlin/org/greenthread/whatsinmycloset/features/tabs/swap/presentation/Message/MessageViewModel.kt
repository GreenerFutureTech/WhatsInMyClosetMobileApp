package org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.domain.onSuccess
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.core.repository.ClosetRepository
import org.greenthread.whatsinmycloset.features.tabs.swap.data.MessageListState

class MessageViewModel(
    private val swapRepository: ClosetRepository
) : ViewModel() {
    val currentUser = UserManager.currentUser
    val userId = currentUser?.id ?: throw IllegalStateException("User ID is null")

    private val _state = MutableStateFlow(MessageListState())
    val state = _state
        .onStart {
            fetchMessageList()
        }

    fun fetchMessageList() {
        viewModelScope.launch {
            if (currentUser == null) {
                println("FETCH MESSAGE LIST: Current user is null")
                _state.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }else {
                println("FETCH MESSAGE LIST: Fetching All Messages")
                _state.update {
                    it.copy(
                        isLoading = true
                    )
                }

                swapRepository
                    .getLatestMessage(currentUser.id.toString())
                    .onSuccess { getResults ->
                        println("FETCH MESSAGE LIST API success: $getResults")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                getAllMessageResults = getResults
                            )
                        }
                    }
                    .onError { error ->
                        println("FETCH MESSAGE LIST API ERROR: $error")
                        _state.update {
                            it.copy(
                                getAllMessageResults = emptyList(),
                                isLoading = false,
                            )
                        }
                    }
            }
        }

    }

    fun fetchChatHistory(userId: Int, otherUserId: String) {
        viewModelScope.launch {
            println("FETCHCHAT : Fetching chat data for user $userId and user $otherUserId")
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            swapRepository
                .getChatHistory(userId, otherUserId)
                .onSuccess { getResults ->
                    println("FETCHCHAT API success: $getResults")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            getChatHistory = getResults
                        )
                    }
                }
                .onError { error ->
                    println("FETCHCHAT API ERROR ${error}")
                    _state.update {
                        it.copy(
                            getChatHistory = emptyList(),
                            isLoading = false
                        )
                    }
                }
        }
    }
}


