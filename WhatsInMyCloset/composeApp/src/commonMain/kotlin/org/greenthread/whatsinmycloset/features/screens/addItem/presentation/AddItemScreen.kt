package org.greenthread.whatsinmycloset.features.screens.addItem.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import org.greenthread.whatsinmycloset.CameraManager
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.dto.ItemDto
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe
import org.greenthread.whatsinmycloset.subjectSegmentation
import org.greenthread.whatsinmycloset.toBitmap
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
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedWardrobe by remember { mutableStateOf<Wardrobe?>(null) }
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var bitmapFile: Any? = null

    var hasSegmented by remember { mutableStateOf(false) } // Prevent re-segmentation
    val buttonText = remember { mutableStateOf("Take Photo") } // <-- Track button text

    val cachedWardrobes by viewModel.cachedWardrobes.collectAsState()
    if (cachedWardrobes.isNotEmpty()) {
        selectedWardrobe = cachedWardrobes.getOrNull(0)
    }

    val categories = ClothingCategory.entries
        .filterNot { it == ClothingCategory.ALL }
        .map { it.categoryName }
    val contentWidth = 280.dp

    LaunchedEffect(itemImage) {
        //
        //
        //To enable image segmentation
        //
        //
          itemImage?.let { imageBytes ->
            println("Segmentation part 1")

           bitmap = imageBytes.toImageBitmap()
           bitmapFile = imageBytes.toBitmap()

           if (!hasSegmented) {  // Only run segmentation once
                subjectSegmentation(imageBytes) { result ->
                    if (result != null) {
                        println("Segmentation successful!")
                        itemImage = result
                        bitmap = result.toImageBitmap()  // ✅ Triggers recomposition once
                        hasSegmented = true
                    } else {
                        println("Segmentation failed!")
                    }
                }
           }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Wardrobe Dropdown
        WardrobeDropdown(
            wardrobes = cachedWardrobes,
            onWardrobeSelected = { selectedWardrobe = it },
            modifier = Modifier.width(contentWidth)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Category Dropdown
        CategoryDropdown(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            modifier = Modifier.width(contentWidth)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.width(contentWidth),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Item Name",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.width(contentWidth),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Brand",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = itemBrand,
                onValueChange = { itemBrand = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Size and Condition in one row
        Row(
            modifier = Modifier.width(contentWidth),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Size",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = itemSize,
                    onValueChange = { itemSize = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    singleLine = true
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Condition",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = itemCondition,
                    onValueChange = { itemCondition = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.width(contentWidth),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Tags (comma-separated)",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = itemTags,
                onValueChange = { itemTags = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        cameraManager.TakePhotoButton(buttonText = buttonText,
            onPhotoTaken = { imageBytes ->
                itemImage = imageBytes
                bitmap = imageBytes.toImageBitmap()
                buttonText.value = "Replace Photo" // <-- Change button text on photo taken
                hasSegmented = false
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Image Preview
        bitmap?.let { img ->
            Image(
                bitmap = img,
                contentDescription = "Captured Image",
                modifier = Modifier
                    .size(225.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Add Item Button
        Button(
            onClick = {
                val item = ItemDto(
                    id = Random.nextLong().toString(),
                    name = itemName,
                    wardrobeId = selectedWardrobe?.id ?: "",
                    itemType = selectedCategory ?: "",
                    mediaUrl = null.toString(),
                    tags = itemTags.split(",").map { it.trim() },
                    condition = itemCondition,
                    brand = itemBrand,
                    size = itemSize,
                    createdAt = Clock.System.now().toString()
                )

                viewModel.addItem(item, itemImage) { success, error ->
                    if (success) onBack()
                }
            },
            modifier = Modifier.width(contentWidth),
            enabled = itemName.isNotBlank() && selectedWardrobe != null && selectedCategory != null
        ) {
            Text("Add Item", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun CategoryDropdown(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Category Label (위에 배치)
        Text(
            text = "Category",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray, // 회색 텍스트
            modifier = Modifier.padding(start = 4.dp)
        )

        // Dropdown Button
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp), // 아주 살짝 둥근 모서리
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedCategory ?: "Select Category",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedCategory == null) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = "Category Dropdown",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                categories.filter { it != "Choose a Category!" }.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WardrobeDropdown(
    wardrobes: List<Wardrobe>,
    onWardrobeSelected: (Wardrobe) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedWardrobe by remember { mutableStateOf(wardrobes.firstOrNull()) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Wardrobe",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp)
        )

        // Dropdown Button
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedWardrobe?.wardrobeName ?: "Select Wardrobe",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedWardrobe == null) {
                            MaterialTheme.colorScheme.onSurface.copy()
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = "Wardrobe Dropdown",
                        tint = MaterialTheme.colorScheme.onSurface.copy(),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                wardrobes.forEach { wardrobe ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = wardrobe.wardrobeName,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = {
                            selectedWardrobe = wardrobe
                            onWardrobeSelected(wardrobe)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}