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
        val outfitFolders by outfitViewModel.outfitFolders.collectAsState()
        val selectedFolder by outfitViewModel.selectedFolder.collectAsState()
        val selectedFolders by outfitViewModel.selectedFolders.collectAsState()
        val isPublic by outfitViewModel.isPublic.collectAsState()

        // retrieve the current outfit user wants to save
        val currentOutfit by outfitViewModel.currentOutfit.collectAsState()

        var showCreateFolderDialog by remember { mutableStateOf(false) }
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
                        // Determine if folder is selected (either one folder or multiple)
                        val isSelected = if (selectedFolders.isNotEmpty()) {
                            selectedFolders.contains(folder) || (isPublic && folder == "Public Outfits")
                        } else {
                            selectedFolder == folder || (isPublic && folder == "Public Outfits")
                        }

                        println(isSelected)
                        println(selectedFolder)

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
                                    if (selectedFolders.isNotEmpty() != null) {
                                        outfitViewModel.updateSelectedFolders(folder)
                                    } else {
                                        outfitViewModel.updateSelectedFolder(if (selectedFolder == folder) null
                                        else folder)
                                    }
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

                // Footer with Done button
                OutfitScreenFooter(
                    onDone = {
                        println("Done button clicked") // Debugging statement
                        // Get current outfit from the viewmodel
                        currentOutfit?.let { outfit ->
                            println("Saving outfit: $outfit") // Debugging statement
                            if(selectedFolders.isNotEmpty())
                            {
                                outfitViewModel.saveOutfit(outfit, selectedFolders,
                                    null)
                            }
                            else
                            {
                                outfitViewModel.saveOutfit(outfit, null,
                                    selectedFolder)
                            }
                        }
                        onDone() // Trigger the onDone callback
                    },
                    isDoneEnabled = selectedFolders.isNotEmpty() || selectedFolder != null
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Button to open the "Create New Folder" dialog
                Button(
                    onClick = { showCreateFolderDialog = true},
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("+ Create New Outfit Folder")
                }

                // Show CreateNewOutfitFolder when the button is clicked
                if (showCreateFolderDialog) {
                    CreateNewOutfitFolder(
                        onDismiss = { showCreateFolderDialog = false },
                        onCreate = { folderName ->
                            outfitViewModel.addFolder(folderName) // Update repository
                            showCreateFolderDialog = false // Close the dialog after creating the folder
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
fun CreateNewOutfitFolder(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var newFolderName by remember { mutableStateOf("") }

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
                Text("New Outfit Folder", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    label = { Text("Folder Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Save Button
                Button(
                    onClick = {
                        if (newFolderName.isNotBlank()) {
                            onCreate(newFolderName) // Pass the new folder name
                            newFolderName = "" // Reset input
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create")
                }

                Spacer(modifier = Modifier.height(8.dp))

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
    val folderNames by viewModel.selectedFolders.collectAsState()
    val isPublic by viewModel.isPublic.collectAsState()

    // Check if the dialog should be shown
    if (showDialog) {
        // Generate the message about the folders
        val folderNamesText = folderNames.joinToString(", ")
        val publicText = if (isPublic) "Your outfit is now public." else ""

        AlertDialog(
            onDismissRequest = {
                showDialog = false // Close dialog
                onDismiss() // Call the provided dismiss callback
            },
            title = { Text(text = "Outfit Saved") },
            text = {
                Text(
                    text = "Your outfit has been saved in the following folder(s): " +
                            "$folderNamesText. $publicText"
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