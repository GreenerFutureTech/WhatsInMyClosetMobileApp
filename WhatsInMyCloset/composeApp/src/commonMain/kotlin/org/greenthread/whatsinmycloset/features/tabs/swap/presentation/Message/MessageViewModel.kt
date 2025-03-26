package org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.domain.getOrNull
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.domain.onError
import org.greenthread.whatsinmycloset.core.domain.onSuccess
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.core.repository.ClosetRepository
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.NotificationEventBus
import org.greenthread.whatsinmycloset.features.tabs.swap.data.MessageListState
import org.greenthread.whatsinmycloset.features.tabs.swap.domain.SwapEventBus

class MessageViewModel(
    private val swapRepository: ClosetRepository,
    userManager: UserManager
) : ViewModel() {
    val currentUser: StateFlow<User?> = userManager.currentUser

    private val _state = MutableStateFlow(MessageListState())
    val state = _state
        .onStart {
            fetchMessageList()
        }

    init {
        fetchMessageList()
    }


    @Serializable
    data class UnreadResponse(val hasUnread: Boolean)

    fun checkForUnreadNotifications(userId: Int?) {
        if (userId == null) return

        viewModelScope.launch {
            val unread = swapRepository.getUnread(userId)
            println("FETCH UNREAD RESPONSE: $unread")

            if (unread.getOrNull() != null) {
                val json = Json.decodeFromString<UnreadResponse>(unread.getOrNull().toString()).hasUnread
                println("FETCH UNREAD HERE : ${json}")
                SwapEventBus.setHasNewNotifications(json)
            }
        }
    }

    fun fetchMessageList() {
        viewModelScope.launch {
            if (currentUser.value == null) {
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
                    .getLatestMessage(currentUser.value?.id.toString())
                    .onSuccess { getResults ->
                        println("FETCH MESSAGE LIST API success: $getResults")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                getLatestMessageResults = getResults
                            )
                        }
                    }
                    .onError { error ->
                        println("FETCH MESSAGE LIST API ERROR: $error")
                        _state.update {
                            it.copy(
                                getLatestMessageResults = emptyList(),
                                isLoading = false,
                            )
                        }
                    }
            }
        }

    }

    fun fetchChatHistory(userId: Int, otherUserId: Int) {
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

    fun sendMessage(senderId: Int, receiverId: Int, content: String) {
        viewModelScope.launch {

            println("SEND MESSAGE : send message from $senderId to user $receiverId")
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            swapRepository
                .sendMessage(senderId, receiverId, content)
                .onSuccess { getResults ->
                    println("SEND MESSAGE API success: $getResults")
                    fetchChatHistory(senderId, receiverId)
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
                .onError { error ->
                    println("SEND MESSAGE API ERROR ${error}")
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun updateRead(messageId: Int) {
        viewModelScope.launch {

            println("UPDATE READ : message ${messageId} marked as read")
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            swapRepository
                .updateRead(messageId)
                .onSuccess { getResults ->
                    println("UPDATE READ API success: $getResults")
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
                .onError { error ->
                    println("UPDATE READ API ERROR ${error}")
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
        }
    }
}


