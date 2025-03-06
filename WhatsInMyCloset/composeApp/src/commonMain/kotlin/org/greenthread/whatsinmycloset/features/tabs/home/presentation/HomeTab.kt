package org.greenthread.whatsinmycloset.features.tabs.home.presentation

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
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyGridColourBox
import org.greenthread.whatsinmycloset.core.ui.components.listItems.generateRandomItems
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun HomeTabScreenRoot(
    viewModel: HomeTabViewModel,
    navController: NavController,
    onWardrobeDetailsClick: (String) -> Unit = {},
    onCreateOutfitClick: () -> Unit = {},
    onAddItemClick: () -> Unit
) {
    WhatsInMyClosetTheme {
        var showContent by remember { mutableStateOf(false) }

        // Create a user profile
        val user = Account("user123", "Test")
        //Relevant info is injected via HomeTabViewModel and managers
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            HomeTabScreen(
                viewModel = viewModel,
                navController = navController,
                user,
                onAddItemClick,
                onCreateOutfitClick = onCreateOutfitClick
            )
        }
/*        // Add some clothing items to the wardrobe
        val redDress = ClothingItem("item1", "Red Dress", "HomeWardrobe",
            ClothingCategory.TOPS, null, listOf("red", "fancy"))
        val jeans = ClothingItem("item2", "Blue Jeans", "HomeWardrobe",
            ClothingCategory.BOTTOMS, null, listOf("blue", "casual"))

        val wardrobe = Wardrobe("Waterloo Wardrobe", "1234", "01-01-2025", "01-01-2025","wardrobe1")
        user.addWardrobe(wardrobe)*/

/*        // Create an outfit
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
        )*/

        //user.addOutfit(summerLook)

    }
}

@Composable
fun HomeTabScreen(
    viewModel: HomeTabViewModel?,
    navController: NavController,
    account: Account,
    onAddItemClick: () -> Unit,
    onCreateOutfitClick: () -> Unit
){
    val wardrobe = viewModel?.defaultWardrobe
    viewModel?.testDb()
    Column {
        WardrobeHeader(itemCount = wardrobe?.getAllItems()?.count() ?: 0)
        CategoriesSection({})

        Text(
            wardrobe?.wardrobeName ?: "No wardrobe found!",
            modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(25.dp))

        FavouriteOutfitsRow()

        val randomItems = generateRandomItems(2) // Generate 10 random items for the preview
        LazyGridColourBox(items = randomItems)

        BottomButtonsRow(navController, onAddItemClick)
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
fun BottomButtonsRow(
    navController: NavController,
    launchAddItemScreen: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CategoryItem(
            icon = Icons.Default.ShoppingCart,
            text = "Create outfit",
            onClick = {
                if (navController.currentBackStackEntry != null) {
                    navController.navigate(Routes.CreateOutfitScreen)
                }
            }
        )
        CategoryItem(
            icon = null,
            text = "Outfit of the Day",
            onClick = {  }
        )
        CategoryItem(
            icon = Icons.Default.Add,
            text = "Add item to wardrobe",
            onClick = { launchAddItemScreen() }
        )
    }
}

@Composable
fun CategoryItem(icon: ImageVector?, text: String?, onClick: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
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