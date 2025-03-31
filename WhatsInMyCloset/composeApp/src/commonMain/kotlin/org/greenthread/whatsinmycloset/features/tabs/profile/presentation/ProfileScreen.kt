package org.greenthread.whatsinmycloset.features.tabs.profile.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.dto.MessageUserDto
import org.greenthread.whatsinmycloset.core.dto.OtherSwapDto
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.core.dto.toOtherSwapDto
import org.greenthread.whatsinmycloset.features.tabs.profile.ConfirmationType
import org.greenthread.whatsinmycloset.features.tabs.profile.ProfileTabViewModel
import org.greenthread.whatsinmycloset.features.tabs.profile.data.FriendshipStatus
import org.greenthread.whatsinmycloset.features.tabs.swap.Action.SwapAction
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.accept_button
import whatsinmycloset.composeapp.generated.resources.add_friend_button
import whatsinmycloset.composeapp.generated.resources.available_swap_title
import whatsinmycloset.composeapp.generated.resources.cancel_request_button
import whatsinmycloset.composeapp.generated.resources.content_description_user_avatar
import whatsinmycloset.composeapp.generated.resources.decline_button
import whatsinmycloset.composeapp.generated.resources.defaultUser
import whatsinmycloset.composeapp.generated.resources.error_no_user_data
import whatsinmycloset.composeapp.generated.resources.my_outfits_title
import whatsinmycloset.composeapp.generated.resources.remove_friend_button

@Composable
fun ProfileScreen(
    userId: Int,
    profileViewModel: ProfileTabViewModel,
    swapViewModel: SwapViewModel,
    navController: NavController,
    onAllSwapClick: () -> Unit,
    onSwapClick: (OtherSwapDto) -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val state by profileViewModel.state.collectAsStateWithLifecycle()
    val swapState by swapViewModel.state.collectAsStateWithLifecycle(
        initialValue = SwapListState(),
        lifecycle = lifecycle
    )

    LaunchedEffect(userId) {
         println("Profile Screen User Id $userId")
         profileViewModel.loadProfile(userId)
         if (swapState.getSearchedUserSwapResults.isEmpty() ||
             swapState.searchedUserInfo?.id != userId) {
             swapViewModel.fetchSwapData(userId.toString())
         }
    }

    // Clear search results when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            profileViewModel.clearSearchResults()
        }
    }

    Scaffold { padding ->
        when {
            state.isLoading -> FullScreenLoading()
            state.error != null -> Text("Error: ${state.error}") // Simple error display
            state.user != null -> ProfileContent(
                user = state.user!!,
                isOwnProfile = state.isOwnProfile,
                viewModel = profileViewModel,
                navController = navController,
                modifier = Modifier.padding(padding),
                onAllSwapClick = onAllSwapClick,
                onSwapClick = onSwapClick,
                swapState = swapState
            )
            else -> Text(stringResource(Res.string.error_no_user_data)) // Fallback UI
        }

        // Confirmation dialog for destructive actions
        FriendActionConfirmationDialog(profileViewModel)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProfileContent(
    user: User,
    isOwnProfile: Boolean,
    onAllSwapClick: () -> Unit,
    onSwapClick: (OtherSwapDto) -> Unit,
    viewModel: ProfileTabViewModel,
    swapState: SwapListState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            ProfileHeader(user)

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SwapStats(
                    user = user,
                    isOwnProfile = isOwnProfile,
                    modifier = Modifier.weight(1f),
                    swapState = swapState
                )

                    if (isOwnProfile) {
                        FriendsCount(
                            friendsCount = user.friends?.size ?: 0,
                            onClick = {
                                navController.navigate(Routes.UserFriendsScreen)
                            },
                            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally))

                        Button(onClick = {
                            navController.navigate(Routes.UserSearchScreen)
                        }) {
                            Text("Find Users")
                        }
                    } else {
                        ProfileActions(
                            viewModel = viewModel,
                            targetUserId = user.id,
                            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally))
                    }
            }

            Spacer(Modifier.height(16.dp))

            ProfileRowSection(
                isOwnProfile = isOwnProfile,
                title = Res.string.available_swap_title,
                state = swapState,
                onAction = { action ->
                    when (action) {
                        is SwapAction.OnSwapClick -> {
                            val isCurrentUserItem = swapState.getUserSwapResults.any { it.itemId.id == action.itemId }

                            if (isCurrentUserItem) {
                                val selectedItem = swapState.getUserSwapResults.find { it.itemId.id == action.itemId }
                                if (selectedItem != null) {
                                    val currentUserDto = MessageUserDto(
                                        id = user.id!!,
                                        name = user.name,
                                        username = user.name,
                                        profilePicture = user.profilePicture
                                    )
                                    onSwapClick(selectedItem.toOtherSwapDto(user = currentUserDto))
                                }
                            } else {
                                val selectedItem = swapState.getSearchedUserSwapResults.find { it.itemId.id == action.itemId }
                                if (selectedItem != null && user.id != null) {
                                    onSwapClick(selectedItem.toOtherSwapDto(user = MessageUserDto(id = user.id, name = user.name, profilePicture = user.profilePicture)))
                                }
                            }
                        }
                        else -> Unit
                    }
                },
                onSeeAll = onAllSwapClick
            )
        }

        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
            ) {
                OutfitSectionTitle(Res.string.my_outfits_title)
            }
        }

        item {
            PostsSection(
                userId = user.id,
                navController = navController,
            )
        }
    }
}

