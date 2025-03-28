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
import org.greenthread.whatsinmycloset.core.utilities.CoordinateNormalizer
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

        // Collect state from ViewModels
        val wardrobes by outfitViewModel.cachedWardrobes.collectAsState()
        val selectedWardrobeId by remember(outfitViewModel) {
            derivedStateOf { outfitViewModel.selectedWardrobe.value }
        }
        val selectedItems by clothingItemViewModel.selectedItems.collectAsState()
        val isCreateNewOutfit by outfitViewModel.isCreateNewOutfit.collectAsState()
        val isOutfitSaved by outfitViewModel.isOutfitSaved.collectAsState()

        var showExitDialog by remember { mutableStateOf(false) }

        // Initialize default wardrobe selection
        LaunchedEffect(wardrobes) {
            if (wardrobes.isNotEmpty() && selectedWardrobeId == null) {
                clothingItemViewModel.setWardrobeFilter(wardrobes.first())
            }
        }

        // Handle position updates
        val onPositionUpdate = { itemId: String, newPosition: OffsetData ->
            outfitViewModel.updateItemPosition(itemId, newPosition)
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
                        outfitViewModel.createOutfit(
                            name = "New Outfit",    // TODO take name from user else Default Name
                            onSuccess = {
                                navController.navigate(Routes.OutfitSaveScreen)
                            }
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

    val (dynamicItemWidth, dynamicItemHeight) = CoordinateNormalizer.calculateDynamicItemSize(
        canvasWidth,
        canvasHeight
    )

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
            // Track positions by item ID instead of index
            val itemPositions = remember { mutableStateMapOf<String, OffsetData>() }

            // Initialize positions for new items
            LaunchedEffect(selectedClothingItems) {
                selectedClothingItems.forEach { item ->
                    if (!itemPositions.containsKey(item.id)) {
                        // Calculate initial position (you can adjust this logic)
                        val x = (itemPositions.size * (dynamicItemWidth * 1.5f)).coerceIn(0f, canvasWidth - dynamicItemWidth)
                        val y = (itemPositions.size * (dynamicItemHeight * 1.5f)).coerceIn(0f, canvasHeight - dynamicItemHeight)

                        val (normalizedX, normalizedY) = CoordinateNormalizer.normalizeCoordinates(
                            x, y, canvasWidth, canvasHeight
                        )

                        itemPositions[item.id] = OffsetData(normalizedX, normalizedY)
                        onPositionUpdate(item.id, OffsetData(normalizedX, normalizedY))
                    }
                }
            }

            // Loop through selectedClothingItems and display them
            selectedClothingItems.forEach { clothingItem ->
                DraggableClothingItem(
                    clothingItem = clothingItem,
                    initialPosition = itemPositions[clothingItem.id] ?: OffsetData(0f, 0f),
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    dynamicItemWidth = dynamicItemWidth,
                    dynamicItemHeight = dynamicItemHeight,
                    onPositionUpdate = { newPosition ->
                        val (normalizedX, normalizedY) = CoordinateNormalizer.normalizeCoordinates(
                            newPosition.x, newPosition.y, canvasWidth, canvasHeight
                        )

                        itemPositions[clothingItem.id] = OffsetData(normalizedX, normalizedY)
                        onPositionUpdate(clothingItem.id, OffsetData(normalizedX, normalizedY))
                    }
                )
            }

        }
    }

}   /* end of OutfitCollageArea */


@Composable
fun DraggableClothingItem(
    clothingItem: ClothingItem,
    initialPosition: OffsetData,
    canvasWidth: Float,
    canvasHeight: Float,
    dynamicItemWidth: Float,
    dynamicItemHeight: Float,
    onPositionUpdate: (OffsetData) -> Unit
) {

    val denormalizedInitialPosition = remember(initialPosition) {
        val (x, y) = CoordinateNormalizer.denormalizeCoordinates(
            initialPosition.x, initialPosition.y, canvasWidth, canvasHeight, dynamicItemWidth, dynamicItemHeight
        )
        OffsetData(x, y)
    }

    var position by remember { mutableStateOf(denormalizedInitialPosition) }

    Box(
        modifier = Modifier
            .offset { IntOffset(position.x.toInt(), position.y.toInt()) }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->

                    val newX = (position.x + dragAmount.x)
                        .coerceIn(0f, canvasWidth - 375f)
                    val newY = (position.y + dragAmount.y)
                        .coerceIn(0f, canvasHeight - 275f)


                    position = OffsetData(newX, newY)

                    val (normalizedX, normalizedY) = CoordinateNormalizer.normalizeCoordinates(
                        newX, newY, canvasWidth, canvasHeight
                    )

                    println("Canvas Width: $canvasWidth, Canvas Height: $canvasHeight")
                    println("New Position X: $newX, Y: $newY")
                    println("Normalized X: $normalizedX, Y: $normalizedY")

                    // Notify parent of position change
                    onPositionUpdate(OffsetData(normalizedX, normalizedY))
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
    viewModel: ClothingItemViewModel
) {
    val categoryEnum = ClothingCategory.fromString(category)
    LaunchedEffect(categoryEnum) {
        viewModel.setCategoryFilter(categoryEnum)
    }

    val categoryItems by viewModel.filteredItems.collectAsState()
    val selectedWardrobe by viewModel.selectedWardrobe.collectAsState()
    val wardrobes by viewModel.cachedWardrobes.collectAsState()

    var selectedItemKeys by remember { mutableStateOf(setOf<Pair<String, ClothingCategory>>()) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutfitScreenHeader(
            onExit = { navController.navigate(Routes.HomeTab) },
            title = "Select $category"
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
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
            WardrobeDropdown(
                wardrobes = wardrobes,
                selectedWardrobe = selectedWardrobe,
                onWardrobeSelected = { wardrobe ->
                    viewModel.setWardrobeFilter(wardrobe)
                }
            )

            Spacer(Modifier.width(8.dp))

            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    isSelectionMode = !isSelectionMode
                    if (!isSelectionMode) {
                        selectedItemKeys = emptySet()
                    }
                }
            )
        }

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(categoryItems.size) { index ->
                    val item = categoryItems[index]
                    val itemKey = item.id to item.itemType

                    CategoryItem(
                        item = item,
                        isSelected = selectedItemKeys.contains(itemKey),
                        isSelectionMode = isSelectionMode,
                        onItemSelected = { selectedItemKeys = selectedItemKeys.toMutableSet().apply {
                            if (contains(itemKey)) remove(itemKey) else add(itemKey)
                        }},
                        onItemClicked = { clickedItem ->
                            navController.navigate(
                                Routes.CategoryItemDetailScreen(
                                    clickedItem.wardrobeId.toString(),
                                    clickedItem.id,
                                    clickedItem.itemType.toString()
                                )
                            )
                        }
                    )
                }
            }
        }

        if (isSelectionMode) {
            OutfitScreenFooter(
                onDone = {
                    val selectedItems = categoryItems.filter {
                        selectedItemKeys.contains(it.id to it.itemType)
                    }
                    viewModel.addSelectedItems(selectedItems)
                    navController.navigate(Routes.CreateOutfitScreen.Default)
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
        val item = viewModel.getItemDetail(itemId)
        selectedItem = item

        // Fetch the wardrobe name using the wardrobeId from the selected item
        wardrobeName = viewModel.selectedWardrobe.value?.wardrobeName ?: "Unknown Wardrobe"
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