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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.repositories.OutfitRepository


@Composable
fun OutfitSaveScreen(
    onExit: () -> Unit,
    onDone: () -> Unit,
    selectedClothingItems: List<ClothingItem>,
    onCreateNewFolder: (String) -> Unit,
    onSaveToFolder: (String, Boolean) -> Unit,
    previewSelectedFolder: String? = null,
    previewSelectedFolders: List<String>? = null,
    previewIsPublic: Boolean = false
) {
    val outfitRepository = remember { OutfitRepository() }
    val outfitFolders by outfitRepository.outfitFolders.collectAsState()
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var isPublic by remember { mutableStateOf(previewIsPublic) }
    var selectedFolder by remember { mutableStateOf(previewSelectedFolder ?: "") }

    // If there are multiple folders selected, use mutableStateListOf, otherwise use selectedFolder
    val selectedFolders = remember { mutableStateListOf<String>().apply { previewSelectedFolders?.let { addAll(it) } } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Heading for the selected category
        OutfitScreenHeader(
            onGoBack = { /* Handle back action */ },
            onExit = { /* Handle exit action */ },
            title = "Save Your Outfit"
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(outfitFolders) { folder ->
                // Determine if folder is selected (either one folder or multiple)
                val isSelected = if (previewSelectedFolders != null) {
                    selectedFolders.contains(folder) || (isPublic && folder == "My Public Outfits")
                } else {
                    selectedFolder == folder || (isPublic && folder == "My Public Outfits")
                }

                print(isSelected)
                print(selectedFolder)

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
                            if (previewSelectedFolders != null) {
                                // Multiple folder selection
                                if (selectedFolders.contains(folder)) {
                                    selectedFolders.remove(folder) // Unselect if already selected
                                } else {
                                    selectedFolders.add(folder) // Select folder
                                }
                            } else {
                                // Single folder selection
                                selectedFolder = if (selectedFolder == folder) {
                                    "" // Deselect the folder if it is already selected
                                } else {
                                    folder // Select the folder
                                }
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
                onCheckedChange = {
                    isPublic = it
                    if (it) {
                        if (previewSelectedFolders != null) {
                            selectedFolders.clear()
                            selectedFolders.add("My Public Outfits")
                        } else {
                            selectedFolder = "My Public Outfits"
                        }
                    }
                }
            )
            Text(text = "Public")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Footer with Done button
        OutfitScreenFooter(onDone = onDone,
            isDoneEnabled = if (previewSelectedFolders != null) selectedFolders.isNotEmpty() else selectedFolder.isNotEmpty())

        /*Button(
            onClick = onDone,
            modifier = Modifier
                .height(40.dp)
                .width(100.dp),
            enabled = if (previewSelectedFolders != null) selectedFolders.isNotEmpty() else selectedFolder.isNotEmpty()
        ) { Text("Done") }*/

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
                    outfitRepository.addFolder(folderName) // Update repository
                    onCreateNewFolder(folderName) // Notify parent
                    showCreateFolderDialog = false // Close the dialog after creating the folder
                }
            )
        }

        // Show discard confirmation dialog when "x" is clicked
        if (showDiscardDialog) {
            DiscardSavingDialog(
                onConfirm = {
                    showDiscardDialog = false
                    onExit() // Exit screen
                },
                onDismiss = { showDiscardDialog = false }
            )
        }
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
    folderNames: List<String>, // List of folder names where the outfit was saved
    isPublic: Boolean, // Indicates if the outfit is public
    onDismiss: () -> Unit, // Callback to close the dialog and go back to the Home Tab
) {
    var showDialog by remember { mutableStateOf(true) }

    // Add "My Public Outfits" if the outfit is public
    val finalFolderNames = if (isPublic) {
        folderNames + "My Public Outfits"
    } else {
        folderNames
    }

    // Check if the dialog should be shown
    if (showDialog) {
        // Generate the message about the folders
        val folderNamesText = finalFolderNames.joinToString(", ")
        val publicText = if (isPublic) "Your outfit is now public." else ""

        AlertDialog(
            onDismissRequest = { onDismiss() }, // Close dialog when clicked outside
            title = { Text(text = "Outfit Saved") },
            text = {
                Text(
                    text = "Your outfit has been saved in the following folder(s): $folderNamesText. $publicText"
                )
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false // Close dialog
                    onDismiss() // Go to ***Home Tab*** or another screen
                }) {
                    Text("OK")
                }
            }
        )
    }
}