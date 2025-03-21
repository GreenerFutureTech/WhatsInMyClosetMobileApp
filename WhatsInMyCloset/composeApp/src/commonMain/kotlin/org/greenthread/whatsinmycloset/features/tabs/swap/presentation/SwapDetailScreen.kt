package org.greenthread.whatsinmycloset.features.tabs.swap.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import io.ktor.client.network.sockets.ConnectTimeoutException
import org.greenthread.whatsinmycloset.core.domain.models.MessageManager
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.dto.SwapDto
import org.greenthread.whatsinmycloset.core.dto.toMessageUserDto
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.greenthread.whatsinmycloset.theme.primaryLight
import org.greenthread.whatsinmycloset.theme.secondaryLight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.complete_swap_dialog_message
import whatsinmycloset.composeapp.generated.resources.complete_swap_dialog_title
import whatsinmycloset.composeapp.generated.resources.delete_button
import whatsinmycloset.composeapp.generated.resources.delete_swap_dialog_message
import whatsinmycloset.composeapp.generated.resources.delete_swap_dialog_title

@Composable
fun SwapDetailScreen(
    swap: SwapDto?,
    onBackClick: () -> Unit,
    onRequestClick: () -> Unit
) = swap?.let {
    val viewModel: SwapViewModel = koinViewModel()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = SwapListState(),
        lifecycle = lifecycle
    )

    val currentUser = viewModel.currentUser
    var menuExpanded by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        try {
                viewModel.getSwapUser(swap.userId)

        } catch (e: ConnectTimeoutException) {
            println("Connection timeout occurred (could not hit backend?): ${e.message}")
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }

    val swapUser = state.swapUserInfoResults.toMessageUserDto()
    var profileLoadFailed by remember { mutableStateOf(false) }
    LaunchedEffect(swapUser.profilePicture) {
        profileLoadFailed = false
    }
    WhatsInMyClosetTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (swap.userId == currentUser.value?.id) {
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.widthIn(min = 130.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(Res.string.complete_swap_dialog_title),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            onClick = {
                                showCompleteDialog = true
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(Res.string.delete_button),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            onClick = {
                                showDeleteDialog = true
                                menuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        if (showCompleteDialog) {
            AlertDialog(
                onDismissRequest = { showCompleteDialog = false },
                title = {
                    Text(
                        text = stringResource(Res.string.complete_swap_dialog_title)
                    )
                },
                text = {
                    Text(
                        text = stringResource(Res.string.complete_swap_dialog_message)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.updateSwap(swap.itemId.id)
                            onBackClick()
                            showCompleteDialog = false
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCompleteDialog = false }) {
                        Text("No")
                    }
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text(
                        text = stringResource(Res.string.delete_swap_dialog_title)
                    )
                },
                text = {
                    Text(
                        text = stringResource(Res.string.delete_swap_dialog_message)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteSwap(swap.itemId.id)
                            onBackClick()
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("No")
                    }
                }
            )
        }


        Spacer(modifier = Modifier.height(10.dp))


        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()

        ) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .align(Alignment.Center)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {

                    @OptIn(ExperimentalResourceApi::class)
                    AsyncImage(
                        model = if (profileLoadFailed) Res.getUri("drawable/defaultUser.png") else swapUser.profilePicture,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, secondaryLight, CircleShape),
                        onError = { profileLoadFailed = true },
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "user${swap.userId}",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))

                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                        .padding(2.dp)
                        .align(Alignment.CenterHorizontally),
                ) {
                    var swapLoadFailed by remember { mutableStateOf(false) }
                    @OptIn(ExperimentalResourceApi::class)
                    AsyncImage(
                        model = if (swapLoadFailed) Res.getUri("drawable/noImage.png") else swap.itemId.mediaUrl,
                        contentDescription = "Swap Image",
                        modifier = Modifier.fillMaxSize(),
                        onError = { swapLoadFailed = true }
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = swap.itemId.brand,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Size: ${swap.itemId.size}",
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Condition: ${swap.itemId.condition}",
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    if (swap.userId != currentUser.value?.id) {
                        MessageManager.setCurrentOtherUser(swapUser)

                        OutlinedButton(
                            onClick = {onRequestClick()},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .height(40.dp),
                            border = BorderStroke(2.dp, primaryLight)
                        ) {
                            Text(
                                text = "Swap Request",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
} ?: Text("No swap data available")