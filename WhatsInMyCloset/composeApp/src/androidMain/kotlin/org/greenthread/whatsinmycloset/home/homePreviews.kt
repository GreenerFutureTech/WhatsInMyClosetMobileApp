package org.greenthread.whatsinmycloset.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyGridColourBox
import org.greenthread.whatsinmycloset.core.ui.components.listItems.generateRandomItems
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe
import org.greenthread.whatsinmycloset.features.tabs.home.AddItemScreen
import org.greenthread.whatsinmycloset.features.tabs.home.HomeTabScreen

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

    // Create a user profile
    val user = Account("user123", "Test")

    // Add some clothing items to the wardrobe
    val redDress = ClothingItem("item1", "Red Dress", "HomeWardrobe",
        ClothingCategory.TOPS, null, listOf("red", "fancy"))
    val jeans = ClothingItem("item2", "Blue Jeans", "HomeWardrobe",
        ClothingCategory.BOTTOMS, null, listOf("blue", "casual"))

    val wardrobe = Wardrobe("Waterloo Wardrobe", "wardrobe1")
    wardrobe.addItem(redDress)
    wardrobe.addItem(jeans)

    user.addWardrobe(wardrobe)

    // Create an outfit
    // Create an outfit
    val summerLook = Outfit(
        id = "outfit1",
        name = "Summer Look",
        itemIds = listOf(
            ClothingItem(
                id = "1",
                name = "Blue Top",
                itemType = ClothingCategory.TOPS,
                mediaUrl = null,
                tags = listOf("casual", "summer")
            ),
            ClothingItem(
                id = "2",
                name = "Denim Jeans",
                itemType = ClothingCategory.BOTTOMS,
                mediaUrl = null,
                tags = listOf("casual", "summer")
            ),
        )
    )
    user.addOutfit(summerLook)

    val mockNavController = rememberNavController()

    androidx.compose.material.MaterialTheme {
        HomeTabScreen(navController = mockNavController, user, {}, {})
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