package org.greenthread.whatsinmycloset.swap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.greenthread.whatsinmycloset.core.dto.MessageDto
import org.greenthread.whatsinmycloset.core.dto.MessageUserDto
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.core.ui.components.controls.SearchBar
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message.MessageInput
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message.MessageItem
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message.MessageList


@Preview(showBackground = true)
@Composable
private fun SearchBarPreview() {

    SearchBar(
        searchString = "Zara",
        onSearchStringChange = {},
        onSearch = {},
        modifier = Modifier
            .fillMaxWidth()

    )

}

@Preview(showBackground = true, name = "MessageList")
@Composable
fun PreviewMessageList() {

    MaterialTheme {
        MessageList(
            user = MessageUserDto(
                id = 1,
                username = "johndoe",
                name = "John Doe",
                profilePicture = "https://fastly.picsum.photos/id/853/200/200.jpg?hmac=f4LF-tVBBnJb9PQAVEO8GCTGWgLUnxQLw44rUofE6mQ",
            ),
            lastMessage = "Hello! How are you?",
            isUnread = true,
            onClick = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMessageItem() {
    val message = MessageDto(
        id = 3,
        sender = MessageUserDto(
            id = 1,
            username = "John Doe",
            name = "test",
            profilePicture = "https://fastly.picsum.photos/id/853/200/200.jpg?hmac=f4LF-tVBBnJb9PQAVEO8GCTGWgLUnxQLw44rUofE6mQ"
        ),
        receiver = MessageUserDto(
            id = 3,
            username = "Veronica",
            name = "Veronica Price",
            profilePicture = "https://fastly.picsum.photos/id/237/200/200.jpg?hmac=TmmQSbShHz9CdQm0NkEjx1Dyh_Y984R9LpNrpvH2D_U"
        ),
        content = "Hello there!",
        sentAt = "2025-03-05T18:21:49.486Z",
        isRead = false
    )

    MaterialTheme {
        Column {

            MessageItem(
                message = message,
                isSender = true
            )

            MessageItem(
                message = message,
                isSender = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMessageInput() {
    MaterialTheme {
        MessageInput(

        )
    }
}

