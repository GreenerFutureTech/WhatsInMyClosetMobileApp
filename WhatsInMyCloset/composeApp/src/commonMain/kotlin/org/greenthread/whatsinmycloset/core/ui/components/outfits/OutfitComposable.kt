package org.greenthread.whatsinmycloset.core.ui.components.outfits

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.jetbrains.compose.resources.painterResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.default
import whatsinmycloset.composeapp.generated.resources.leggings


// Mapping local resources to a string mimicking an URL
// Use for development only while the database doesn't have images
val localImageMap = mapOf(
    "url_to_sweater.png" to Res.drawable.default,
    "url_to_leggings.png" to Res.drawable.leggings
)

// Repository to test the Outfit composable
// For development only
val itemRepository: Map<String, ClothingItem> = mapOf(
    "1" to ClothingItem(
        id = "1",
        name = "Red Sweater",
        wardrobeId = "1",
        itemType = "top",
        mediaUrl = "url_to_sweater.png", // Local resource key or remote URL
        tags= listOf("red", "casual"),
        "20202020"
//        x = 100f,
//        y = 200f
    ),
    "2" to ClothingItem(
        id = "2",
        name = "Pink Pattern Leggings",
        wardrobeId = "1",
        itemType = "bottom",
        mediaUrl = "url_to_leggings.png", // Local resource key or remote URL
        tags= listOf("pink", "casual"),
        "20202020"
//        x = 100f,
//        y = 300f
    )
)

@Composable
fun OutfitComposable(outfit: Outfit) {
    Box(modifier = Modifier
        .height(150.dp)
        .fillMaxWidth()
        .background(
            color = Color.White,
            shape =  RoundedCornerShape(8.dp)
        )
        .clip(RoundedCornerShape(8.dp))
//        .padding(8.dp)
    ) {
        // Resolve itemIds to OutfitItem objects
        var x = 5f
        var y = 5f

        outfit.itemIds.forEach { itemId ->
            val item = itemRepository[itemId] ?: error("Item not found: $itemId")
            LoadImage(
                imageKey = item.mediaUrl.toString(),
                contentDescription = item.name,
                x = x,
                y = y
            )
            x += 60f
            y += 50f
        }
    }
}

@Composable
fun LoadImage(imageKey: String, contentDescription: String, x: Float, y: Float) {
    val isLocalDevelopment = true
    val painter = if (isLocalDevelopment) {
        // Load from local resources
        val drawableResId = localImageMap[imageKey] ?: error("Drawable not found: $imageKey")
        painterResource(drawableResId)
    } else {
        // Load from remote URL
        rememberAsyncImagePainter(imageKey)
    }

    Image(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .offset(x = x.dp, y = y.dp)
            .size(90.dp) // Adjust size as needed
    )
}