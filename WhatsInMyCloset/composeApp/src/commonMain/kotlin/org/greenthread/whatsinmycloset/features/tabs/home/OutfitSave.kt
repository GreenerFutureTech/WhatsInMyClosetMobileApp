package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.repositories.OutfitRepository
import org.greenthread.whatsinmycloset.core.viewmodels.OutfitViewModel

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
    viewModel: OutfitViewModel
) {
    val isOutfitSaved by viewModel.isOutfitSaved.collectAsState()
    val outfitFolders by viewModel.outfitFolders.collectAsState()
    val selectedFolder by viewModel.selectedFolder.collectAsState()
    val selectedFolders by viewModel.selectedFolders.collectAsState()
    val isPublic by viewModel.isPublic.collectAsState()

    // retrieve the current outfit user wants to save
    val currentOutfit by viewModel.currentOutfit.collectAsState()

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
            viewModel = viewModel
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
                items(outfitFolders) { folder ->
                    // Determine if folder is selected (either one folder or multiple)
                    val isSelected = if (selectedFolders.isNotEmpty()) {
                        selectedFolders.contains(folder) || (isPublic && folder == "My Public Outfits")
                    } else {
                        selectedFolder == folder || (isPublic && folder == "My Public Outfits")
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
                                    viewModel.updateSelectedFolders(folder)
                                } else {
                                    viewModel.updateSelectedFolder(if (selectedFolder == folder) null
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
                    onCheckedChange = { viewModel.toggleIsPublic(it) }
                )
                Text(text = "Public")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer with Done button
            OutfitScreenFooter(
                onDone = {
                    // Get current outfit from the viewmodel
                    // Save the current outfit
                    currentOutfit?.let { outfit ->
                        viewModel.saveOutfit(outfit)
                    }
                    onDone()
                         },
                    isDoneEnabled = selectedFolders.isNotEmpty() || selectedFolder != null
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Button to open the "Create New Folder" dialog
            Button(
                onClick = { showCreateFolderDialog = true },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("+ Create New Outfit Folder")
            }

            // Show CreateNewOutfitFolder when the button is clicked
            if (showCreateFolderDialog) {
                CreateNewOutfitFolder(
                    onDismiss = { showCreateFolderDialog = false },
                    onCreate = { folderName ->
                        viewModel.addFolder(folderName) // Update repository
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
    viewModel: OutfitViewModel
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
                    text = "Your outfit has been saved in the following folder(s): $folderNamesText. $publicText"
                )
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false // Close dialog
                    navController.navigate(Routes.HomeTab) // Navigate to Home
                }) {
                    Text("OK")
                }
            }
        )
    }
}