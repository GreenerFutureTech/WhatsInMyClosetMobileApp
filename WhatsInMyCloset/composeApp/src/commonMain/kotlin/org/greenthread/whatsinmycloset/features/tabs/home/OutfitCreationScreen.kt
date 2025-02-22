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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.jetbrains.compose.resources.painterResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.top1



@Composable
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
}


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
fun OutfitComplete(
    onSave: () -> Unit,
    onAddToCalendar: (String) -> Unit, // Pass date as String
    onCreateNew: () -> Unit,
    selectedClothingItems: List<ClothingItem>
) {
    val isAddToCalendarEnabled = selectedClothingItems.isNotEmpty()

    // Show calendar dialog
    var showCalendarDialog by remember { mutableStateOf(false) }

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
            title = "Outfit Complete!",
            onDone = null
        )

        // Outfit collage area (Preview or actual implementation)
        OutfitCollageArea(selectedClothingItems)

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onSave, modifier = Modifier.fillMaxWidth(), enabled = true) {
                Text("Save Outfit")
            }

            // Add to Calendar button
            Button(
                onClick = { showCalendarDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = isAddToCalendarEnabled
            ) {
                Text("Add to Calendar")
            }

            Button(onClick = onCreateNew, modifier = Modifier.fillMaxWidth(), enabled = true) {
                Text("Create New Outfit")
            }
        }

        // Show Calendar Dialog
        if (showCalendarDialog) {
            CalendarDialog(
                onDismiss = { showCalendarDialog = false },
                onDateSelected = { selectedDate ->
                    onAddToCalendar(selectedDate) // Pass the selected date to the callback
                    showCalendarDialog = false // Close the dialog
                }
            )
        }
    }
}


@Composable
fun OutfitCollageArea(selectedClothingItems: List<ClothingItem>) {
    // To track the position of each item
    val itemPositions = remember { mutableStateListOf<OffsetData>() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
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
fun ClothingCategorySelection(onSelectCategory: (String) -> Unit) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Creating two buttons per row
        listOf("Tops", "Bottoms", "Footwear", "Accessories").chunked(2)
            .forEach { categoryPair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Add spacing between buttons
            ) {
                categoryPair.forEach { category ->
                    Button(
                        onClick = {
                            selectedCategory = category
                            onSelectCategory(category)
                        },
                        modifier = Modifier.weight(1f), // Ensure buttons are evenly spaced
                        enabled = selectedCategory != category // Disable if already selected
                    ) {
                        Text("$category")
                    }
                }
            }
        }
    }
}

// highlight selected clothing items
// more than 1 items more the same category can be selected by the user
@Composable
fun CategoryItemsScreen(
    category: String,
    onDone: () -> Unit
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
            onGoBack = { /* Handle back action */ },
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
        OutfitScreenFooter(onDone = onDone, isDoneEnabled = selectedItemIds.value.isNotEmpty())
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