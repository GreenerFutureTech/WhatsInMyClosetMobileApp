package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyGridColourBox
import org.greenthread.whatsinmycloset.core.ui.components.listItems.generateRandomItems
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun HomeTabScreenRoot(onWardrobeDetailsClick: (String) -> Unit) {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            HomeTabScreen(null) {}
        }
    }
}

@Composable
fun HomeTabScreen(
    viewModel: Any?,
    onWardrobeDetailsClick: (String) -> Unit
){
    // Create a user profile
    val user = Account("user123", "Test")

    // Add some clothing items to the wardrobe
    val redDress = ClothingItem("item1", "Red Dress", "1", "url_to_red_dress.jpg","mediaurl", listOf("red", "fancy"),"20202020")
    val jeans = ClothingItem("item2", "Blue Jeans", "1", "url_to_jeans.jpg", "mediaUrl", listOf("blue", "casual"), "20202020")

    val wardrobe = Wardrobe("Waterloo Wardrobe", "wardrobe1")
    wardrobe.addItem(redDress)
    wardrobe.addItem(jeans)

    user.addWardrobe(wardrobe)


    // Create an outfit
    val summerLook = Outfit("outfit1", "Summer Look", setOf("item1", "item2"))
    user.addOutfit(summerLook)


    var wardrobeRepository = null
    var favouriteOutfits = null
    var outfitCalendarContents = null
    WardrobeScreen(user)
}

@Composable
fun WardrobeHeader(itemCount: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$itemCount items in your Wardrobe",
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun CategoriesSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Tops Category
        CategoryItem(
            icon = Icons.Default.Home, // Icon for Tops
            text = "Tops"
        )
        // Bottoms Category
        CategoryItem(
            icon = Icons.Default.Add, // Icon for Bottoms
            text = "Bottoms"
        )
        // Accessories Category
        CategoryItem(
            icon = Icons.Default.PlayArrow, // Icon for Accessories
            text = "Accessories"
        )
    }
}

@Composable
fun BottomButtonsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Tops Category
        CategoryItem(
            icon = Icons.Default.ShoppingCart, // Icon for Tops
            text = "Create outfit"
        )
        // Bottoms Category
        CategoryItem(
            icon = null, // Icon for Bottoms
            text = "Outfit of the Day"
        )
        // Accessories Category
        CategoryItem(
            icon = Icons.Default.Add, // Icon for Accessories
            text = "Add item to wardrobe"
        )
    }
}

@Composable
fun CategoryItem(icon: ImageVector?, text: String?) {
    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Center the icon and text
    ) {
        // Icon
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text, // Accessibility description
                modifier = Modifier.size(48.dp), // Set icon size
                tint = MaterialTheme.colors.primary // Use theme color for the icon
            )
            Spacer(modifier = Modifier.height(8.dp)) // Space between icon and text
            // Text
        }
        text?.let {
            Text(
                text = text,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
fun SeeAllButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
//            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(text = "See All")
    }
}

@Composable
fun ActionButtonRow(outfit: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Outfit of the day")
        }

    }
}

@Composable
fun FavouriteOutfitsRow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Centered Text
        Text(
            text = "Favourite Outfits",
            modifier = Modifier
                .align(Alignment.Center) // Perfectly center the text in the Box
        )
        // Button aligned to the end
        Button(
            onClick = { /* Handle button click */ },
            modifier = Modifier
                .align(Alignment.CenterEnd) // Align the button to the end (rightmost side)
                .padding(8.dp)
        ) {
            Text(text = "See all")
        }
    }
}

@Composable
fun WardrobeScreen(account: Account) {
    val wardrobe = account.getWardrobe("wardrobe1")
    Column {
        WardrobeHeader(itemCount = wardrobe?.getAllItems()?.count() ?: 0)
        CategoriesSection()

        Text(
            wardrobe?.name ?: "No wardrobe found!",
            modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(25.dp))

        FavouriteOutfitsRow()

        val randomItems = generateRandomItems(account.getAllOutfits().size) // Generate 10 random items for the preview
        LazyGridColourBox(items = randomItems)

        BottomButtonsRow()
        //SeeAllButton(onClick = onSeeAllClicked)
        //ActionButtonRow(outfit = outfitOfTheDay)

    }
}