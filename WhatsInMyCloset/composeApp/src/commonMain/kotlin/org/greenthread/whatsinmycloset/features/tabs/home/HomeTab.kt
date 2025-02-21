package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.foundation.clickable
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
fun HomeTabScreenRoot(onWardrobeDetailsClick: (String) -> Unit,
                      onAddItemClick: () -> Unit) {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
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

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            HomeTabScreen(user, onAddItemClick)
        }
    }
}

@Composable
fun HomeTabScreen(account: Account, onAddItemClick: () -> Unit) {
    val wardrobe = account.getWardrobe("wardrobe1")
    Column {
        WardrobeHeader(itemCount = wardrobe?.getAllItems()?.count() ?: 0)
        CategoriesSection({})

        Text(
            wardrobe?.name ?: "No wardrobe found!",
            modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(25.dp))

        FavouriteOutfitsRow()

        val randomItems = generateRandomItems(account.getAllOutfits().size) // Generate 10 random items for the preview
        LazyGridColourBox(items = randomItems)

        BottomButtonsRow(onAddItemClick)
        //SeeAllButton(onClick = onSeeAllClicked)
        //ActionButtonRow(outfit = outfitOfTheDay)

    }
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
fun CategoriesSection(onCategoryClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Tops Category
        CategoryItem(
            icon = Icons.Default.Home,
            text = "Tops",
            onClick = { onCategoryClick("Tops") }
        )
        // Bottoms Category
        CategoryItem(
            icon = Icons.Default.Add,
            text = "Bottoms",
            onClick = { onCategoryClick("Bottoms") }
        )
        // Accessories Category
        CategoryItem(
            icon = Icons.Default.PlayArrow,
            text = "Accessories",
            onClick = { onCategoryClick("Accessories") }
        )
    }
}

@Composable
fun CategoryItem(icon: ImageVector?, text: String?, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
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
fun BottomButtonsRow(launchAddItemScreen: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Tops Category
        CategoryItem(
            icon = Icons.Default.ShoppingCart, // Icon for Tops
            text = "Create outfit",
            onClick = {  }
        )
        // Bottoms Category
        CategoryItem(
            icon = null, // Icon for Bottoms
            text = "Outfit of the Day",
            onClick = {  }
        )
        // Accessories Category
        CategoryItem(
            icon = Icons.Default.Add, // Icon for Accessories
            text = "Add item to wardrobe",
            onClick = { launchAddItemScreen() }
        )
    }
}

@Composable
fun SeeAllButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
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

