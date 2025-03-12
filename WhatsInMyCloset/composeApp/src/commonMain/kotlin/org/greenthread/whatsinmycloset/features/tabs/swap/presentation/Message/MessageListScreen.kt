package org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.MessageManager
import org.greenthread.whatsinmycloset.features.tabs.swap.data.MessageListState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MessageListScreen(
    viewModel: MessageViewModel = koinViewModel(),
    navController: NavController,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = MessageListState(),
        lifecycle = lifecycle
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
        ) {
            TextButton(
                onClick = { navController.popBackStack() }
            ) {
                Text(
                    text = "Back",
                    fontSize = 15.sp,
                    color = Color.Blue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            state.getLatestMessageResults.isEmpty() -> {
                Text(text = "No Swap Message", modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .fillMaxSize()
                        .padding(top = 60.dp)

                ) {
                    items(state.getLatestMessageResults) { message ->
                        val isSender =
                            message.sender.id == (viewModel.currentUser.value?.id ?: true)
                        val otherUser = if (isSender) message.receiver else message.sender

                        MessageList(
                            user = otherUser,
                            lastMessage = message.content,
                            isUnread = !message.isRead && message.sender.id == otherUser.id,
                            onClick = {
                                viewModel.updateRead(message.id)
                                MessageManager.setCurrentOtherUser(otherUser)
                                navController.navigate(Routes.ChatScreen)
                            }
                        )
                    }
                }
            }
        }
    }
}
