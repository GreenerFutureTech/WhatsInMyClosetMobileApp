package org.greenthread.whatsinmycloset.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyGridColourBox
import org.greenthread.whatsinmycloset.core.ui.components.listItems.generateRandomItems
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.HomeTabScreen

// Preview Composable
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewLazyGridColourBox() {
    val randomItems = generateRandomItems(10) // Generate 10 random items for the preview
    LazyGridColourBox(items = randomItems)
}

// Preview Composable
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewWardrobeScreen() {
    val mockNavController = rememberNavController()

    androidx.compose.material.MaterialTheme {
        HomeTabScreen(viewModel = null, navController = mockNavController)
    }
}

// Preview Composable
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewAddItemScreen() {

    androidx.compose.material.MaterialTheme {
        //AddItemScreen(null)
    }
}