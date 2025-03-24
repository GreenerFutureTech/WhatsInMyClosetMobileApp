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

    // Create a user profile
    val user = User(99999123, "TestName", email = "testmail", firebaseUuid = "", lastLogin = "01-01-2025", name = "testName", registeredAt = "01-01-2025", updatedAt = "01-01-2025")

    // Add some clothing items to the wardrobe
    val redDress = ClothingItem("item1", "Red Dress", "HomeWardrobe",
        ClothingCategory.TOPS, null, listOf("red", "fancy"))
    val jeans = ClothingItem("item2", "Blue Jeans", "HomeWardrobe",
        ClothingCategory.BOTTOMS, null, listOf("blue", "casual"))

    val wardrobe = Wardrobe("Waterloo Wardrobe", "12345", "01-01-2025", "01-01-2025", "2")
    wardrobe.addItem(redDress)
    wardrobe.addItem(jeans)

    user.addWardrobe(wardrobe)

    // Create an outfit
    val summerLook = Outfit(
        id = "outfit1",
        userId = 1,
        public = true,
        favorite = true,
        mediaURL = "",
        name = "Summer Look",
        itemIds = listOf("1", "10", "9"),
        createdAt = "08/03/2025"
    )
    user.addOutfit(summerLook, listOf("Busines Casuals", "Casuals"))

    val mockNavController = rememberNavController()

    androidx.compose.material.MaterialTheme {
        HomeTabScreen(viewModel = null, navController = mockNavController, user)
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