package org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
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
import io.ktor.client.network.sockets.ConnectTimeoutException
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.dto.MessageUserDto
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.features.tabs.swap.data.MessageListState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.viewmodel.koinViewModel
import whatsinmycloset.composeapp.generated.resources.Res

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
    val otherUserIdInt = otherUserId.toInt()

    if (currentUserId != null) {
        LaunchedEffect(state) {
            try {
                if (state.getChatHistory.isEmpty()) {
                    viewModel.fetchChatHistory(currentUserId, otherUserIdInt)
                }
//                if (state.getOtherUserInfo == null) {
//                    viewModel.fetchUserById(otherUserIdInt)
//                }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
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

                Spacer(modifier = Modifier.width(8.dp))

//                state.getOtherUserInfo?.let { user ->
//                    ChatTitle(user)
//                }

            }


            ChatList(
                modifier = Modifier.weight(1f),
                currentUserId = currentUserId,
                messages = state.getChatHistory
            )
            MessageInput { messageContent ->
                viewModel.sendMessage(currentUserId, otherUserIdInt, messageContent)
            }

        }
    }
}

//@Composable
//fun ChatTitle(user: UserDto){
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .fillMaxWidth()
//
//    ) {
//        @OptIn(ExperimentalResourceApi::class) // TEMP for /drawable image
//        (AsyncImage(
//        model = user.profilePicture ?: Res.getUri("drawable/defaultUser.png"), // TODO: REMOVE drawable
//        contentDescription = "Profile Image",
//        modifier = Modifier
//            .size(50.dp)
//            .clip(CircleShape)
//            .border(1.dp, Color.LightGray, CircleShape)
//    ))
//        Spacer(modifier = Modifier.width(8.dp))
//        Column {
//            Text(
//                text = user.username,
//                fontWeight = FontWeight.Bold,
//                fontSize = 26.sp,
//                color = Color.Black
//            )
//        }
//    }
//}

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
