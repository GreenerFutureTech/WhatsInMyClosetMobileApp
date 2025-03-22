package org.greenthread.whatsinmycloset.features.tabs.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData
import org.greenthread.whatsinmycloset.core.domain.models.generateRandomClothingItems
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe
import org.greenthread.whatsinmycloset.core.viewmodels.ClothingItemViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.OutfitViewModel
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.jetbrains.compose.resources.painterResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.top1
import whatsinmycloset.composeapp.generated.resources.wardrobe


@Composable
fun OutfitScreen(
    navController: NavController,
    clothingItemViewModel: ClothingItemViewModel,
    outfitViewModel: OutfitViewModel
) {

    WhatsInMyClosetTheme {

        // Track selected wardrobe in the Composable
        var selectedWardrobe by remember { mutableStateOf(clothingItemViewModel.defaultWardrobe) }

        // Initialize clothing items for the selected wardrobe (default when screen first launches)
        LaunchedEffect(selectedWardrobe) {
            println("DEBUG: Initializing clothing items for wardrobe: ${selectedWardrobe?.wardrobeName}...")

            // fetch items for selected wardrobe
            /*if (selectedWardrobe != null) {
                // For each category, fetch items for the selected wardrobe
                clothingItemViewModel.getItemsByCategoryAndWardrobe("Tops", selectedWardrobe)
            }*/


            // get sample items from BLOB to show on screen
            clothingItemViewModel.fetchSampleClothingItems()
        }

        // Collect state from the ViewModel
        val selectedItems by clothingItemViewModel.selectedItems.collectAsState()
        val isCreateNewOutfit by outfitViewModel.isCreateNewOutfit.collectAsState()
        val isOutfitSaved by outfitViewModel.isOutfitSaved.collectAsState()
        var showExitDialog by remember { mutableStateOf(false) } // Exit screen

        // Handle position updates
        val onPositionUpdate = { itemId: String, newPosition: OffsetData ->
            outfitViewModel.updateClothingItemPosition(itemId, newPosition)
        }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {

                // Header
                OutfitScreenHeader(
                    onExit = { showExitDialog = true },  // Discard Outfit Creation
                    title = "Create Your Outfit"
                )

                // Outfit collage area will show the selectedClothingItems
                OutfitCollageArea(
                    selectedClothingItems = selectedItems,
                    onPositionUpdate = onPositionUpdate
                )

                // Clothing category selection
                ClothingCategorySelection { selectedCategory ->
                    // Convert the selected category string to ClothingCategory enum
                    val categoryEnum = ClothingCategory.fromString(selectedCategory.categoryName)
                    if (categoryEnum != null) {
                        // Navigate to the category items screen showing all items in that category
                        navController.navigate(Routes.CategoryItemScreen(categoryEnum.toString()))
                    } else {
                        // Handle invalid category (e.g., show an error message)
                        println("Invalid category selected: ${selectedCategory.categoryName}")
                    }
                }

                // show additional options when there is at least one item in outfit area
                if (selectedItems.isNotEmpty()) {
                        // Save Outfit button
                        Button(
                            onClick = {
                                // Create the outfit, pass selected items
                                outfitViewModel.createOutfit(name="New Outfit", // allow user to add a name
                                    selectedItems = selectedItems)

                                // Navigate to the OutfitSaveScreen
                                navController.navigate(
                                    Routes.OutfitSaveScreen
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedItems.isNotEmpty()
                        ) {
                            Text("Save Outfit")
                        }

                        // Create New Outfit button
                        Button(
                            onClick = {
                                // Discard the current outfit and create a new one
                                outfitViewModel.discardCurrentOutfit()
                                outfitViewModel.clearOutfitState() // Clear the outfit state
                                clothingItemViewModel.clearClothingItemState() // Clear the selected items state
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedItems.isNotEmpty()
                        ) {
                            Text("Reset")
                        }
                }
            }   // end of Column

        // Show Exit Dialog
        if (showExitDialog) {
            DiscardOutfitDialog(
                onConfirm = {
                    showExitDialog = false
                    // Discard the current outfit and create a new one
                    outfitViewModel.discardCurrentOutfit()
                    outfitViewModel.clearOutfitState() // Clear the outfit state
                    clothingItemViewModel.clearClothingItemState() // Clear the selected items state
                    navController.navigate(Routes.HomeTab) // Navigate to Home Tab
                },
                onDismiss = { showExitDialog = false }
            )
        }
    }
}   /* end of OutfitScreen */

// select a wardrobe to choose items from
@Composable
fun WardrobeDropdown(
    wardrobes: List<Wardrobe>,
    selectedWardrobe: Wardrobe?,
    onWardrobeSelected: (Wardrobe) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Wardrobe clickable icon
    Image(
        painter = painterResource(Res.drawable.wardrobe), // Replace with your wardrobe icon resource
        contentDescription = "Wardrobe Icon",
        modifier = Modifier
            .size(30.dp)
            .clickable { expanded = true } // Show dropdown when clicked
    )

    // Wardrobe dropdown menu
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false } // Hide dropdown when dismissed
    ) {
        wardrobes.forEach { wardrobe ->
            DropdownMenuItem(
                onClick = {
                    onWardrobeSelected(wardrobe) // Notify parent of the selected wardrobe
                    expanded = false // Hide dropdown after selection
                },
                text = { Text(text = wardrobe.wardrobeName) } // Display wardrobe name
            )
        }
    }
    /*Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

    }*/
}

@Composable
fun DiscardOutfitDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancel creating outfit") },
        text = { Text("Are you sure you want to cancel the outfit?") },
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
}   /* end of DiscardOutfitDialog */

