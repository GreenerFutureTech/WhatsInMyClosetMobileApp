package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import org.greenthread.whatsinmycloset.CameraManager

@Composable
fun AddItemScreen(cameraManager: CameraManager) {
    var itemName by remember { mutableStateOf("") }
    var itemImage by remember { mutableStateOf<ByteArray?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text("Item Name") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Use the TakePhotoButton composable
        cameraManager.TakePhotoButton { imageBytes ->
            itemImage = imageBytes
        }

        Spacer(modifier = Modifier.height(16.dp))

        itemImage?.let { imageBytes ->
            val bitmap = ImageBitmap.imageFromBytes(imageBytes)
            Image(
                bitmap = bitmap,
                contentDescription = "Captured Image",
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Handle adding the item (e.g., save to a repository)
        }) {
            Text("Add Item")
        }
    }
}

// Helper function to convert ByteArray to ImageBitmap
fun ImageBitmap.Companion.imageFromBytes(bytes: ByteArray): ImageBitmap {
/*    val inputStream = ByteArrayInputStream(bytes)
    val bufferedImage = ImageIO.read(inputStream)
    val raster = bufferedImage.raster
    val width = bufferedImage.width
    val height = bufferedImage.height
    val bitmap = ImageBitmap(width, height)
    val buffer = IntArray(width * height)
    raster.getPixels(0, 0, width, height, buffer)
    bitmap.writePixels(buffer)*/
    return ImageBitmap(100,100)
}