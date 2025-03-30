package org.greenthread.whatsinmycloset.features.tabs.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.ui.components.listItems.SwapImageCard
import org.greenthread.whatsinmycloset.core.ui.components.posts.PostCard
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.SeeAllButton
import org.greenthread.whatsinmycloset.features.tabs.profile.ConfirmationType
import org.greenthread.whatsinmycloset.features.tabs.profile.ProfileTabViewModel
import org.greenthread.whatsinmycloset.features.tabs.social.data.PostState
import org.greenthread.whatsinmycloset.features.tabs.social.presentation.EmptyState
import org.greenthread.whatsinmycloset.features.tabs.social.presentation.PostViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.Action.SwapAction
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState
import org.greenthread.whatsinmycloset.theme.outlineVariantLight
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.cancel
import whatsinmycloset.composeapp.generated.resources.cancel_request_button
import whatsinmycloset.composeapp.generated.resources.cancel_request_dialog
import whatsinmycloset.composeapp.generated.resources.confirm
import whatsinmycloset.composeapp.generated.resources.content_description_user_avatar
import whatsinmycloset.composeapp.generated.resources.decline_request_dialog
import whatsinmycloset.composeapp.generated.resources.decline_request_type
import whatsinmycloset.composeapp.generated.resources.defaultUser
import whatsinmycloset.composeapp.generated.resources.friends_count_label
import whatsinmycloset.composeapp.generated.resources.my_outfits_title
import whatsinmycloset.composeapp.generated.resources.no_items_found
import whatsinmycloset.composeapp.generated.resources.remove_friend_button
import whatsinmycloset.composeapp.generated.resources.remove_friend_dialog
import whatsinmycloset.composeapp.generated.resources.swaps_count_label

@Composable
fun ProfilePicture(profilePicture: String?) {
    AsyncImage(
        model = profilePicture?: "",
        contentDescription = stringResource(Res.string.content_description_user_avatar),
        modifier = Modifier
            .size(60.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape),
        contentScale = ContentScale.Crop,
        error = painterResource(Res.drawable.defaultUser)
    )
}

@Composable
fun Username(name: String, username: String) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(start = 16.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "@$username",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FriendsCount(
    friendsCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = modifier
        )
        {
            Text(
                text = friendsCount.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(Res.string.friends_count_label),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun SwapsCount(
    swapsCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = "$swapsCount",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(Res.string.swaps_count_label),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun OutfitSectionTitle(title: StringResource) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileRowSection(
    isOwnProfile: Boolean,
    title: StringResource? = null,
    showSeeAll: Boolean = true,
    state: SwapListState,
    onAction: (SwapAction) -> Unit,
    onSeeAll: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        title?.let {
            Text(
                stringResource(title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }
        if (showSeeAll) {
            SeeAllButton { onSeeAll() }
        }
    }

    val swapResults = if (isOwnProfile) state.getUserSwapResults else state.getSearchedUserSwapResults

    if (swapResults.isEmpty()) {
        Box(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.no_items_found),
                fontWeight = FontWeight.Medium,
                color = outlineVariantLight
            )

        }
    } else {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(100.dp)
        ) {
            itemsIndexed(swapResults) { _, item ->
                SwapImageCard(
                    onSwapClick = {
                        onAction(SwapAction.OnSwapClick(item.itemId.id))
                    },
                    imageUrl = item.itemId.mediaUrl
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@Composable
fun FriendActionConfirmationDialog(
    viewModel: ProfileTabViewModel
) {
    val (type, userId) = viewModel.showConfirmationDialog.collectAsState().value ?: return

    val (title, message) = when (type) {
        ConfirmationType.RemoveFriend ->
            stringResource(Res.string.remove_friend_button) to stringResource(Res.string.remove_friend_dialog)
        ConfirmationType.CancelRequest ->
            stringResource(Res.string.cancel_request_button) to stringResource(Res.string.cancel_request_dialog)
        ConfirmationType.DeclineRequest ->
            stringResource(Res.string.decline_request_type) to stringResource(Res.string.decline_request_dialog)
    }

    AlertDialog(
        onDismissRequest = viewModel::dismissConfirmation,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = {
                when (type) {
                    ConfirmationType.RemoveFriend -> viewModel.removeFriend(userId)
                    ConfirmationType.CancelRequest -> viewModel.cancelRequest(userId)
                    ConfirmationType.DeclineRequest -> viewModel.respondToRequest(false)
                }
                viewModel.dismissConfirmation()
            }) {
                Text(stringResource(Res.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = viewModel::dismissConfirmation) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}

@Composable
fun PostsSection(
    userId: Int?,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: PostViewModel = koinViewModel()
    val currentUser = viewModel.currentUser.value
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = PostState(),
        lifecycle = lifecycle
    )

    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.fetchUserOutfits(userId)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutfitSectionTitle(Res.string.my_outfits_title)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    state.outfits.isEmpty() -> {
                        EmptyState()
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(state.outfits) { outfit ->
                                PostCard(
                                    outfit = outfit,
                                    currentUser = currentUser,
                                    modifier = Modifier.padding(4.dp),
                                    onPostClick = {
                                        navController.navigate(
                                            Routes.SocialDetailsScreen(
                                                outfit.outfitId
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}