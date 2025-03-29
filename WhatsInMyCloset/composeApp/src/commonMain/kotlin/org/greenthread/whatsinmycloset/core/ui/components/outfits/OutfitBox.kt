package org.greenthread.whatsinmycloset.core.ui.components.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.utilities.CoordinateNormalizer
import org.greenthread.whatsinmycloset.features.tabs.social.data.OutfitState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.no_items_found
@Composable
fun OutfitBox(
    state: OutfitState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .fillMaxWidth(0.8f) // Adjust width to ensure it doesn't take all space
        .aspectRatio(1f) // Keep consistent proportion between the items
        .background(
            color = MaterialTheme.colorScheme.surface,
            shape =  RoundedCornerShape(8.dp)
        ),
    ){
        // Use BoxWithConstraints to get the actual dimensions
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val boxWidth = constraints.maxWidth.toFloat()
            val boxHeight = constraints.maxHeight.toFloat()

            val (dynamicItemWidth, dynamicItemHeight) = CoordinateNormalizer.calculateDynamicItemSize(boxWidth, boxHeight)

            when {
                state.isLoading -> Box(
                    modifier = Modifier.matchParentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                state.items.isEmpty() -> Text(stringResource(Res.string.no_items_found))
                else -> {
                    state.items.forEach { item ->
                        val position = state.itemIds.find { it.id == item.id }

                        position?.let {
                            // Denormalize coordinates for the current OutfitBox dimensions
                            val (scaledX, scaledY) = CoordinateNormalizer.denormalizeCoordinates(
                                it.x ?: 0f,
                                it.y ?: 0f,
                                currentCanvasWidth = boxWidth,
                                currentCanvasHeight = boxHeight
                            )

                            LoadImage(
                                imageUrl = item.mediaUrl,
                                contentDescription = "Clothing item",
                                x = scaledX.coerceIn(0f, boxWidth - dynamicItemWidth), // Limit x coordinate
                                y = scaledY.coerceIn(0f, boxHeight - dynamicItemHeight), // Limit y coordinate
                                itemWidth = dynamicItemWidth,
                                itemHeight = dynamicItemHeight
                            )
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalResourceApi::class)
@Composable
fun LoadImage(imageUrl: String, contentDescription: String, x: Float, y: Float, itemWidth: Float, itemHeight: Float) {
    var loadFailed by remember { mutableStateOf(false) }

    // Reduced image size
    val imageSize = 45.dp

    Box(
        modifier = Modifier
            .offset(x = x.dp, y = y.dp)
            .size(width = itemWidth.dp, height = itemHeight.dp)
    ) {
        AsyncImage(
            model = if (loadFailed) Res.getUri("drawable/noImage.png") else imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}