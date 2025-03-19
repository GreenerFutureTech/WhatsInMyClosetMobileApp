package org.greenthread.whatsinmycloset.features.tabs.swap.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.ktor.client.network.sockets.ConnectTimeoutException
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.Action.SwapAction
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.core.ui.components.controls.SearchBar
import org.greenthread.whatsinmycloset.core.ui.components.listItems.SwapImageCard
import org.greenthread.whatsinmycloset.core.ui.components.listItems.SwapOtherImageCard
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.greenthread.whatsinmycloset.theme.onSurfaceLight
import org.greenthread.whatsinmycloset.theme.outlineVariantLight
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.see_all_button
import whatsinmycloset.composeapp.generated.resources.no_items_found
import whatsinmycloset.composeapp.generated.resources.my_swap_item
import whatsinmycloset.composeapp.generated.resources.friends_items
import whatsinmycloset.composeapp.generated.resources.swap_tab_title

@Composable
fun SwapScreenRoot(
    viewModel: SwapViewModel = koinViewModel(),
    onSwapClick: (SwapDto) -> Unit,
    onAllSwapClick: () -> Unit,
    onMessageClick: () -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = SwapListState(),
        lifecycle = lifecycle
    )

    val currentUser = viewModel.currentUser

    WhatsInMyClosetTheme {
        LaunchedEffect(state) {
            try {
                if (state.getAllSwapResults.isEmpty()) {
                    viewModel.fetchAllSwapData()
                }
                if (state.getUserSwapResults.isEmpty()) {
                    viewModel.fetchSwapData(currentUser.value?.id.toString())
                }
                if (state.getOtherUserSwapResults.isEmpty()) {
                    viewModel.fetchOtherSwapData(currentUser.value?.id.toString())
                }
            } catch (e: ConnectTimeoutException) {
                println("Connection timeout occurred (could not hit backend?): ${e.message}")
            } catch (e: Exception) {
                println("An error occurred: ${e.message}")
            }
        }

        SwapScreen(
            state = state,
            onAction = { action ->
                when (action) {
                    is SwapAction.OnSwapClick -> {
                        val selectedItem = state.getAllSwapResults.find { it.itemId.id == action.itemId }
                    if (selectedItem != null) {
                        onSwapClick(selectedItem)
                    }
                }
                else -> Unit
            }
           // viewModel.onAction(action)
        },
        onAllSwapClick = onAllSwapClick,
        onMessageClick = onMessageClick
        )
    }
}

@Composable
fun SwapScreen(
    state: SwapListState,
    onAction: (SwapAction) -> Unit,
    onAllSwapClick: () -> Unit,
    onMessageClick: () -> Unit
) {
    var searchString by remember { mutableStateOf("") }

    val matchingSwaps = state.getOtherUserSwapResults.filter { swap ->
        val query = searchString.lowercase()
        swap.itemId.brand.lowercase().contains(query) ||
                swap.itemId.itemType.lowercase().contains(query) ||
                swap.itemId.tags.any { it.lowercase().contains(query) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.height(48.dp),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                text = stringResource(Res.string.swap_tab_title)
            )

            Icon(
                imageVector = Icons.Default.MailOutline,
                contentDescription = "Messages",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onMessageClick() }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                text = stringResource(Res.string.my_swap_item)
            )

            TextButton(
                onClick = { onAllSwapClick() },
                modifier = Modifier
            ) {
                Text(
                    text = stringResource(Res.string.see_all_button),
                    fontSize = 15.sp
                )
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(120.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(80.dp)
                        .height(95.dp)
                        .border(1.dp, onSurfaceLight, RoundedCornerShape(8.dp))
                        .clickable { println("Add button clicked") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "+",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = outlineVariantLight
                    )
                }
            }
            itemsIndexed(state.getUserSwapResults) { index, item ->
                SwapImageCard(
                    onSwapClick = {
                        onAction(SwapAction.OnSwapClick(item.itemId.id))
                    },
                    imageUrl = item.itemId.mediaUrl
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Text(
                modifier = Modifier.height(30.dp),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                text = stringResource(Res.string.friends_items)
            )

            SearchBar(
                searchString = searchString,
                onSearchStringChange = { searchString = it },
                onSearch = {},
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.getOtherUserSwapResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(Res.string.no_items_found),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = outlineVariantLight
                )
            }
        } else {
            val displayedSwaps = if (searchString.isEmpty()) state.getOtherUserSwapResults else matchingSwaps

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(displayedSwaps) { index, item ->
                    SwapOtherImageCard(
                        onSwapClick = {
                            onAction(SwapAction.OnSwapClick(item.itemId.id))
                        },
                        imageUrl = item.itemId.mediaUrl,
                        username = "user${item.userId}" // TODO: update to username
                    )
                }
            }
        }
    }
}

