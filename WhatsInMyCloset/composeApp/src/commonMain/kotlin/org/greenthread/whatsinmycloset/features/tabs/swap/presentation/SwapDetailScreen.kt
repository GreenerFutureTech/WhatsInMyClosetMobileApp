package org.greenthread.whatsinmycloset.features.tabs.swap.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import org.greenthread.whatsinmycloset.core.dto.MessageUserDto
import org.greenthread.whatsinmycloset.core.dto.OtherSwapDto
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.greenthread.whatsinmycloset.theme.outlineVariantLight
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
import whatsinmycloset.composeapp.generated.resources.item_size
import whatsinmycloset.composeapp.generated.resources.item_condition
import whatsinmycloset.composeapp.generated.resources.item_brand
import whatsinmycloset.composeapp.generated.resources.item_name

@Composable
fun SwapDetailScreen(
    swap: OtherSwapDto?,
    onBackClick: () -> Unit,
    onRequestClick: (OtherSwapDto) -> Unit
) = swap?.let {
    val viewModel: SwapViewModel = koinViewModel()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = SwapListState(),
        lifecycle = lifecycle
    )
    val swapItem = swap.swap
    var swapUser = swap.user
    val currentUser = viewModel.currentUser

    val isSearchUser = swapUser.id != currentUser.value?.id
    if (isSearchUser) {
        viewModel.getUser(swapUser.id)

        val searchedUserInfo = state.searchedUserInfo
        swapUser = if (searchedUserInfo != null) {
            MessageUserDto(
                id = swapUser.id,
                username = searchedUserInfo.username,
                profilePicture = searchedUserInfo.profilePicture
            )
        } else {
            swapUser
        }
    }

    var menuExpanded by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var profileLoadFailed by remember { mutableStateOf(false) }


    LaunchedEffect(swapUser.profilePicture) {
        profileLoadFailed = false
    }

    WhatsInMyClosetTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (swapItem.userId == currentUser.value?.id) {
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
                                viewModel.updateSwap(swapItem.itemId.id)
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
                                viewModel.deleteSwap(swapItem.itemId.id)
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
                        if (swapItem.userId != currentUser.value?.id) {
                            @OptIn(ExperimentalResourceApi::class)
                            AsyncImage(
                                model = if (profileLoadFailed || swapUser.profilePicture == null) Res.getUri(
                                    "drawable/defaultUser.png"
                                ) else swapUser.profilePicture,
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, secondaryLight, CircleShape),
                                onError = { profileLoadFailed = true },
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = swapUser.username ?: "unknown user",
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(50.dp))
                        }

                    }

                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                            .padding(2.dp)
                            .align(Alignment.CenterHorizontally),
                    ) {
                        var swapLoadFailed by remember { mutableStateOf(false) }
                        @OptIn(ExperimentalResourceApi::class)
                        AsyncImage(
                            model = if (swapLoadFailed) Res.getUri("drawable/noImage.png") else swapItem.itemId.mediaUrl,
                            contentDescription = "Swap Image",
                            modifier = Modifier.fillMaxSize(),
                            onError = { swapLoadFailed = true }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(bottom = 4.dp)) {
                            Text(
                                text = stringResource(Res.string.item_name),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = swapItem.itemId.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )

                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 1.dp,
                            color = outlineVariantLight
                        )

                        Column(modifier = Modifier.padding(bottom = 4.dp)) {
                            Text(
                                text = stringResource(Res.string.item_brand),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = swapItem.itemId.brand,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )

                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 1.dp,
                            color = outlineVariantLight
                        )

                        Column(modifier = Modifier.padding(bottom = 4.dp)) {
                            Text(
                                text = stringResource(Res.string.item_size),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                text = swapItem.itemId.size,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 1.dp,
                            color = outlineVariantLight
                        )

                        Column(modifier = Modifier.padding(top = 4.dp)) {
                            Text(
                                text = stringResource(Res.string.item_condition),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                text = swapItem.itemId.condition,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(top = 12.dp),
                            thickness = 1.dp,
                            color = outlineVariantLight
                        )

                        Spacer(modifier = Modifier.height(30.dp))

                        if (swapItem.userId != currentUser.value?.id) {
                            swapUser.id = swapItem.userId
                            MessageManager.setCurrentOtherUser(swapUser)

                            OutlinedButton(
                                onClick = { onRequestClick(swap) },
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
    }
} ?: Text("No swap data available")