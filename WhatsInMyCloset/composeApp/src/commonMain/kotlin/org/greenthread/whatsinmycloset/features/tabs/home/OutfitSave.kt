package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.viewmodels.ClothingItemViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.OutfitViewModel
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme

// Save outfit to selected folder(s)
@Composable
fun OutfitSaveScreen(
    navController: NavController,
    onExit: () -> Unit,
    onDone: () -> Unit,
    /* this viewmodel handles the following:
    currentOutfit
    creating new folders (updating the outfit repository)
    selectedFolder state
    selectedFolders state (when user wants to save outfit in more than 1 folder)
    isPublic state (when user wants the outfit to be public)
    */
    outfitViewModel: OutfitViewModel,
    clothingItemViewModel: ClothingItemViewModel
) {
    WhatsInMyClosetTheme {
        val isOutfitSaved by outfitViewModel.isOutfitSaved.collectAsState()
        val outfitFolders by outfitViewModel.outfitTags.collectAsState()
        //val selectedTags by outfitViewModel.tags.collectAsState()
        //val selectedTag by outfitViewModel.tag.collectAsState()
        val isPublic by outfitViewModel.isPublic.collectAsState()

        // retrieve the current outfit user wants to save
        val currentOutfit by outfitViewModel.currentOutfit.collectAsState()

        // Local UI state for highlighting tags
        val (selectedTags, setSelectedTags) = remember { mutableStateOf(setOf<String>()) }
        var showCreateTagDialog by remember { mutableStateOf(false) }
        var showDiscardDialog by remember { mutableStateOf(false) }

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
                clothingItemViewModel = clothingItemViewModel
            )
        }
        else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                // Heading for the selected category
                OutfitScreenHeader(
                    onGoBack = { navController.popBackStack() },
                    onExit = { showDiscardDialog = true },
                    title = "Save Your Outfit"
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(outfitFolders.toList()) { folder ->
                        // Determine if folder is selected based on local UI state
                        val isSelected = selectedTags.contains(folder)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    // Update local UI state for highlighting
                                    val updatedTags = if (selectedTags.contains(folder)) {
                                        selectedTags - folder // Remove folder from the set
                                    } else {
                                        selectedTags + folder // Add folder to the set
                                    }
                                    setSelectedTags(updatedTags) // Directly update the state
                                }
                                .padding(16.dp)
                        ) {
                            Text(text = folder, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Public Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Checkbox(
                        checked = isPublic,
                        onCheckedChange = { outfitViewModel.toggleIsPublic(it) }
                    )
                    Text(text = "Public")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Calculate if the Done button should be enabled
                val isDoneEnabled = selectedTags.isNotEmpty() || isPublic

                // Footer with Done button
                OutfitScreenFooter(
                    onDone = {
                        println("Done button clicked") // Debugging statement
                        // Get current outfit from the viewmodel
                        currentOutfit?.let { outfit ->
                            println("Saving outfit: $outfit") // Debugging statement
                            if(selectedTags.isNotEmpty())
                            {
                                // update the selected tags
                                outfitViewModel.updateSelectedTags(selectedTags.toString())
                                outfitViewModel.saveOutfit(outfit, selectedTags.toList(), null)
                            }
                        }
                        onDone() // Trigger the onDone callback
                        setSelectedTags(emptySet()) // Clear local UI state
                    },
                    isDoneEnabled = isDoneEnabled
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Button to open the "Create New Folder" dialog
                Button(
                    onClick = { showCreateTagDialog = true},
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("+ Create New Outfit Tag")
                }

                // Show CreateNewOutfitTag when the button is clicked
                if (showCreateTagDialog) {
                    CreateNewOutfitTag(
                        onDismiss = { showCreateTagDialog = false },
                        onCreate = { tagName ->
                            outfitViewModel.addTag(tagName) // Update repository
                            showCreateTagDialog = false // Close the dialog after creating the folder
                        }
                    )
                }

                // Show discard confirmation dialog when "x" is clicked
                if (showDiscardDialog) {

                    DiscardSavingDialog(
                        onConfirm = {
                            showDiscardDialog = false
                            navController.navigate(Routes.HomeTab) // Navigate to Home Tab
                        },
                        onDismiss = { showDiscardDialog = false }
                    )
                }
            }
        } // end of else block
    }
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
fun CreateNewOutfitTag(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var newTagName by remember { mutableStateOf("") }

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
                Text("New Outfit Tag", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(
                    value = newTagName,
                    onValueChange = { newTagName = it },
                    label = { Text("Tag Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Save Button
                Button(
                    onClick = {
                        if (newTagName.isNotBlank()) {
                            onCreate(newTagName) // Pass the new folder name
                            newTagName = "" // Reset input
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create")
                }

                // Cancel Button
                TextButton(onClick = onDismiss) {
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
    viewModel: OutfitViewModel,
    clothingItemViewModel: ClothingItemViewModel
) {
    var showDialog by remember { mutableStateOf(true) }

    // Retrieve folder names and isPublic state from the ViewModel
    val tagNames by viewModel.tags.collectAsState()

    var tagNamesText = ""

    // Check if the dialog should be shown
    if (showDialog) {
        // Generate the message about the folders
        if(tagNames.isNotEmpty())
        {
            tagNamesText = tagNames.joinToString(", ")
        }

        var msgStr: String = tagNamesText
        if(tagNames.isNotEmpty()) {
            msgStr = "Your outfit has been saved with following tag(s) ${tagNamesText}."
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