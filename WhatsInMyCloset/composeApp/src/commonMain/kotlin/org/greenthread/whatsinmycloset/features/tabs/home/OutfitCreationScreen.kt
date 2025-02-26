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
import org.greenthread.whatsinmycloset.core.domain.models.generateRandomClothingItems
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.generateSampleClothingItems
import org.greenthread.whatsinmycloset.core.viewmodels.ClothingItemViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.OutfitViewModel
import org.jetbrains.compose.resources.painterResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.top1


// the screen that opens up when user clicks on "Create Outfit" button
// from Home Tab
/*@Composable
fun OutfitScreen(
    onDone: () -> Unit,
    selectedClothingItems: List<ClothingItem>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Header
        OutfitScreenHeader(
            onGoBack = { /* Handle back action */ },
            onExit = { /* Handle exit action */ },
            title = "Create Your Outfit"
        )

        // Outfit collage preview - Displaying a blank area
        OutfitCollageArea(selectedClothingItems)

        Spacer(modifier = Modifier.height(16.dp))

        // Footer with Done button
        OutfitScreenFooter(onDone = onDone, isDoneEnabled = selectedClothingItems.isNotEmpty())
    }
}*/


@Composable
fun DisplayCategoryItems(
    categoryItems: List<ClothingItem>,
    onItemClick: (ClothingItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(categoryItems.size) { item ->

            val item = categoryItems[item % categoryItems.size]

            if (item.category == ClothingCategory.TOPS)
            {
                Image(
                    painter = painterResource(Res.drawable.top1),
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) } // Handle click
                )
            }
            else if (item.category == ClothingCategory.BOTTOMS)
            {
                Image(
                    painter = painterResource(Res.drawable.top1),
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) } // Handle click
                )
            }
            else if (item.category == ClothingCategory.FOOTWEAR)
            {
                Image(
                    painter = painterResource(Res.drawable.top1),
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) } // Handle click
                )
            }
            else if (item.category == ClothingCategory.ACCESSORIES)
            {
                Image(
                    painter = painterResource(Res.drawable.top1),
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) } // Handle click
                )
            }
        }
    }
}




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
    val currentOutfit by outfitViewModel.currentOutfit.collectAsState()

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
            onExit = { /* Handle exit action */ },  // Discard Outfit Creation -- pop up
            title = "Create Your Outfit"
        )

        // Outfit collage area (Preview or actual implementation)
        OutfitCollageArea(selectedClothingItems)

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
                        // Navigate to the category items screen
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
                                // Save the current outfit
                                if (currentOutfit != null) {
                                    outfitViewModel.createOutfit(selectedClothingItems)
                                    outfitViewModel.saveOutfit(currentOutfit!!)
                                }
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

    // Show Outfit Saved Dialog
    if (isOutfitSaved) {
        OutfitSaved(
            navController = navController,
            onDismiss = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            },
            viewModel = outfitViewModel
        )
    }

}

// show the items user selected to create an outfit
@Composable
fun OutfitCollageArea(selectedClothingItems: List<ClothingItem>) {
    // To track the position of each item
    val itemPositions = remember { mutableStateListOf<OffsetData>() }
    //selectedClothingItems: List<ClothingItem>

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
    val position = remember { mutableStateOf(OffsetData(100f, 100f)) }

    Box(
        modifier = Modifier
            .offset { IntOffset(position.value.x.toInt(), position.value.y.toInt()) }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    // Update the position based on the drag gesture
                    position.value = OffsetData(position.value.x + dragAmount.x, position.value.y + dragAmount.y)
                    itemPositions[itemIndex] = position.value
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
            val category = categories[index]
            Button(
                onClick = { onSelectCategory(category) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
            ) {
                Text(
                    text = category.categoryName,
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
    category: String,
    onDone: (List<ClothingItem>) -> Unit, // Callback to return selected items
    onBack: () -> Unit,
    viewModel: ClothingItemViewModel // Inject the ClothingItemViewModel
) {
    // Track selected item IDs
    val selectedItemIds = remember { mutableStateOf(mutableSetOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Heading for the selected category
        OutfitScreenHeader(
            onGoBack = onBack,
            onExit = { /* Handle exit action */ },
            title = "Select $category"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Generate or fetch items for the category
        val items = generateRandomClothingItems(category, 18)

        // Use a Box with a Modifier.weight(1f) to allow scrolling within the grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)  // This makes the grid scrollable
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(items.size) { index ->
                    val item = items[index]
                    CategoryItemBox(
                        item = item,
                        isSelected = selectedItemIds.value.contains(item.id),
                        onItemSelected = { selectedItem ->
                            // Toggle selection based on ID
                            if (selectedItemIds.value.contains(selectedItem.id)) {
                                selectedItemIds.value.remove(selectedItem.id)
                            } else {
                                selectedItemIds.value.add(selectedItem.id)
                            }
                            // Update state to trigger recomposition
                            selectedItemIds.value = selectedItemIds.value.toMutableSet()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer with Done button
        OutfitScreenFooter(
            onDone = {
                // Update the ViewModel with the selected items
                val selectedItems = items.filter { selectedItemIds.value.contains(it.id) }
                viewModel.addClothingItems(selectedItems) // Add selected items to the ViewModel
                onDone(selectedItems) // Navigate back
            },
            isDoneEnabled = selectedItemIds.value.isNotEmpty()
        )
    }
}



@Composable
fun CategoryItemBox(
    item: ClothingItem,
    isSelected: Boolean,
    onItemSelected: (ClothingItem) -> Unit
) {
    // Border color based on selection status
    val borderColor = if (isSelected) Color.Red else Color.Transparent

    Box(
        modifier = Modifier
            .padding(4.dp)
            .border(2.dp, borderColor, shape = RoundedCornerShape(8.dp))
            .clickable { onItemSelected(item) } // Toggle selection
            .padding(8.dp)
    ) {
        Image(
            painter = when (item.category)
            {
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


@Composable
fun SelectClothingItem(

)
{
    // highlight clothing item, say "Tops" when user clicks on it
    // user can select 1 or more of the clothing items.
    // For example, user can select more than 1 tops
    // when user selects atleast 1 clothing item, enable the done button
}

@Composable
fun OutfitScreenHeader(
    onGoBack: () -> Unit,
    onExit: () -> Unit,
    title: String,
    onDone: (() -> Unit)? = null // optional

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