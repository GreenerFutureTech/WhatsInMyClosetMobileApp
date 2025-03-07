package org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.ktor.client.network.sockets.ConnectTimeoutException
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.features.tabs.swap.data.MessageListState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatScreen(
    viewModel: MessageViewModel = koinViewModel(),
    otherUserId: String,
    navController: NavController
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = MessageListState(),
        lifecycle = lifecycle
    )
    val currentUserId = UserManager.currentUser?.id

    if (currentUserId != null) {
        LaunchedEffect(state) {
            try {
                if (state.getChatHistory.isEmpty()) {
                    viewModel.fetchChatHistory(currentUserId, otherUserId)
                }
            } catch (e: ConnectTimeoutException) {
                println("Connection timeout occurred (could not hit backend?): ${e.message}")
            } catch (e: Exception) {
                println("An error occurred: ${e.message}")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp),
        ) {
            TextButton(
                onClick = { navController.popBackStack() },
            ) {
                Text(
                    text = "Back",
                    fontSize = 15.sp,
                    color = Color.Blue
                )
            }


            ChatList(
                modifier = Modifier.weight(1f),
                currentUserId = currentUserId,
                messages = state.getChatHistory
            )
            MessageInput()

        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(8f)
                .padding(end = 8.dp)
                .heightIn(min = 50.dp),
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 18.sp
            ),
            placeholder = { Text("Let's Swap!") },
            singleLine = false
        )

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onSendMessage(text)
                    text = ""
                }
            },
            modifier = Modifier
                .weight(2f)
                .height(50.dp)
                .padding(start = 8.dp),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
