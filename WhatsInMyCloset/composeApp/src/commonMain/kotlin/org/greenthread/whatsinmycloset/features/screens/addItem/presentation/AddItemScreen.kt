package org.greenthread.whatsinmycloset.features.screens.addItem.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import org.greenthread.whatsinmycloset.CameraManager
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe
import org.greenthread.whatsinmycloset.toImageBitmap

import kotlin.random.Random

@Composable
fun AddItemScreen(viewModel: AddItemScreenViewModel, cameraManager: CameraManager, onBack: () -> Unit) {
    var itemName by remember { mutableStateOf("") }
    var itemTags by remember { mutableStateOf("") }
    var itemBrand by remember { mutableStateOf("") }
    var itemSize by remember { mutableStateOf("") }
    var itemCondition by remember { mutableStateOf("") }

    var itemImage by remember { mutableStateOf<ByteArray?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>("Tops") }
    var selectedWardrobe by remember { mutableStateOf<Wardrobe?>(null) }

    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var bitmapFile: Any? = null

    var hasSegmented by remember { mutableStateOf(false) } // Prevent re-segmentation

    val cachedWardrobes by viewModel.cachedWardrobes.collectAsState()
    if (cachedWardrobes.isNotEmpty()) {
        selectedWardrobe = cachedWardrobes.getOrNull(0)
    }

    LaunchedEffect(itemImage) {
        //
        //
        //To enable image segmentation
        //
        //
/*        itemImage?.let { imageBytes ->
            println("Segmentation part 1")

            bitmap = imageBytes.toImageBitmap()
            bitmapFile = imageBytes.toBitmap()

            if (!hasSegmented) {  // Only run segmentation once
                hasSegmented = true
                subjectSegmentation(imageBytes) { result ->
                    if (result != null) {
                        println("Segmentation successful!")
                        itemImage = result
                        bitmap = result.toImageBitmap()  // ✅ Triggers recomposition once
                    } else {
                        println("Segmentation failed!")
                    }
                }
            }
        }*/
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val categories =
            listOf("Choose a Category!") + ClothingCategory.entries.map { it.categoryName }

        WardrobeDropdown(
            wardrobes = cachedWardrobes,
            onWardrobeSelected = { selectedWardrobe = it })
        Spacer(modifier = Modifier.height(16.dp))

        // Pass selectedCategory and a setter function to update it
        CategoryDropdown(
            categories = categories,
            selectedCategory = selectedCategory ?: "Choose a Category!",
            onCategorySelected = { selectedCategory = it }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // TextField for Name
        OutlinedTextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text("Item Name") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // TextField for Tags
        OutlinedTextField(
            value = itemTags,
            onValueChange = { itemTags = it },
            label = { Text("Tags (comma-separated)") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // TextField for Brand
        OutlinedTextField(
            value = itemBrand,
            onValueChange = { itemBrand = it },
            label = { Text("Brand") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // TextField for Size
        OutlinedTextField(
            value = itemSize,
            onValueChange = { itemSize = it },
            label = { Text("Size") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // TextField for Size
        OutlinedTextField(
            value = itemCondition,
            onValueChange = { itemCondition = it },
            label = { Text("Condition") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Use the TakePhotoButton composable
        cameraManager.TakePhotoButton { imageBytes ->
            itemImage = imageBytes
            bitmap = imageBytes.toImageBitmap()
        }

        Spacer(modifier = Modifier.height(16.dp))

        bitmap?.let { img ->
            Image(
                bitmap = img,
                contentDescription = "Captured Image",
                modifier = Modifier.size(225.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val item = ItemDto(
                id = "Commemorative ID",
                name = itemName,
                wardrobeId = selectedWardrobe?.id ?: "null",
                itemType = selectedCategory ?: "null",
                mediaUrl = null.toString(), // Will be set after uploading image
                tags = itemTags.split(",").map { it.trim() },
                condition = itemCondition, // Consider adding UI input for this
                brand = itemBrand,
                size = itemSize,
                createdAt = Clock.System.now().toString()
            )

            viewModel.addItem(item, itemImage) { success, error ->
                if (success) {
                    onBack()
                } else {
                    //TODO: show dialogue , no connection.
                }
            }
        }) {
            Text("Add Item")
        }
    }
}

/*
fun ImageBitmap.Companion.imageFromBytes(bytes: ByteArray): ImageBitmap {
    val bitmap = BitMap.decodeByteArray(bytes, 0, bytes.size)
    return bitmap.asImageBitmap()
}


// Helper function to convert ByteArray to ImageBitmap
fun ImageBitmap.Companion.imageFromBytes(bytes: ByteArray): ImageBitmap {
    val inputStream = ByteArrayInputStream(bytes)
    val bufferedImage = ImageIO.read(inputStream)
    val raster = bufferedImage.raster
    val width = bufferedImage.width
    val height = bufferedImage.height
    val bitmap = ImageBitmap(width, height)
    val buffer = IntArray(width * height)
    raster.getPixels(0, 0, width, height, buffer)
    bitmap.readPixels(buffer)

    return ImageBitmap(100,100)
}
*/

@Composable
fun CategoryDropdown(categories: List<String>,
                     selectedCategory: String,
                     onCategorySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally // Center the dropdown
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.width(200.dp) // Adjust width as needed
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedCategory,
                    style = MaterialTheme.typography.headlineSmall, // Larger font size
                    modifier = Modifier.weight(1f) // Allows text to take available space
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(200.dp) // Match width to the OutlinedButton
        ) {
            categories.filter { category -> category != "Choose a Category!" }.forEach { category ->
                DropdownMenuItem(
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.headlineSmall // Match font size in dropdown items
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun WardrobeDropdown(wardrobes: List<Wardrobe>,
                     onWardrobeSelected: (Wardrobe) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedWardrobe by remember { mutableStateOf(wardrobes.firstOrNull()) } // Default to the first category if available

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally // Center the dropdown
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.width(200.dp) // Adjust width as needed
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedWardrobe?.wardrobeName ?: "Choose a Wardrobe!",
                    style = MaterialTheme.typography.headlineSmall, // Larger font size
                    modifier = Modifier.weight(1f) // Allows text to take available space
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(300.dp) // Match width to the OutlinedButton
        ) {
            wardrobes.forEach { wardrobe ->
                DropdownMenuItem(
                    onClick = {
                        onWardrobeSelected(wardrobe)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = wardrobe.wardrobeName,
                            style = MaterialTheme.typography.headlineMedium // Match font size in dropdown items
                        )
                    }
                )
            }
        }
    }
}