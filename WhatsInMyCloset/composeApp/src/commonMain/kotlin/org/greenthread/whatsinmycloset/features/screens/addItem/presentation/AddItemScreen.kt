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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import org.greenthread.whatsinmycloset.subjectSegmentation
import org.greenthread.whatsinmycloset.toBitmap
import org.greenthread.whatsinmycloset.toImageBitmap
import kotlin.random.Random

@Composable
fun AddItemScreen(viewModel: AddItemScreenViewModel, cameraManager: CameraManager, onBack: () -> Unit) {
    var itemName by remember { mutableStateOf("") }
    var itemImage by remember { mutableStateOf<ByteArray?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>("Tops") }
    var selectedWardrobe by remember { mutableStateOf<String?>("Winter Collection") }

    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var bitmapFile : Any? = null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        val categories = listOf("Choose a Category!") + ClothingCategory.entries.map { it.categoryName }

        val wardrobes = viewModel.getWardrobes()
        WardrobeDropdown(
            wardrobes = wardrobes.map { it.wardrobeName },
            selectedWardrobe = selectedWardrobe ?: "Choose a Wardrobe!",
            onWardrobeSelected = { selectedWardrobe = it })
        Spacer(modifier = Modifier.height(16.dp))

        // Pass selectedCategory and a setter function to update it
        CategoryDropdown(
            categories = categories,
            selectedCategory = selectedCategory ?: "Choose a Category!",
            onCategorySelected = { selectedCategory = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Use the TakePhotoButton composable
        cameraManager.TakePhotoButton { imageBytes ->
            itemImage = imageBytes
        }

        Spacer(modifier = Modifier.height(16.dp))

        itemImage?.let { imageBytes ->
            println("Segmentation part 1")

            bitmap = imageBytes.toImageBitmap()
            bitmapFile = imageBytes.toBitmap()

            subjectSegmentation(imageBytes) { result ->
                if (result != null) {
                    println("Segmentation successful!")
                    bitmap = result

                } else {
                    println("Segmentation failed!")
                    // Handle the failure
                }
            }

            bitmap?.let { img ->
                Image(
                    bitmap = img,
                    contentDescription = "Captured Image",
                    modifier = Modifier.size(300.dp)
                )
            }

        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val item = ItemDto(
                id = Random.nextInt(1000, 100000).toString(),
                wardrobeId = selectedWardrobe?: "null",
                itemType = selectedCategory?: "null",
                mediaUrl = null.toString(), // Will be set after uploading image
                tags = emptyList(),
                condition = "",
                brand = "",
                size = "",
                createdAt = Clock.System.now().toString()
            )

            viewModel.addItem(item, itemImage) { success, error ->
                if (success) {
                    onBack()
                } else {
                    println("failure")
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
fun WardrobeDropdown(wardrobes: List<String>,
                     selectedWardrobe: String,
                     onWardrobeSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedWadrobe by remember { mutableStateOf(wardrobes.firstOrNull() ?: "") } // Default to the first category if available

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
                    text = selectedWadrobe,
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
                            text = wardrobe,
                            style = MaterialTheme.typography.headlineMedium // Match font size in dropdown items
                        )
                    }
                )
            }
        }
    }
}