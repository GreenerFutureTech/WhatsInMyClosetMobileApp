package org.greenthread.whatsinmycloset.core.ui.components.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
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
        contentAlignment = Alignment.Center
    ){
        when {
            state.isLoading -> CircularProgressIndicator()
            state.items.isEmpty() -> Text(stringResource(Res.string.no_items_found))
            else -> {
                // TODO load items in a different composable so it can be displayed like one image
                state.items.forEach { item ->
                    LoadImage(
                        imageUrl = item.mediaUrl,
                        contentDescription = "Clothing item",
                        // TODO replace with coordinates from the backend when implemented
                        x = 0f,
                        y = 0f
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalResourceApi::class)
@Composable
fun LoadImage(imageUrl: String, contentDescription: String, x: Float, y: Float) {
    var loadFailed by remember { mutableStateOf(false) }
    AsyncImage(
        model = if (loadFailed) Res.getUri("drawable/noImage.png") else imageUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(90.dp)
            .offset(x = x.dp, y = y.dp) //Positioning based on backend coordinates
    )
}