package org.greenthread.whatsinmycloset.features.tabs.profile.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.ui.components.listItems.SwapImageCard
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.SeeAllButton
import org.greenthread.whatsinmycloset.features.tabs.swap.Action.SwapAction
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState
import org.greenthread.whatsinmycloset.theme.outlineVariantLight
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.content_description_user_avatar
import whatsinmycloset.composeapp.generated.resources.defaultUser
import whatsinmycloset.composeapp.generated.resources.friends_count_label
import whatsinmycloset.composeapp.generated.resources.no_items_found
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = friendsCount.toString(),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Friends",
                style = MaterialTheme.typography.bodySmall
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
            itemsIndexed(swapResults) { index, item ->
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