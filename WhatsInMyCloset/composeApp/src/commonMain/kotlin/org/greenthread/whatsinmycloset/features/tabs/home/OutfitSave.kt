package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.Calendar
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.utilities.DateUtils
import org.greenthread.whatsinmycloset.core.viewmodels.ClothingItemViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.OutfitViewModel
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme

// Save outfit with selects tags and add to calendar
@Composable
fun OutfitSaveScreen(
    navController: NavController,
    onExit: () -> Unit,
    onDone: () -> Unit,
    outfitViewModel: OutfitViewModel,
    clothingItemViewModel: ClothingItemViewModel
) {
    WhatsInMyClosetTheme {
        val isOutfitSaved by outfitViewModel.isOutfitSaved.collectAsState()
        val tags by outfitViewModel.tags.collectAsState()
        val selectedTags by outfitViewModel.selectedTags.collectAsState()
        val isPublic by outfitViewModel.isPublic.collectAsState()

        // retrieve the current outfit user wants to save
        val currentOutfit by outfitViewModel.currentOutfit.collectAsState()
        val outfitName by outfitViewModel.outfitName.collectAsState()

        var showCreateTagDialog by remember { mutableStateOf(false) }
        var showCalendarDialog by remember { mutableStateOf(false) }
        var showCalendarConfirmationDialog by remember { mutableStateOf(false) }
        var showDiscardDialog by remember { mutableStateOf(false) }
        var showOutfitNameDialog by remember { mutableStateOf(false) }


        if (isOutfitSaved) {

            OutfitSaved(
                navController = navController,
                onDismiss = {

                    // Navigate back to the Home Tab
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                viewModel = outfitViewModel,
                outfitName = outfitName,
                clothingItemViewModel = clothingItemViewModel
            )
        }
        else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                item {
                    // Heading for the screen
                    OutfitScreenHeader(
                        onExit = { showDiscardDialog = true },
                        title = "Select Tags"
                    )
                }
                items(tags.toList())
                { tag ->
                    // Determine if the tag is selected based on local UI state
                    val isSelected = selectedTags.contains(tag)

                    // Use SwipeToDeleteTag with both swipe-to-delete and click-to-select functionality
                    SelectOrDeleteTag(
                        tag = tag,
                        isSelected = isSelected,
                        onDelete = {
                            outfitViewModel.removeTag(tag) // Remove the tag
                        },
                        onClick = {
                            outfitViewModel.updateSelectedTags(tag) // Toggle tag selection
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Calculate if the Done button should be enabled
                    val isDoneEnabled = selectedTags.isNotEmpty() || isPublic

                    // Footer with Done button
                    OutfitScreenFooter(
                        onDone = {
                            println("Done button clicked") // Debugging statement
                            // Get current outfit from the viewmodel
                            currentOutfit?.let { outfit ->
                                println("Saving outfit: $outfit with tags ${selectedTags}") // Debugging statement
                                if(selectedTags.isNotEmpty())
                                {
                                    // pop up to ask user if they would like to add outfit to calendar
                                    showCalendarConfirmationDialog = true
                                }
                            }
                            onDone() // Trigger the onDone callback
                        },
                        isDoneEnabled = isDoneEnabled
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Button to open the "Create New Tag" dialog
                    Button(
                        onClick = { showCreateTagDialog = true},
                        modifier = Modifier.width(210.dp)
                    ) {
                        Text("Create New Outfit Tag")
                    }
                }
            }   // end of Lazy Column

            // Get outfit name from user before saving
            if (showOutfitNameDialog) {
                OutfitNameDialog(
                    showDialog = showOutfitNameDialog,
                    onDismiss = {
                        showOutfitNameDialog = false
                    },
                    onSave = { name ->
                        outfitViewModel.viewModelScope.launch {
                            val success = outfitViewModel.saveOutfit(
                                selectedTags = selectedTags.toList(),
                                outfitName = name
                            )
                        }
                        showOutfitNameDialog = false
                    }
                )
            }

            // Show CreateNewOutfitTag when the button is clicked
            if (showCreateTagDialog) {
                CreateNewOutfitTag(
                    onConfirm = {
                        showCreateTagDialog = false // Close the dialog
                    },
                    onDismiss = { showCreateTagDialog = false },
                    viewModel = outfitViewModel
                )
            }

            // Calendar Confirmation Dialog
            if (showCalendarConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showCalendarConfirmationDialog = false },
                    title = { Text("Add to Calendar?") },
                    text = { Text("Would you like to add this outfit to calendar?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showCalendarConfirmationDialog = false
                                showCalendarDialog = true
                            }
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showCalendarConfirmationDialog = false
                                // If not adding to calendar, just get outfit name
                                showOutfitNameDialog = true
                            }
                        ) {
                            Text("No")
                        }
                    }
                )
            }

            // Show Calendar Dialog
            if (showCalendarDialog) {
                OutfitDatePicker(
                    onDismiss = { showCalendarDialog = false },
                    outfitViewModel = outfitViewModel,
                    clothingItemViewModel = clothingItemViewModel,
                    selectedTags = selectedTags,
                    navController = navController
                )
            }

            // Show discard confirmation dialog when "x" is clicked
            if (showDiscardDialog) {

                DiscardSavingDialog(
                    onConfirm = {
                        showDiscardDialog = false
                        // Discard the current outfit and create a new one
                        outfitViewModel.discardCurrentOutfit()
                        outfitViewModel.clearOutfitState() // Clear the outfit state
                        clothingItemViewModel.clearClothingItemState() // Clear the selected items state
                        navController.navigate(Routes.HomeTab) // Navigate to Home Tab
                    },
                    onDismiss = { showDiscardDialog = false }
                )
            }
        } // end of else block
    } // end of WhatsInMyClosetTheme
}

