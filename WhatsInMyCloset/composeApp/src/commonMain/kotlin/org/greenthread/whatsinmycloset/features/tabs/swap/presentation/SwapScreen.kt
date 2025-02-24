package org.greenthread.whatsinmycloset.features.tabs.swap.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.Action.SwapAction
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.ui.components.listItems.SwapImageCard
import org.greenthread.whatsinmycloset.core.ui.components.listItems.SwapOtherImageCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SwapScreenRoot(
    viewModel: SwapViewModel = koinViewModel(),
    onSwapClick: (SwapDto) -> Unit,
    onAllSwapClick: () -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = SwapListState(),
        lifecycle = lifecycle
    )

    LaunchedEffect(state) {
        if (state.getAllSwapResults.isEmpty()) {
            viewModel.fetchAllSwapData()
        }
        if (state.getUserSwapResults.isEmpty()) {
            viewModel.fetchSwapData("1") // NEED TO UPDATE : current user id
        }
        if (state.getOtherUserSwapResults.isEmpty()) {
            viewModel.fetchOtherSwapData("1") // NEED TO UPDATE : current user id
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
            viewModel.onAction(action)
        },
        onAllSwapClick = onAllSwapClick
    )
}

@Composable
fun SwapScreen(
    state: SwapListState,
    onAction: (SwapAction) -> Unit,
    onAllSwapClick: () -> Unit
) {
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
                text = "SWAP"
            )

            Icon(
                imageVector = Icons.Default.MailOutline,
                contentDescription = "Messages",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colors.primary
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                text = "My Swap Items"
            )

            TextButton(
                onClick = {onAllSwapClick()},
                modifier = Modifier
            ) {
                Text(
                    text = "All Swaps",
                    fontSize = 15.sp,
                    color = Color.Blue
                )
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            itemsIndexed(state.getUserSwapResults) { index, item ->
                SwapImageCard(
                    onSwapClick = {
                        onAction(SwapAction.OnSwapClick(item.itemId.id))
                    }
                )
               Spacer(modifier = Modifier.width(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.height(30.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            text = "Followers and Nearby Items"
        )

        TextField(
            value = "SEARCH ...",
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text(text = "hint") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {

                }
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // Set the number of columns to 3
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(state.getOtherUserSwapResults) { index, item ->
                SwapOtherImageCard(
                    onSwapClick = {
                        onAction(SwapAction.OnSwapClick(item.itemId.id))
                    },
                    imageUrl = item.itemId.mediaUrl,
                    username = "user${item.userId}" // NEED TO UPDATE : username
                )
            }
        }
    }
}

