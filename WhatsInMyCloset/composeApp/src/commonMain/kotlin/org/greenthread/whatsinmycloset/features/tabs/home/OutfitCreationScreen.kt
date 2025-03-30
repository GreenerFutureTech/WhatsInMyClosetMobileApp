package org.greenthread.whatsinmycloset.features.tabs.home


import androidx.compose.foundation.BorderStroke
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
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import org.greenthread.whatsinmycloset.features.tabs.social.presentation.TagsSection
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.greenthread.whatsinmycloset.theme.outlineVariantLight
import org.greenthread.whatsinmycloset.theme.secondaryLight
import org.jetbrains.compose.resources.painterResource
import whatsinmycloset.composeapp.generated.resources.Res
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
            Spacer(Modifier.height(10.dp))
            // Header
            OutfitScreenHeader(
                title = "Create Your Outfit"
            )

            Spacer(Modifier.height(16.dp))

            // Outfit collage area will show the selectedClothingItems
            OutfitCollageArea(
                selectedClothingItems = selectedItems,
                onPositionUpdate = onPositionUpdate
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Clothing category selection
            ClothingCategorySelection { selectedCategory ->
                val categoryEnum = ClothingCategory.fromString(selectedCategory.categoryName)
                navController.navigate(Routes.CategoryItemScreen(categoryEnum.toString()))
            }

           // Reset button
            Button(
                onClick = {
                    // Discard the current outfit and create a new one
                    outfitViewModel.discardCurrentOutfit()
                    outfitViewModel.clearOutfitState() // Clear the outfit state
                    clothingItemViewModel.clearClothingItemState() // Clear the selected items state
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedItems.isNotEmpty()
            ) {
                Text("Reset")
            }

            Spacer(modifier = Modifier.padding(10.dp))

            // Create New Outfit button
            Button(
                onClick = {
                    outfitViewModel.createOutfit(
                        name = "New Outfit",
                        onSuccess = {
                            navController.navigate(Routes.OutfitSaveScreen)
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedItems.isNotEmpty()
            ) {
                Text("Save Outfit")
            }
        }

        // Show Exit Dialog
        if (showExitDialog) {
            DiscardOutfitDialog(
                onConfirm = {
                    showExitDialog = false
                    // Discard the current outfit and create a new one
                    outfitViewModel.discardCurrentOutfit()
                    outfitViewModel.clearOutfitState() // Clear the outfit state
                    clothingItemViewModel.clearClothingItemState() // Clear the selected items state
                    navController.navigate(Routes.HomeTab) {
                        popUpTo(Routes.HomeTab) { inclusive = true }
                    }
                },
                // close exit dialog
                // don't clear outfit states
                onDismiss = {
                    showExitDialog = false
                    // Don't navigate - stay on current screen
                }
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
            .height(300.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = secondaryLight,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
    ) {

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
            .border(1.dp, secondaryLight, RoundedCornerShape(12.dp))
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
    val categories = ClothingCategory.entries.toTypedArray()

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories.size) { index ->
            val thisItem = categories[index]

            OutlinedButton(
                onClick = { onSelectCategory(thisItem) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = thisItem.categoryName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp
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
    clothingItemViewModel: ClothingItemViewModel
) {
    val categoryEnum = ClothingCategory.fromString(category)
    LaunchedEffect(categoryEnum) {
        if (categoryEnum == ClothingCategory.ALL)
        {
            clothingItemViewModel.setCategoryFilter(null)
        }
        else
        {
            clothingItemViewModel.setCategoryFilter(categoryEnum)
        }
    }

    val categoryItems by clothingItemViewModel.filteredItems.collectAsState()
    val selectedWardrobe by clothingItemViewModel.selectedWardrobe.collectAsState()
    val wardrobes by clothingItemViewModel.cachedWardrobes.collectAsState()

    var selectedItemKeys by remember { mutableStateOf(setOf<Pair<String, ClothingCategory>>()) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutfitScreenHeader(title = category)

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Wardrobe",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                WardrobeDropdown(
                    wardrobes = wardrobes,
                    selectedWardrobe = selectedWardrobe,
                    onWardrobeSelected = { wardrobe ->
                        clothingItemViewModel.setWardrobeFilter(wardrobe)
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Item Selection",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
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
        }

        if (isSelectionMode) {
            Text(
                text = "Selected Items ${selectedItemKeys.size}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.weight(1f)
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

        OutfitScreenFooter(
            onDone = {
                val selectedItems = categoryItems.filter {
                    selectedItemKeys.contains(it.id to it.itemType)
                }
                clothingItemViewModel.addSelectedItems(selectedItems)
                navController.navigate(Routes.CreateOutfitScreen)
            },
            isDoneEnabled = true
        )
    }
}/* end of CategoryItemsScreen */



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
            .border(2.dp, if (isSelected) secondaryLight else Color.Transparent, RoundedCornerShape(8.dp))
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
    wardrobeId: String,
    itemId: String,
    category: ClothingCategory,
    onBack: () -> Unit,
    clothingItemViewModel: ClothingItemViewModel // Inject the ClothingItemViewModel

) {
    var selectedItem by remember { mutableStateOf<ClothingItem?>(null) }
    var wardrobeName by remember { mutableStateOf("Unknown Wardrobe") }

    LaunchedEffect(wardrobeId, itemId, category) {
        val item = clothingItemViewModel.getItemDetail(itemId)
        selectedItem = item
        wardrobeName = clothingItemViewModel.selectedWardrobe.value?.wardrobeName ?: "Unknown Wardrobe"
    }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        // Header
        OutfitScreenHeader(
            title = selectedItem!!.name
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Image Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(2.dp)
            ) {
                AsyncImage(
                    model = selectedItem!!.mediaUrl,
                    contentDescription = selectedItem!!.name,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tags Section
        if (selectedItem!!.tags.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                TagsSection(tags = selectedItem!!.tags)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Column(
        ){
            Column() {
                Text(
                    text = "WARDROBE",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = wardrobeName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = outlineVariantLight
            )

            // Category Info
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "CATEGORY",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "${selectedItem!!.itemType}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = outlineVariantLight
            )
        }
    }
}

@Composable
fun OutfitScreenHeader(
    title: String

) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
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
