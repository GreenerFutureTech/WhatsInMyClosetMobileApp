package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.viewmodels.ClothingItemViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.OutfitViewModel
import org.jetbrains.compose.resources.painterResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.top1
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun OutfitScreen(
    navController: NavController,
    clothingItemViewModel: ClothingItemViewModel,
    outfitViewModel: OutfitViewModel
) {
    // Collect state from the ViewModel
    val selectedClothingItems by clothingItemViewModel.clothingItems.collectAsState()   // ClothingItemViewModel
    val isCreateNewOutfit by outfitViewModel.isCreateNewOutfit.collectAsState()
    val isOutfitSaved by outfitViewModel.isOutfitSaved.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) } // Exit screen

    // Show calendar dialog
    var showCalendarDialog by remember { mutableStateOf(false) }

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

        // Outfit collage area (Preview or actual implementation)
        OutfitCollageArea(emptyList())

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

                // show additional options when there is at least one clothing item
                if (selectedClothingItems.isNotEmpty())
                {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Save Outfit button
                        Button(
                            onClick = {
                                // Create the outfit
                                outfitViewModel.createOutfit(selectedClothingItems)

                                // Navigate to the OutfitSaveScreen
                                navController.navigate(
                                    Routes.OutfitSaveScreen
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedClothingItems.isNotEmpty()
                        ) {
                            Text("Save Outfit")
                        }

                        // Add to Calendar button
                        Button(
                            onClick = { showCalendarDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedClothingItems.isNotEmpty()
                        ) {
                            Text("Add to Calendar")
                        }

                        // Create New Outfit button
                        Button(
                            onClick = {
                                // Discard the current outfit and create a new one
                                outfitViewModel.discardCurrentOutfit()
                                      },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedClothingItems.isNotEmpty()
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

    // Show Outfit Saved Dialog
    if (isOutfitSaved) {
        OutfitSaved(
            navController = navController,
            onDismiss = {
                navController.navigate(Routes.HomeTab) {
                    popUpTo(Routes.HomeTab) { inclusive = true }
                }
            },
            viewModel = outfitViewModel
        )
    }

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
}

// show the items user selected to create an outfit
@Composable
fun OutfitCollageArea(selectedClothingItems: List<ClothingItem>) {
    // To track the position of each item
    // Initialize itemPositions with the same size as selectedClothingItems
    val itemPositions = remember {
        mutableStateListOf<OffsetData>().apply {
            // Calculate initial positions to avoid overlap
            selectedClothingItems.forEachIndexed { index, _ ->
                val x = 100f + (index * 200f) // Spacing of 120f between items on the x-axis
                val y = 100f + (index * 100f) // Same y-coordinate for all items
                add(OffsetData(x, y))
            }
        }
    }

    if(selectedClothingItems.isNotEmpty())
    {
        // Use LaunchedEffect to dynamically update itemPositions when selectedClothingItems changes
        LaunchedEffect(selectedClothingItems.size) {
            while (itemPositions.size < selectedClothingItems.size) {
                val x = 100f + (itemPositions.size * 120f) // Spacing of 120f between items on the x-axis
                val y = 100f // Same y-coordinate for all items (or adjust as needed)
                itemPositions.add(OffsetData(x, y))
            }
            while (itemPositions.size > selectedClothingItems.size) {
                itemPositions.removeAt(itemPositions.size - 1) // Remove extra positions
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (selectedClothingItems.isEmpty()) {
            Text("No items selected", color = Color.Gray)
        } else {
            // Loop through selectedClothingItems and display them
            selectedClothingItems.forEachIndexed { index, clothingItem ->
                DraggableClothingItem(
                    clothingItem = clothingItem,
                    itemIndex = index,
                    itemPositions = itemPositions
                )
            }
        }
    }
}

@Composable
fun DraggableClothingItem(
    clothingItem: ClothingItem,
    itemIndex: Int,
    itemPositions: MutableList<OffsetData>
) {
    // Use the position from itemPositions
    val position = remember { mutableStateOf(itemPositions[itemIndex]) }

    Box(
        modifier = Modifier
            .offset { IntOffset(position.value.x.toInt(), position.value.y.toInt()) }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    // Update the position based on the drag gesture
                    val newX = position.value.x + dragAmount.x
                    val newY = position.value.y + dragAmount.y
                    position.value = OffsetData(newX, newY)
                    itemPositions[itemIndex] = position.value // Update the shared state
                }
            }
            .size(100.dp) // Define the size of the clothing item
            .background(Color.Cyan) // Placeholder for clothing item (replace with image or actual content)
    ) {
        // Content (e.g., Text or Image of clothing)
        Text(
            text = clothingItem.name,
            modifier = Modifier.align(Alignment.Center),
            color = Color.White
        )
    }
}

data class OffsetData(val x: Float, val y: Float)

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
    onDone: (List<ClothingItem>) -> Unit, // Callback to return selected items
    onBack: () -> Unit,
    viewModel: ClothingItemViewModel // Inject the ClothingItemViewModel
) {
    // Track selected item IDs
    val selectedItemIds = remember { mutableStateOf(mutableSetOf<String>()) }

    // Track if selection mode is ON - meaning user wants to select 1 or more items
    // otherwise, clicking on the item will open a new screen with details of that item
    val isSelectionMode = remember { mutableStateOf(false) }

    // Animate the button background color
    val buttonBackgroundColor by animateColorAsState(
        targetValue = if (isSelectionMode.value) Color.Green else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    // Generate or fetch items for the category
    val categoryItems = generateRandomClothingItems(category, 18)

    // Initialize clothing items for testing routing
    LaunchedEffect(Unit) {
        if (viewModel.clothingItems.value.isEmpty()) {
            viewModel.initializeClothingItems(categoryItems)
        }
    }

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
        Button(
            onClick = {
                isSelectionMode.value = !isSelectionMode.value
            },
            modifier = if (isSelectionMode.value) {
                Modifier.background(color = buttonBackgroundColor) // Highlight the button when selection mode is ON
            } else {
                Modifier // Default modifier when selection mode is OFF
            }
        ) {
            Text("Select Item(s)")
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
                    CategoryItem(
                        item = categoryItems[thisItem],
                        isSelected = selectedItemIds.value.contains(categoryItems[thisItem].id),
                        isSelectionMode = isSelectionMode.value,

                        onItemSelected = { selectedItem ->
                            // Toggle selection based on ID
                            if (selectedItemIds.value.contains(selectedItem.id)) {
                                selectedItemIds.value.remove(selectedItem.id)
                            } else {
                                selectedItemIds.value.add(selectedItem.id)
                            }
                            // Update state to trigger recomposition
                            selectedItemIds.value = selectedItemIds.value.toMutableSet()
                        },

                        onItemClicked = { clickedItem ->
                            // Navigate to the detail screen
                            navController.navigate(Routes.CategoryItemDetailScreen(clickedItem.id,
                                clickedItem.category.toString(),

                            ))
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer with Done button
        if (isSelectionMode.value) {
            OutfitScreenFooter(
                onDone = {
                    // Update the ViewModel with the selected items
                    val selectedItems = categoryItems.filter { selectedItemIds.value.contains(it.id) }
                    viewModel.addClothingItems(selectedItems) // Add selected items to the ViewModel
                    onDone(selectedItems) // Navigate back
                },
                isDoneEnabled = selectedItemIds.value.isNotEmpty()
            )
        }
    }
}

@Composable
fun CategoryItem(
    item: ClothingItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onItemSelected: (ClothingItem) -> Unit, // For selection mode
    onItemClicked: (ClothingItem) -> Unit // For detail mode
) {
    // Border color based on selection status
    val borderColor = if (isSelected) Color.Red else Color.Transparent

    Box(
        modifier = Modifier
            .padding(4.dp)
            .border(2.dp, borderColor, shape = RoundedCornerShape(8.dp))
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
                painter = when (item.category) {
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

            // Display the clothing item name
            Text(
                text = item.name,
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 14.sp,
                color = Color.Black
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

    val clothingItems by viewModel.clothingItems.collectAsState()
    println("DEBUG, CategoryItemDetailScreen -> clothingItems: $clothingItems")

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
            text = "Category: ${selectedItem!!.category}",
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