@Composable
fun UserSearchResult(
    user: UserDto?,
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
            if (user != null) {
                AsyncImage(
                    model = user.profilePicture ?: "",
                    contentDescription = stringResource(Res.string.content_description_user_avatar),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    error = painterResource(Res.drawable.defaultUser)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                if (user != null) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                if (user != null) {
                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    user: User,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfilePicture(user.profilePicture)
        Username(user.name ?: "No username found", user.username ?: "No username found")
    }
}

@Composable
private fun SwapStats(
    user: User,
    isOwnProfile: Boolean,
    modifier: Modifier,
    swapState: SwapListState
) {
    val swapCount = if (isOwnProfile) {
        swapState.getUserSwapResults.size
    } else {
        swapState.getSearchedUserSwapResults.size
    }

    SwapsCount(
        swapsCount = swapCount,
        modifier = modifier
    )
}

@Composable
fun ProfileActions(
    viewModel: ProfileTabViewModel,
    targetUserId: Int?,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Row(modifier = modifier) {
        when (state.friendshipStatus) {
            FriendshipStatus.NOT_FRIENDS -> {
                Button(
                    onClick = {
                        targetUserId?.let { viewModel.sendFriendRequest(it) }
                    },
                    enabled = !state.isLoading && targetUserId != null
                ) {
                    Text(stringResource(Res.string.add_friend_button))
                }
            }

            FriendshipStatus.PENDING -> {
                TextButton(
                    onClick = { targetUserId?.let { viewModel.showConfirmation(ConfirmationType.CancelRequest, it) } },
                    enabled = !state.isLoading && targetUserId != null,
                    colors = ButtonDefaults.textButtonColors(
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.size(20.dp))
                    } else {
                        Text(stringResource(Res.string.cancel_request_button))
                    }
                }
            }

            FriendshipStatus.REQUEST_RECEIVED -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.respondToRequest(true) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        enabled = !state.isLoading
                    ) {
                        Text(stringResource(Res.string.accept_button))
                    }

                    OutlinedButton(
                        onClick = { viewModel.showConfirmation(ConfirmationType.DeclineRequest, targetUserId!!) },
                        enabled = !state.isLoading
                    ) {
                        Text(stringResource(Res.string.decline_button))
                    }
                }
            }

            FriendshipStatus.FRIENDS -> {
                OutlinedButton(
                    onClick = {
                        targetUserId?.let { viewModel.showConfirmation(ConfirmationType.RemoveFriend, it) }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    enabled = !state.isLoading && targetUserId != null
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.size(20.dp))
                    } else {
                        Text(stringResource(Res.string.remove_friend_button))
                    }
                }
            }
        }
    }
}