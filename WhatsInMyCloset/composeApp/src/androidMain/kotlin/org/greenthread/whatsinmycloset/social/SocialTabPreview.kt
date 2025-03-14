package org.greenthread.whatsinmycloset.social

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.features.tabs.social.SocialTabScreen

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SocialTab() {
    var showContent by remember { mutableStateOf(false) }
    val fakeUser = User(
        id = 100,
        username = "UserTest1",
        email = "testmail",
        firebaseUuid = "",
        name = "Carla G",
        lastLogin = "01-01-2025",
        registeredAt = "01-01-2025",
        updatedAt = "01-01-2025"
    )
    SocialTabScreen(user = fakeUser, onNavigate = {})
}

