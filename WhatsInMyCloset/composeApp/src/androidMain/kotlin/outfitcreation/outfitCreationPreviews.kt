package org.greenthread.whatsinmycloset.outfitcreation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import org.greenthread.whatsinmycloset.features.tabs.home.CreateNewOutfitTag
import org.greenthread.whatsinmycloset.features.tabs.home.CalendarDialog
import org.greenthread.whatsinmycloset.features.tabs.home.ConfirmationDialog
import org.greenthread.whatsinmycloset.features.tabs.home.DiscardConfirmationDialog
import org.greenthread.whatsinmycloset.features.tabs.home.DiscardSavingDialog
import androidx.navigation.compose.rememberNavController
import org.greenthread.whatsinmycloset.core.domain.models.User

/*import org.greenthread.whatsinmycloset.core.viewmodels.MockClothingItemViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.MockOutfitViewModel*/
//import org.greenthread.whatsinmycloset.core.viewmodels.MockWardrobeManager

/*@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewCategoryItemsScreen() {
    val onDone: () -> Unit = {
        // Handle when user is done selecting items
        println("Going back to outfit screen")
    }
    val onBack: () -> Unit = {
        // Handle back navigation
        println("Navigating back")
    }

    val mockNavController = rememberNavController()
    val mockViewModel = MockClothingItemViewModel(wardrobeManager = MockWardrobeManager())

    // Show all items in the selected category
    CategoryItemsScreen(
        navController = mockNavController,
        category = "Tops",
        onBack = onBack,
        onDone = onDone,
        viewModel = mockViewModel
    )
}*/


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewOutfitCreationScreen() {

    val user = User(99999123, "TestName", email = "testmail", firebaseUuid = "", lastLogin = "01-01-2025", name = "testName", registeredAt = "01-01-2025", updatedAt = "01-01-2025")

    val mockNavController = rememberNavController()
    //val mockOutfitViewModel = MockOutfitViewModel(user)
    //val mockClothingViewModel = MockClothingItemViewModel(wardrobeManager = MockWardrobeManager())


/*    OutfitScreen(
        navController = mockNavController,  // for testing preview
        clothingItemViewModel = mockClothingViewModel,
        outfitViewModel = mockOutfitViewModel
    )*/
}

@Preview
@Composable
fun OutfitSaveScreenPreview() {
    val onExit: () -> Unit = { /* Handle Exiting to Home Page */ }
    val onDone: () -> Unit = { /* Handle When User is Done Saving the Outfit */ }

    val mockNavController = rememberNavController()

    val mockUser = User(99999123, "TestName", email = "testmail", firebaseUuid = "", lastLogin = "01-01-2025", name = "testName", registeredAt = "01-01-2025", updatedAt = "01-01-2025")

    //val mockViewModel = MockOutfitViewModel(
    //    account = mockAccount)
    //val mockClothingItem = MockClothingItemViewModel(wardrobeManager = MockWardrobeManager())

/*    OutfitSaveScreen(
        navController = mockNavController,
        onExit = onExit,
        onDone = onDone,
        outfitViewModel = mockViewModel,
        clothingItemViewModel = mockClothingItem
    )*/
}


@Preview
@Composable
fun PreviewSingleFolderSelected() {

    val mockNavController = rememberNavController()

    val mockUser = User(99999123, "TestName", email = "testmail", firebaseUuid = "", lastLogin = "01-01-2025", name = "testName", registeredAt = "01-01-2025", updatedAt = "01-01-2025")

/*    val mockViewModel = MockOutfitViewModel(
        account = mockAccount,
        initialSelectedFolder = "Business Casuals", // Initialize with a selected folder
        initialIsPublic = false
    )*/

    //val mockClothingItem = MockClothingItemViewModel(wardrobeManager = MockWardrobeManager())

/*    OutfitSaveScreen(
        navController = mockNavController,
        onExit = {},
        onDone = {},
        outfitViewModel = mockViewModel,
        clothingItemViewModel = mockClothingItem
    )*/

}

@Preview
@Composable
fun PreviewPublicChecked() {

    val mockNavController = rememberNavController()

    val mockUser = User(99999123, "TestName", email = "testmail", firebaseUuid = "", lastLogin = "01-01-2025", name = "testName", registeredAt = "01-01-2025", updatedAt = "01-01-2025")

/*    val mockViewModel = MockOutfitViewModel(
        account = mockAccount,
        initialSelectedFolders = listOf("Business Casuals", "My Public Outfits"), // Initialize with a selected folder
        initialIsPublic = true
    )*/
    //val mockClothingItem = MockClothingItemViewModel(wardrobeManager = MockWardrobeManager())

/*    OutfitSaveScreen(
        navController = mockNavController,
        onExit = {},
        onDone = {},
        outfitViewModel = mockViewModel,
        clothingItemViewModel = mockClothingItem
    )*/
}

@Preview
@Composable
fun PreviewMultipleFoldersSelected() {

    val mockNavController = rememberNavController()

    val mockUser = User(99999123, "TestName", email = "testmail", firebaseUuid = "", lastLogin = "01-01-2025", name = "testName", registeredAt = "01-01-2025", updatedAt = "01-01-2025")

/*
    val mockViewModel = MockOutfitViewModel(
        account = mockAccount,
        initialSelectedFolders = listOf("Business Casuals", "Formals"), // Initialize with a selected folder
        initialIsPublic = true
    )
*/

    //val mockClothingItem = MockClothingItemViewModel(wardrobeManager = MockWardrobeManager())

/*    OutfitSaveScreen(
        navController = mockNavController,
        onExit = {},
        onDone = {},
        outfitViewModel = mockViewModel,
        clothingItemViewModel = mockClothingItem
    )*/
}


@Preview
@Composable
fun OutfitSavedPreview() {
    val mockNavController = rememberNavController()

    val mockUser = User(99999123, "TestName", email = "testmail", firebaseUuid = "", lastLogin = "01-01-2025", name = "testName", registeredAt = "01-01-2025", updatedAt = "01-01-2025")

/*    val mockViewModel = MockOutfitViewModel(
        account = mockAccount,
        initialSelectedFolders = listOf("Business Casuals", "Formals"), // Initialize with a selected folder
        initialIsPublic = true
    )*/

    //val mockClothingItem = MockClothingItemViewModel(wardrobeManager = MockWardrobeManager())

/*    OutfitSaved(
        navController = mockNavController,
        onDismiss = { println("Outfit saved in folder: Business Casuals") },
        viewModel = mockViewModel,
        clothingItemViewModel = mockClothingItem
    )*/
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

