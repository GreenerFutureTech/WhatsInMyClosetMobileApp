package org.greenthread.whatsinmycloset.swap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.core.ui.components.controls.SearchBar
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message.MessageList
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect


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
            user = UserDto(
                id = 1,
                username = "johndoe",
                email = "johndoe@example.com",
                name = "John Doe",
                firebaseUid = "firebase-uid-123",
                profilePicture = "https://fastly.picsum.photos/id/853/200/200.jpg?hmac=f4LF-tVBBnJb9PQAVEO8GCTGWgLUnxQLw44rUofE6mQ",
                registeredAt = "2024-01-01T00:00:00Z",
                updatedAt = "2024-01-02T00:00:00Z",
                lastLogin = "2024-02-01T12:00:00Z"
            ),
            lastMessage = "Hello! How are you?",
            onClick = {}
        )
    }
}