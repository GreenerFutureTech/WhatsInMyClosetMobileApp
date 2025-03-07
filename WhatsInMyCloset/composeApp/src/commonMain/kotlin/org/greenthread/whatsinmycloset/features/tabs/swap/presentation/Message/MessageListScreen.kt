package org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.dto.UserDto
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(
                text = "Back",
                fontSize = 15.sp,
                color = Color.Blue
            )
        }


        Spacer(modifier = Modifier.height(16.dp))
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }
            state.getAllMessageResults.isEmpty() -> {
                Text(text = "No Swap Message")
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.getAllMessageResults) { message ->
                        val isSender = message.sender.id == viewModel.currentUser?.id
                        val otherUser = if (isSender) message.receiver else message.sender

                        MessageList(
                            user = otherUser,
                            lastMessage = message.content,
                            onClick = {
                                navController.navigate(Routes.ChatScreen(otherUser.id.toString()))
                            }
                        )

                    }
                }
            }
        }
    }
}