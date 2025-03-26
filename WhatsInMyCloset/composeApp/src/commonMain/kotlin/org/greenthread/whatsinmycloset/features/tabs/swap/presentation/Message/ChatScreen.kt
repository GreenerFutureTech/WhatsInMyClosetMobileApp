package org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.MessageManager
import org.greenthread.whatsinmycloset.core.dto.MessageUserDto
import org.greenthread.whatsinmycloset.features.screens.notifications.presentation.NotificationsViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.data.MessageListState
import org.greenthread.whatsinmycloset.features.tabs.swap.domain.SwapEventBus
import org.greenthread.whatsinmycloset.theme.outlineLight
import org.greenthread.whatsinmycloset.theme.secondaryLight
import org.greenthread.whatsinmycloset.theme.surfaceDimLight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.no_items_found
import whatsinmycloset.composeapp.generated.resources.swap_placeholder

@Composable
fun ChatScreen(
    viewModel: MessageViewModel = koinViewModel(),
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = MessageListState(),
        lifecycle = lifecycle
    )
    val currentUserId = viewModel.currentUser.value?.id
    val otherUser = MessageManager.currentOtherUser

    // Listen for new message events
    val coroutineScope = rememberCoroutineScope()
    val newNotificationState by SwapEventBus.newNotificationEvent.collectAsState(initial = null)

    // Trigger refresh when new notification arrives
    LaunchedEffect(newNotificationState) {
        newNotificationState?.let { _ ->
            if (currentUserId != null && otherUser != null) {
                coroutineScope.launch {
                    try {
                        // Refresh chat history when a new notification is received
                        viewModel.fetchChatHistory(currentUserId, otherUser.id)
                    } catch (e: Exception) {
                        println("MESSAGE SCREEN REFRESH ERROR: ${e.message}")
                    }
                }
            }
        }
    }

    if (currentUserId != null && otherUser != null) {
        val otherUserId = otherUser.id

        LaunchedEffect(state) {
            try {
                if (state.getChatHistory.isEmpty()) {
                    viewModel.fetchChatHistory(currentUserId, otherUserId)
                }
            } catch (e: Exception) {
                println("MESSAGE SCREEN ERROR: ${e.message}")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ChatTitle(
                    user = otherUser,
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = surfaceDimLight
            )

            ChatList(
                modifier = Modifier.weight(1f),
                currentUserId = currentUserId,
                messages = state.getChatHistory
            )
            MessageInput { messageContent ->
                viewModel.sendMessage(currentUserId, otherUserId, messageContent)
            }
        }
    }
}

@Composable
fun ChatTitle(
    user: MessageUserDto,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        var loadFailed by remember { mutableStateOf(false) }
        @OptIn(ExperimentalResourceApi::class)
        AsyncImage(
            model = if(loadFailed) Res.getUri("drawable/defaultUser.png") else user.profilePicture,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, secondaryLight, CircleShape),
            onError = { loadFailed = true }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = user.username,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = Color.Black
            )
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
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(8f)
                .padding(end = 8.dp)
                .heightIn(min = 40.dp),
            shape = RoundedCornerShape(100),
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),
            placeholder = { Text(stringResource(Res.string.swap_placeholder)) },
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
                .height(40.dp)
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
