package org.greenthread.whatsinmycloset.features.tabs.swap.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyGridColourBox
import org.greenthread.whatsinmycloset.core.ui.components.listItems.generateRandomItems
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.Action.SwapAction
import org.greenthread.whatsinmycloset.features.tabs.swap.dto.SwapDto
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SwapScreenRoot(
    viewModel: SwapViewModel = koinViewModel(),
    onSwapClick: (SwapDto) -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = SwapListState(),
        lifecycle = lifecycle
    )

    LaunchedEffect(state) {
        if (state.getResults.isEmpty()) {
            viewModel.fetchSwapData("1") // test user 1
        }
    }
    SwapScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SwapAction.OnSwapClick -> {
                    val selectedItem = state.getResults.find { it.itemId == action.itemId }
                    if (selectedItem != null) {
                        onSwapClick(selectedItem)
                    }
                }
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun SwapScreen(
    state: SwapListState,
    onAction: (SwapAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(modifier = Modifier.height(48.dp), fontSize = 30.sp, text = "SWAP")

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
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.height(25.dp),
                fontSize = 25.sp,
                text = "My Swap Items"
            )

            TextButton(
                onClick = {
                    println("Button clicked!")
                },
                modifier = Modifier.height(50.dp)
            ) {
                Text(
                    text = "All Swaps",
                    fontSize = 15.sp,
                    color = Color.Blue
                )
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(state.getResults) { index, item ->
                SwapImageCard(
                    imageUrl = item.mediaUrl,
                    onSwapClick = {
                        onAction(SwapAction.OnSwapClick(item.itemId))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.height(30.dp),
            fontSize = 25.sp,
            text = "Followers and Nearby Items"
        )

        TextField(
            value = "SEARCH ...",
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
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

        val otherRandomSwaps = generateRandomItems(6)
        LazyGridColourBox(items = otherRandomSwaps)
    }
}


@Composable
fun SwapImageCard(imageUrl: String, onSwapClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(100.dp)
            .padding(8.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(10.dp))
            .clickable { onSwapClick() }
    ) {
        Text(
            text = imageUrl,
            modifier = Modifier
                .align(Alignment.Center),
            fontWeight = FontWeight.SemiBold
        )
    }
}



