package org.greenthread.whatsinmycloset.features.tabs.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.Friend
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.features.tabs.profile.ProfileTabViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.content_description_user_avatar
import whatsinmycloset.composeapp.generated.resources.defaultUser

@Composable
fun UserFriendsScreen(
    profileViewModel: ProfileTabViewModel,
    navController: NavController
) {

    val state by profileViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if (state.user?.friends == null) {
            profileViewModel.loadUserFriends()
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Text("Error loading friends: ${state.error}")
                }
                state.user?.friends.isNullOrEmpty() -> {
                    Text("No friends found", modifier = Modifier.padding(16.dp))
                }
                else -> {
                    println("FRIENDS ${state.user?.friends}")
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        items(state.user?.friends ?: emptyList()) { friend ->
                            FriendList(
                                friend = friend,
                                onClick = {
                                    navController.navigate(Routes.ProfileDetailsScreen(friend.id!!))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FriendList(
    friend: Friend?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Adjusted padding
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            if (friend != null) {
                AsyncImage(
                    model = friend.profilePicture ?: "",
                    contentDescription = stringResource(Res.string.content_description_user_avatar),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    error = painterResource(Res.drawable.defaultUser)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                if (friend != null) {
                    Text(
                        text = friend.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                if (friend != null) {
                    Text(
                        text = "@${friend.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}