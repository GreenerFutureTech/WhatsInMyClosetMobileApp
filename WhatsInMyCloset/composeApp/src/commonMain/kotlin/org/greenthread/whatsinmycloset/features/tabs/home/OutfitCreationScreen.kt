package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyGridCalendarUI
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyGridColourBox
import org.greenthread.whatsinmycloset.core.ui.components.listItems.generateRandomItems


@Composable
fun OutfitScreen(
    onDone: () -> Unit,
    selectedClothingItems: List<ClothingItem>
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var categoryItems by remember { mutableStateOf<List<ClothingItem>>(emptyList()) }

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

        // Category selection
        ClothingCategorySelection { category ->
            selectedCategory = category
            categoryItems = generateRandomClothingItems(category, 15) // Load sample items when category changes
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer with Done button
        OutfitScreenFooter(onDone = onDone, isDoneEnabled = selectedClothingItems.isNotEmpty())
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

@Composable
fun CategoryItemsScreen(
    category: String,
    selectedItemIds: MutableSet<String>, // Track selected item IDs
    onDone: () -> Unit) {

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

        //val items = generateRandomItems(8) // Generate or fetch items for the category
        //LazyGridColourBox(items)

        val items = generateRandomClothingItems(category, 18)

        // Display category items in a LazyVerticalGrid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items.size) { index ->
                CategoryItemBox(
                    item = items[index],
                    isSelected = selectedItemIds.contains(items[index].id), // Compare by item ID
                    onItemSelected = { selectedItem ->
                        // Toggle selection based on ID
                        if (selectedItemIds.contains(selectedItem.id)) {
                            selectedItemIds.remove(selectedItem.id)
                        } else {
                            selectedItemIds.add(selectedItem.id)
                        }
                    }
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer with Done button
        OutfitScreenFooter(onDone = onDone, isDoneEnabled = selectedItemIds.isNotEmpty())
    }
}


@Composable
fun CategoryItemBox(
    item: ClothingItem,
    isSelected: Boolean,
    onItemSelected: (ClothingItem) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(75.dp) // Set item box height
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .clickable {
                onItemSelected(item)
            }
    ) {
        Text(
            text = item.name,
            modifier = Modifier.align(Alignment.Center)
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