@Composable
fun DiscardSavingDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancel saving outfit") },
        text = { Text("Are you sure you want to discard the outfit?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Composable
fun OutfitNameDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    initialName: String = ""
) {
    var outfitName by remember { mutableStateOf(initialName) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Name Your Outfit") },
            text = {
                TextField(
                    value = outfitName,
                    onValueChange = { outfitName = it },
                    label = { Text("Outfit Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (outfitName.isNotBlank()) {
                            onSave(outfitName)
                        }
                    },
                    enabled = outfitName.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun CreateNewOutfitTag(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: OutfitViewModel
) {
    var newTagName by remember { mutableStateOf("") }
    val maxLength = 20 // Maximum allowed characters for the tag name

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "New Outfit Tag",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = newTagName,
                    onValueChange = { if (it.length <= maxLength) newTagName = it },
                    label = { Text("Tag Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    trailingIcon = {
                        if (newTagName.isNotEmpty()) {
                            IconButton(onClick = { newTagName = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    }
                )

                Text(
                    text = "${newTagName.length}/$maxLength",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Create Button
                Button(
                    onClick = {
                        if (newTagName.isNotBlank()) {
                            viewModel.addNewTag(newTagName)
                            onConfirm(newTagName) // Notify the caller that the operation is complete
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = newTagName.isNotBlank()
                ) {
                    Text("Create")
                }

                // Cancel Button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}


@Composable
fun OutfitSaved(
    navController: NavController,
    onDismiss: () -> Unit, // Callback to close the dialog and go back to the Home Tab
    outfitName: String,
    viewModel: OutfitViewModel,
    clothingItemViewModel: ClothingItemViewModel
) {
    var showDialog by remember { mutableStateOf(true) }

    // Retrieve folder names and isPublic state from the ViewModel
    val tagNames by viewModel.selectedTags.collectAsState()

    // Sort the tag names alphabetically
    val sortedTagNames = tagNames.toList().sorted()

    // Format the tag names with "and" before the last one
    val tagNamesText = when (sortedTagNames.size) {
        0 -> ""
        1 -> sortedTagNames[0]
        else -> {
            val allButLast = sortedTagNames.dropLast(1).joinToString(", ")
            val last = sortedTagNames.last()
            "$allButLast and $last"
        }
    }

    // Check if the dialog should be shown
    if (showDialog) {
        val msgStr = if (tagNamesText.isNotEmpty()) {
            "$outfitName has been saved with the following tag(s): $tagNamesText."
        } else {
            "Your outfit has been saved."
        }

        AlertDialog(
            onDismissRequest = {
                showDialog = false // Close dialog
                onDismiss() // Call the provided dismiss callback
            },
            title = { Text(text = "Outfit Saved") },
            text = {
                Text(
                    text = msgStr
                )
            },
            confirmButton = {
                Button(onClick = {
                    // clear outfit state for next outfit
                    viewModel.clearOutfitState()
                    clothingItemViewModel.clearClothingItemState()

                    showDialog = false // Close dialog
                    navController.navigate(Routes.HomeTab) // Navigate to Home
                }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun dpToPx(dp: Dp): Float {
    val density = LocalDensity.current.density
    return dp.value * density
}

@Composable
fun SelectOrDeleteTag(
    tag: String,
    isSelected: Boolean,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    var swipeOffset by remember { mutableStateOf(0f) }
    val swipeThreshold = dpToPx(100.dp) // Swipe threshold to trigger delete
    val maxSwipeOffset = dpToPx(200.dp) // Maximum swipe offset

    // Animate the swipe offset for smooth transitions
    val animatedOffset by animateFloatAsState(
        targetValue = swipeOffset,
        animationSpec = tween(durationMillis = 1000)
    )

    // Calculate the background color based on swipeOffset
    val backgroundColor = if (swipeOffset <= -swipeThreshold)
    {
        Color.Red   // Fully red
    }
    else if (swipeOffset < 0)
    {
        Color.Red.copy(alpha = -swipeOffset / swipeThreshold) // Gradually transition to red
    }
    else
    {
        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        else MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Tag content
        Box(
            modifier = Modifier
                .offset(x = animatedOffset.dp)
                .fillMaxWidth()
                .background(
                    backgroundColor, // Use dynamic background color
                    RoundedCornerShape(8.dp)
                )
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            // Snap back if the swipe doesn't exceed the threshold
                            if (swipeOffset > -swipeThreshold) {
                                swipeOffset = 0f
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            val scaledDragAmount = dragAmount * 0.5f // Adjust this value to control swipe speed

                            swipeOffset = (swipeOffset + scaledDragAmount)
                                .coerceIn(-maxSwipeOffset, 0f)
                            if (swipeOffset <= -swipeThreshold) {
                                onDelete() // Trigger delete if swiped beyond the threshold
                                swipeOffset = 0f // Reset offset after deletion
                            }
                        }
                    )
                }
                .clickable { onClick() } // Handle click for selection
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = tag,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}