package org.greenthread.whatsinmycloset.features.tabs.profile.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import org.greenthread.whatsinmycloset.core.dto.toOtherSwapDto
import org.greenthread.whatsinmycloset.core.ui.components.controls.SearchBar
import org.greenthread.whatsinmycloset.features.tabs.profile.ProfileTabViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.Action.SwapAction
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel
import org.jetbrains.compose.resources.painterResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.add_friend_button
import whatsinmycloset.composeapp.generated.resources.available_swap_title
import whatsinmycloset.composeapp.generated.resources.defaultUser
import whatsinmycloset.composeapp.generated.resources.my_outfits_title

@Composable
fun ProfileScreen(
    userId: Int,
    profileViewModel: ProfileTabViewModel,
    swapViewModel: SwapViewModel,
    navController: NavController,
    onAllSwapClick: () -> Unit,
    onSwapClick: (OtherSwapDto) -> Unit
) {
    val state by profileViewModel.state.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val swapState by swapViewModel.state.collectAsStateWithLifecycle(
        initialValue = SwapListState(),
        lifecycle = lifecycle
    )

     LaunchedEffect(userId) {
        if (state.user?.id != userId) { // Only load if different user
            profileViewModel.loadProfile(userId)
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
                onFollowClick = {},
                viewModel = profileViewModel,
                navController = navController,
                modifier = Modifier.padding(padding),
                onAllSwapClick = onAllSwapClick,
                onSwapClick = onSwapClick,
                swapState = swapState
            )
            else -> Text("No user data available") // Fallback UI
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    isOwnProfile: Boolean,
    onFollowClick: () -> Unit,
    onAllSwapClick: () -> Unit,
    onSwapClick: (OtherSwapDto) -> Unit,
    viewModel: ProfileTabViewModel,
    swapState: SwapListState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResult.collectAsState()

    Column(modifier
        .verticalScroll(rememberScrollState())
        .fillMaxWidth()
        .padding(16.dp)
    ) {
        ProfileHeader(user, isOwnProfile, onFollowClick)

        Spacer(Modifier.height(16.dp))

        if (isOwnProfile) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SearchBar(
                    searchString = searchQuery,
                    onSearchStringChange = { viewModel.updateSearchQuery(it) },
                    onSearch = { viewModel.searchUser() },
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                Button(onClick = {
                    if (searchResults != null) {
                        viewModel.clearSearchResults()
                    } else {
                        viewModel.searchUser()
                    }
                }) {
                    Text(if (searchResults != null) "Clear" else "Search")
                }
            }

            if (state.isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            state.error?.takeIf { state.isSearching }?.let { error ->
                Text(
                    text = "Search failed: ${error.take(50)}", // Limiting error length
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            AnimatedVisibility(
                visible = searchResults != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                searchResults?.let { foundUser ->
                    UserSearchResult(
                        user = foundUser,
                        onClick = {
                            // Navigate to other user's profile
                            navController.navigate(Routes.ProfileDetailsScreen(foundUser?.id!!))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        ProfileRowSection(
            title = Res.string.available_swap_title,
            state = swapState,
            onAction = { action ->
                when (action) {
                    is SwapAction.OnSwapClick -> {
                        val isCurrentUserItem = swapState.getUserSwapResults.any { it.itemId.id == action.itemId }

                        if (isCurrentUserItem) {
                            val selectedItem = swapState.getUserSwapResults.find { it.itemId.id == action.itemId }
                            if (selectedItem != null) {
                                val currentUserDto = MessageUserDto()
                                onSwapClick(selectedItem.toOtherSwapDto(user = currentUserDto))
                            }
                        } else {
                            val selectedItem = swapState.getOtherUserSwapResults.find { it.swap.itemId.id == action.itemId }
                            if (selectedItem != null) {
                                onSwapClick(selectedItem)
                            }
                        }
                    }
                    else -> Unit
                }
            },
            onSeeAll = onAllSwapClick
        )

        OutfitSectionTitle(Res.string.my_outfits_title)

    }
}

@Composable
private fun UserSearchResult(
    user: User,
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
            AsyncImage(
                model = user.profilePicture ?: "",
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                error = painterResource(Res.drawable.defaultUser)
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    user: User,
    isOwnProfile: Boolean,
    onFollowClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfilePicture(user)
                Username(user.name ?: "No username found", user.username ?: "No username found")
            }

            if (user.type == "Super") {
                UserBadge()
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileStats(user, isOwnProfile)
        }
    }
}

@Composable
private fun ProfileStats(
    user: User,
    isOwnProfile: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SwapsCount(
            onClick = {},
            swapsCount = 10,
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )

        if (isOwnProfile) {
            FriendsCount(
                friendsCount = user.friends?.size ?: 0,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        } else {
            ManageFriendButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onClick = {},
                action = Res.string.add_friend_button
            )
        }
    }
}