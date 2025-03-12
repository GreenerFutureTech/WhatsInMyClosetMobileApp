package org.greenthread.whatsinmycloset.social

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.features.tabs.profile.ProfileTabScreen

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun ProfileTab() {
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

    val fakeUserState = MutableStateFlow<User?>(fakeUser) // Fake state for preview

    ProfileTabScreen(userState = fakeUserState, onNavigate = {})}