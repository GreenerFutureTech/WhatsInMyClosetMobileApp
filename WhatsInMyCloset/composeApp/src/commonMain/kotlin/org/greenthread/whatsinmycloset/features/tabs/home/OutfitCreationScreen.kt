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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.viewmodels.ClothingItemViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.OutfitViewModel
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.top1



@Composable
fun OutfitScreen(
    navController: NavController,
    clothingItemViewModel: ClothingItemViewModel,
    outfitViewModel: OutfitViewModel
) {

    WhatsInMyClosetTheme {
        // for testing - populate the categories with random items
        val allCategories = ClothingCategory.values()
        val categoryItemsMap = allCategories.associateWith { category ->
            generateRandomClothingItems(category.toString(), 18)
        }

        // Initialize clothing items for testing routing
        LaunchedEffect(Unit) {
            println("DEBUG: Initializing clothing items...")

            val allItems = categoryItemsMap.values.flatten() // Combine all category items
            clothingItemViewModel.initializeClothingItems(allItems)
        }


        // Collect state from the ViewModel
        val selectedItems by clothingItemViewModel.selectedItems.collectAsState()
        val isCreateNewOutfit by outfitViewModel.isCreateNewOutfit.collectAsState()
        val isOutfitSaved by outfitViewModel.isOutfitSaved.collectAsState()
        var showExitDialog by remember { mutableStateOf(false) } // Exit screen

        // Show calendar dialog
        var showCalendarDialog by remember { mutableStateOf(false) }

        // Handle position updates
        val onPositionUpdate = { itemId: String, newPosition: OffsetData ->
            outfitViewModel.updateClothingItemPosition(itemId, newPosition)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header
            OutfitScreenHeader(
                onGoBack = { navController.popBackStack() }, // Navigate back to Home Tab,
                onExit = { showExitDialog = true },  // Discard Outfit Creation -- pop up
                title = "Create Your Outfit"
            )

            // Outfit collage area will show the selectedClothingItems
            OutfitCollageArea(
                selectedClothingItems = selectedItems,
                onPositionUpdate = onPositionUpdate
            )

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp))
            {
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
                if (selectedItems.isNotEmpty())
                {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Save Outfit button
                        Button(
                            onClick = {
                                // Create the outfit, pass user's id and outfit id
                                outfitViewModel.createOutfit(selectedItems)

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

                        // Add to Calendar button
                        Button(
                            onClick = { showCalendarDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedItems.isNotEmpty()
                        ) {
                            Text("Add to Calendar")
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
                            Text("Create New Outfit")
                        }
                    }
                }
            }

        }   // end of Column

        // Show Calendar Dialog
        if (showCalendarDialog) {
            CalendarDialog(
                onDismiss = { showCalendarDialog = false },
                onDateSelected = { selectedDate ->
                    outfitViewModel.addOutfitToCalendar(selectedDate) // Pass the selected date to the callback
                    showCalendarDialog = false // Close the dialog
                }
            )
        }

        // Show Exit Dialog
        if (showExitDialog) {
            DiscardOutfitDialog(
                onConfirm = {
                    showExitDialog = false
                    navController.navigate(Routes.HomeTab) // Navigate to Home Tab
                },
                onDismiss = { showExitDialog = false }
            )
        }
    }
}   /* end of OutfitScreen */

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
            Text("No items selected", color = Color.Gray)
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
        // Content (e.g., Text or Image of clothing)
        Text(
            text = clothingItem.name,
            modifier = Modifier.align(Alignment.Center),
            color = Color.White
        )

        // Show Image
    }
}

//data class OffsetData(val x: Float, val y: Float)

// shows all category options
@Composable
fun ClothingCategorySelection(onSelectCategory: (ClothingCategory) -> Unit) {
    val categories = ClothingCategory.values()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Two columns
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(categories.size) { index ->
            val thisItem = categories[index]

            Button(
                onClick = { onSelectCategory(thisItem) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
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

    // Collect items for the selected category when category changes
    LaunchedEffect(categoryEnum) {
        viewModel.getItemsByCategory(categoryEnum.toString())
    }
    val categoryItems by viewModel.categoryItems.collectAsState()

    // Track selected items uniquely by ID + Category
    var selectedItemKeys by remember { mutableStateOf(setOf<Pair<String, ClothingCategory>>()) }

    // Track if selection mode is ON - meaning user wants to select 1 or more items
    // otherwise, clicking on the item will open a new screen with details of that item
    val isSelectionMode = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Heading for the selected category
        OutfitScreenHeader(
            onGoBack = {navController.popBackStack()},
            onExit = {navController.navigate(Routes.HomeTab)},
            title = "Select $category"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // A button to toggle selection, if this button is not selected
        // clicking on an item, will open a new screen with the details of that item
        // using the CategoryItemScreen function
        // Button to toggle selection mode
        Text(
            text = "Select Items ${selectedItemKeys.size}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        var checked by remember { mutableStateOf(false) }   // set switch to false

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
                                Routes.CategoryItemDetailScreen(
                                    clickedItem.id, clickedItem.itemType.toString()
                                )
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
}

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
            // Display the clothing item image
            Image(
                painter = when (item.itemType) {
                    ClothingCategory.TOPS -> painterResource(Res.drawable.top1)
                    ClothingCategory.BOTTOMS -> painterResource(Res.drawable.top1)
                    ClothingCategory.FOOTWEAR -> painterResource(Res.drawable.top1)
                    ClothingCategory.ACCESSORIES -> painterResource(Res.drawable.top1)
                },
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

// when user clicks on an item, a new screen opens showing picture and details of the item
@Composable
fun CategoryItemDetailScreen(
    navController: NavController,
    itemId: String,
    category: ClothingCategory,
    onBack: () -> Unit,
    viewModel: ClothingItemViewModel // Inject the ClothingItemViewModel

) {

    // Fetch the item details from the ViewModel using both itemId and category
    val selectedItem = remember(itemId, category) {
        viewModel.getClothingItemDetails(itemId, category)
    }

    println("DEBUG, CategoryItemDetailScreen -> selectedItem: $selectedItem")

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
            onGoBack = {navController.popBackStack()},
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
            // Display the clothing item image
            //selectedItem?.clothingImage?.let { imageResId ->
            Image(
                painter = painterResource(Res.drawable.top1), // Using dynamic resource
                contentDescription = selectedItem.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .fillMaxHeight()
                    .align(Alignment.Center) // Use Alignment.Center to center the image
            )
            //}
        }

        Spacer(modifier = Modifier.height(4.dp))

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
    onGoBack: () -> Unit,
    onExit: () -> Unit,
    title: String

) {
    // Go Back and Exit buttons
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Button(onClick = onGoBack, modifier = Modifier.padding(8.dp)) {
            Text("<")
        }

        Button(onClick = onExit, modifier = Modifier.padding(8.dp)) {
            Text("x")
        }
    }

    Text(text = "$title", style = MaterialTheme.typography.headlineMedium)
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