// show the items user selected to create an outfit
@Composable
fun OutfitCollageArea(
    selectedClothingItems: List<ClothingItem>,
    onPositionUpdate: (String, OffsetData) -> Unit)
{
    val canvasHeight = with(LocalDensity.current) { 300.dp.toPx() }
    val canvasWidth = with(LocalDensity.current) { 450.dp.toPx() }

    Box(
        modifier = Modifier
            .width(450.dp)
            .height(300.dp) // Start with 300.dp
            .background(Color.LightGray)
    )
    {
        if (selectedClothingItems.isEmpty()) {
            Text("No items selected",
                color = Color.Gray,
                textAlign = TextAlign.Center)
        }
        else
        {
            val itemPositions = remember { mutableStateListOf<OffsetData>() }

            // Ensure items retain their positions when added
            LaunchedEffect(selectedClothingItems.size) {
                selectedClothingItems.forEachIndexed { index, _ ->
                    if (index >= itemPositions.size) {
                        val x = (index * 120f).coerceIn(0f, canvasWidth - 100f)
                        val y = (index * 80f).coerceIn(0f, canvasHeight - 100f) // Stagger Y positions

                        itemPositions.add(OffsetData(x, y))
                    }
                }
            }

            // Loop through selectedClothingItems and display them
            selectedClothingItems.forEachIndexed { index, clothingItem ->
                DraggableClothingItem(
                    clothingItem = clothingItem,
                    itemIndex = index,
                    itemPositions = itemPositions,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    onPositionUpdate = { newPosition ->
                        // Notify parent of position change
                        onPositionUpdate(clothingItem.id, newPosition)
                    }
                )
            }

        }
    }

}   /* end of OutfitCollageArea */


