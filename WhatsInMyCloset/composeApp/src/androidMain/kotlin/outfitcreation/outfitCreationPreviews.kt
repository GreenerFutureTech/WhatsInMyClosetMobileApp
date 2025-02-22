package org.greenthread.whatsinmycloset.outfitcreation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.features.tabs.home.OutfitComplete
import org.greenthread.whatsinmycloset.features.tabs.home.OutfitScreen
import org.greenthread.whatsinmycloset.features.tabs.home.OutfitSaveScreen
import org.greenthread.whatsinmycloset.features.tabs.home.OutfitSaved
import org.greenthread.whatsinmycloset.features.tabs.home.CreateNewOutfitFolder
import org.greenthread.whatsinmycloset.core.repositories.OutfitRepository
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import org.greenthread.whatsinmycloset.core.domain.models.generateSampleClothingItems
import org.greenthread.whatsinmycloset.features.tabs.home.CalendarDialog
import org.greenthread.whatsinmycloset.features.tabs.home.CategoryItemsScreen
import org.greenthread.whatsinmycloset.features.tabs.home.ConfirmationDialog
import org.greenthread.whatsinmycloset.features.tabs.home.DiscardConfirmationDialog
import org.greenthread.whatsinmycloset.features.tabs.home.DiscardSavingDialog


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewOutfitScreen() {
    val onDone: () -> Unit = { /* Handle When User is Done Creating the Outfit */ }

    val sampleItems = generateSampleClothingItems()

    OutfitScreen(
        onDone = onDone,
        selectedClothingItems = sampleItems // dummy items
    )
}

@Composable
@Preview
fun PreviewCategoryItemsScreen() {
    val onDone: () -> Unit = { /* Handle When User is Done Creating the Outfit */ }

    // show all items in the category selected
    CategoryItemsScreen(
        category = "Tops",
        onDone = onDone
    )
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewOutfitCreationDone() {
    val onSave: () -> Unit = { /* Handle Saving Outfit */ }
    val onCreateNew: () -> Unit = {/* When User wants to get out of the outfit creation screen*/}

    val sampleItems = generateSampleClothingItems()

    OutfitComplete(
        onSave = onSave,
        onAddToCalendar = { _ -> },
        onCreateNew = onCreateNew,
        selectedClothingItems = sampleItems
    )
}

@Preview
@Composable
fun OutfitSaveScreenPreview() {
    val onGoBack: () -> Unit = { /* Handle Go Back */ }
    val onExit: () -> Unit = { /* Handle Exiting to Home Page */ }
    val onDone: () -> Unit = { /* Handle When User is Done Saving the Outfit */ }
    val onCreateNewFolder: (String) -> Unit = { /* Handle Creating New Folder */ }
    val onSaveToFolder: (String, Boolean) -> Unit = { folderName, isPublic ->
        /* Handle Saving Outfit to folderName with public flag */
    }

    // Initialize repository
    val outfitRepository = remember { OutfitRepository() }

    val sampleItems = generateSampleClothingItems()

    OutfitSaveScreen(
        onExit = onExit,
        selectedClothingItems = sampleItems,
        onCreateNewFolder = onCreateNewFolder,
        onDone = onDone,
        onSaveToFolder = onSaveToFolder
    )
}


@Preview
@Composable
fun PreviewSingleFolderSelected() {
    val selectedFolder = remember { mutableStateOf("Business Casuals") }

    OutfitSaveScreen(
        onExit = {},
        onDone = {},
        selectedClothingItems = emptyList(),
        onCreateNewFolder = {},
        onSaveToFolder = { _, _ -> },
        previewSelectedFolder = selectedFolder.value,
        previewIsPublic = false
    )
}

@Preview
@Composable
fun PreviewPublicChecked() {
    OutfitSaveScreen(
        onExit = {},
        onDone = {},
        selectedClothingItems = emptyList(),
        onCreateNewFolder = {},
        onSaveToFolder = { _, _ -> },
        previewSelectedFolder = "My Public Outfits",
        previewIsPublic = true
    )
}

@Preview
@Composable
fun PreviewMultipleFoldersSelected() {
    val selectedFolders = remember { mutableStateListOf("Business Casuals", "Fancy") }

    OutfitSaveScreen(
        onExit = {},
        onDone = {},
        selectedClothingItems = emptyList(),
        onCreateNewFolder = {},
        onSaveToFolder = { _, _ -> },
        previewSelectedFolders = selectedFolders
    )
}


@Preview
@Composable
fun OutfitSavedPreview() {
    OutfitSaved(
        folderNames = listOf("Business Casuals", "Fancy"), // Pass a list of folder names
        isPublic = true, // Set whether the outfit is public or not
        onDismiss = { println("Outfit saved in folder: Business Casuals") }
    )
}
@Preview
@Composable
fun DiscardSaveOutfitPreview() {
    DiscardSavingDialog(
        onConfirm = {  },
        onDismiss = { /* Preview dismiss */ }
    )
}


@Preview
@Composable
fun CreateNewOutfitFolderPreview() {
    CreateNewOutfitFolder(
        onDismiss = { /* Preview dismiss */ },
        onCreate = { folderName -> println("Folder created: $folderName") }
    )
}


@Preview
@Composable
fun AddToCalendarScreenPreview() {
    CalendarDialog(
        onDismiss = { /* Preview dismiss */ },
        onDateSelected = { selectedDate ->
            println("Selected Date: $selectedDate") // Show the selected date in the log
        }
    )
}

@Preview
@Composable
fun AddToCalendarDonePreview() {
    ConfirmationDialog(
        message = "Outfit added to February 7, 2025",
        onDismiss = { /* Preview dismiss */ }
    )
}


@Preview
@Composable
fun DiscardAddToCalendarPreview() {
    DiscardConfirmationDialog(
        onConfirm = {  },
        onDismiss = { /* Preview dismiss */ }
    )
}

// ---------------------------------------------
// Additional Individual Previews for UI Components
// ---------------------------------------------

@Preview
@Composable
fun OutfitItemsOptions() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Outfit Items Options", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = { /* Handle Select Tops */ }) { Text("Tops") }
        Button(onClick = { /* Handle Select Bottoms */ }) { Text("Bottoms") }
        Button(onClick = { /* Handle Select Footwear */ }) { Text("Footwear") }
        Button(onClick = { /* Handle Select Others */ }) { Text("Others") }
    }
}

@Preview
@Composable
fun OutfitCollageArea() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Outfit Collage Area", style = MaterialTheme.typography.headlineMedium)
        Text("Images or representations of selected clothing items would go here.")
    }
}


@Preview
@Composable
fun GoBackButton() {
    Button(onClick = { /* Handle Go Back */ }, modifier = Modifier.padding(8.dp)) {
        Text("<")
    }
}

@Preview
@Composable
fun SaveButton() {
    Button(onClick = { /* Save outfit to repo */ }, modifier = Modifier.padding(8.dp)) {
        Text("Save Outfit")
    }
}

@Preview
@Composable
fun AddToCalendarButton() {
    Button(onClick = { /* Add outfit to calendar */ }, modifier = Modifier.padding(8.dp)) {
        Text("Add to Calendar")
    }
}