@Composable
fun DraggableClothingItem(
    clothingItem: ClothingItem,
    itemIndex: Int,
    itemPositions: MutableList<OffsetData>,
    canvasWidth: Float,
    canvasHeight: Float,
    onPositionUpdate: (OffsetData) -> Unit
) {
    val defaultPosition = OffsetData(100f, 100f)
    val position = remember {
        mutableStateOf(
            if (itemIndex < itemPositions.size) itemPositions[itemIndex] else defaultPosition
        )
    }

    // Only update if the position has changed
    LaunchedEffect(itemPositions) {
        if (itemIndex < itemPositions.size && position.value != itemPositions[itemIndex]) {
            position.value = itemPositions[itemIndex]
        }
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(position.value.x.toInt(), position.value.y.toInt()) }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->

                    val newX = (position.value.x + dragAmount.x)
                        .coerceIn(0f, canvasWidth - 300f)
                    val newY = (position.value.y + dragAmount.y)
                        .coerceIn(0f, canvasHeight - 275f)

                    println("NEW X: $newX")
                    println("NEW Y: $newY")

                    val newPosition = OffsetData(newX, newY)
                    position.value = OffsetData(newX, newY)

                    if (itemIndex < itemPositions.size) {
                        itemPositions[itemIndex] = position.value
                    }

                    // Notify parent of position change
                    onPositionUpdate(newPosition)
                }
            }
            .size(100.dp) // Define the size of the clothing item
            .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
    ) {

        // Display the item image
        AsyncImage(
            model = clothingItem.mediaUrl, // Use the image URL from the sample data
            contentDescription = clothingItem.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

// shows all category options
@Composable
fun ClothingCategorySelection(onSelectCategory: (ClothingCategory) -> Unit) {
    val categories = ClothingCategory.values()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Two columns
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(categories.size) { index ->
            val thisItem = categories[index]

            Button(
                onClick = { onSelectCategory(thisItem) },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = thisItem.categoryName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// highlight selected clothing items
// more than 1 items more the same category can be selected by the user
@Composable
fun CategoryItemsScreen(
    navController: NavController,
    category: String,
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: ClothingItemViewModel // Inject the ClothingItemViewModel
) {
    // items in the selected category
    val categoryEnum = ClothingCategory.fromString(category)

    val categoryItems by viewModel.categoryItems.collectAsState()

    // Track selected items uniquely by ID + Category
    var selectedItemKeys by remember { mutableStateOf(setOf<Pair<String, ClothingCategory>>()) }

    // Track if selection mode is ON - meaning user wants to select 1 or more items
    // otherwise, clicking on the item will open a new screen with details of that item
    val isSelectionMode = remember { mutableStateOf(false) }

    // Track selected wardrobe in the Composable
    var selectedWardrobe by remember { mutableStateOf(viewModel.defaultWardrobe) }

    // Dropdown menu state
    var expanded by remember { mutableStateOf(false) }

    val wardrobes by viewModel.wardrobes.collectAsState()
    var checked by remember { mutableStateOf(false) }   // set switch to false

    // Fetch items for the selected category and wardrobe whenever they change
    /*LaunchedEffect(category, selectedWardrobe) {
        if (selectedWardrobe != null) {
            viewModel.getItemsByCategoryAndWardrobe(category, selectedWardrobe)
        }
    }*/

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Enable vertical scrolling
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Heading for the selected category
        OutfitScreenHeader(
            onExit = {navController.navigate(Routes.HomeTab)},
            title = "Select $category"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Selected Items ${selectedItemKeys.size}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Wardrobe selection dropdown
            WardrobeDropdown(
                wardrobes = wardrobes,
                selectedWardrobe = selectedWardrobe,
                onWardrobeSelected = { wardrobe ->
                    selectedWardrobe = wardrobe // Update the selected wardrobe
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    // Toggle selection mode when switch state changes
                    isSelectionMode.value = !isSelectionMode.value

                    // Reset selection when leaving selection mode
                    if (!isSelectionMode.value) {
                        selectedItemKeys = emptySet()
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)  // This makes the grid scrollable
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(categoryItems.size)
                { thisItem ->
                    val item = categoryItems[thisItem]
                    val itemKey = item.id to item.itemType

                    CategoryItem(
                        item = item,
                        isSelected = selectedItemKeys.contains(itemKey),
                        isSelectionMode = isSelectionMode.value,

                        onItemSelected = { selectedItem ->
                            selectedItemKeys = selectedItemKeys.toMutableSet().apply {
                                if (contains(itemKey)) remove(itemKey) else add(itemKey)
                            }
                        },
                        onItemClicked = { clickedItem ->
                            navController.navigate(
                                Routes.CategoryItemDetailScreen(clickedItem.wardrobeId.toString(),
                                    clickedItem.id, clickedItem.itemType.toString())
                                )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Done button shows up when user selects 1 or more items to add to canvas
        if (isSelectionMode.value) {
            OutfitScreenFooter(
                onDone = {
                    // Update the ViewModel with the selected items
                    val selectedItems = categoryItems.filter {
                        selectedItemKeys.contains(it.id to it.itemType)
                    }

                    viewModel.addSelectedItems(selectedItems) // Add selected items to the ViewModel
                    navController.navigate(Routes.CreateOutfitScreen)
                         // Navigate to Outfit Screen with selected items
                },
                isDoneEnabled = selectedItemKeys.isNotEmpty()
            )
        }
    }
} /* end of CategoryItemsScreen */

// this function displays all items in the selected category on screen
@Composable
fun CategoryItem(
    item: ClothingItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onItemSelected: (ClothingItem) -> Unit, // For selection mode
    onItemClicked: (ClothingItem) -> Unit // For detail mode
) {

    // Log the isSelected state for debugging
    println("CategoryItem: ${item.name}, isSelected: $isSelected")


    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, if (isSelected) Color.Green else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable {
                if (isSelectionMode) {
                    onItemSelected(item) // Handle selection
                } else {
                    onItemClicked(item) // Handle detail navigation
                }
            }
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Display the item image
            AsyncImage(
                model = item.mediaUrl, // Use the image URL from the sample data
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            // Display the clothing item name
            Text(
                text = item.name,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// when user clicks on an item, a new screen opens showing picture and details of the item
@Composable
fun CategoryItemDetailScreen(
    navController: NavController,
    wardrobeId: String,
    itemId: String,
    category: ClothingCategory,
    onBack: () -> Unit,
    viewModel: ClothingItemViewModel // Inject the ClothingItemViewModel

) {

    // State to hold the selected item
    var selectedItem by remember { mutableStateOf<ClothingItem?>(null) }

    // State to hold the wardrobe name
    var wardrobeName by remember { mutableStateOf("Unknown Wardrobe") }

    // Fetch the item details when the screen is first launched
    LaunchedEffect(wardrobeId, itemId, category) {
        //val item = viewModel.getItemDetail(wardrobeId, itemId, category)
        val item = viewModel.getItemDetailTest(itemId)
        selectedItem = item

        // Fetch the wardrobe name using the wardrobeId from the selected item
        wardrobeName = viewModel.wardrobes.value.find { it.id == item?.wardrobeId }?.wardrobeName
            ?: "Unknown Wardrobe"
    }

    /*println("DEBUG, CategoryItemDetailScreen -> " +
            "selectedItem: $selectedItem selected wardrobe: $wardrobeName")*/

    // If the item is not found, show an error message
    if (selectedItem == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Item not found", color = Color.Red, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Back")
            }
        }
        return
    }

    // Display the item details
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Heading for the selected category
        OutfitScreenHeader(
            onExit = {navController.navigate(Routes.HomeTab)},
            title = selectedItem!!.name
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                .padding(2.dp)
        ) {
            // Display the item image
            AsyncImage(
                model = selectedItem!!.mediaUrl, // Use the image URL from the sample data
                contentDescription = selectedItem!!.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .fillMaxHeight()
                    .align(Alignment.Center) // Use Alignment.Center to center the image
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Display wardrobe name
        Text(
            text = "Wardrobe: ${wardrobeName}",
            modifier = Modifier.padding(top = 8.dp),
            fontSize = 20.sp,
            color = Color.Gray
        )

        // Display the clothing item category
        Text(
            text = "Category: ${selectedItem!!.itemType}",
            modifier = Modifier.padding(top = 8.dp),
            fontSize = 20.sp,
            color = Color.Gray
        )

        // Display the clothing item tags (if any)
        if (!selectedItem!!.tags.isNullOrEmpty()) {
            Text(
                text = "Tags: ${selectedItem!!.tags?.joinToString(", ")}",
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 20.sp,
                color = Color.Gray
            )
        }
    }
}


@Composable
fun OutfitScreenHeader(
    onExit: () -> Unit,
    title: String

) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
    {
        Text(
            text = "$title",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.Center), // Allow centering
            textAlign = TextAlign.Center)

        // Exit Button - Right Aligned
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ){
        ElevatedButton(
            onClick = onExit,
            modifier = Modifier
                .width(60.dp)  // Set width to ensure a rectangular shape
                .height(30.dp), // Define height for a proper rectangle
            shape = RoundedCornerShape(10.dp) // Rounded corners
        )
        {
            Icon(Icons.Default.Close,
                contentDescription = "Exit",
                modifier = Modifier.size(24.dp))
        }}
    }
}

@Composable
fun OutfitScreenFooter(
    onDone: () -> Unit,
    isDoneEnabled: Boolean
)
{
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onDone,
            modifier = Modifier
                .width(210.dp),
            enabled = isDoneEnabled
        ) { Text("Done") }
    }
}


@Composable
fun CreateNewOutfit() {

    // message pop - if the user would like to save or discard the outfit

    // if save - show the save screen

    // if discard - discard the outfit and show the create outfit screen again
}

@Preview
@Composable
fun OutfitItemsOptions()
{

}

@Preview
@Composable
fun OutfitCollageArea()
{

}

@Preview
@Composable
fun DoneButton()
{

}

@Preview
@Composable
fun GoBackButton()
{

}

@Preview
@Composable
fun SaveButton()
{
    // save created outfit in a repo - "Friday Looks", "Business Casuals" etc.
}

@Preview
@Composable
fun AddToCalendarButton()
{

}