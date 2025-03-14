package org.greenthread.whatsinmycloset.features.tabs.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyRowColourBox
import org.greenthread.whatsinmycloset.core.ui.components.listItems.generateRandomItems
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.categories_section_title
import whatsinmycloset.composeapp.generated.resources.category_label_accessories
import whatsinmycloset.composeapp.generated.resources.category_label_bottoms
import whatsinmycloset.composeapp.generated.resources.category_label_footwear
import whatsinmycloset.composeapp.generated.resources.category_label_tops
import whatsinmycloset.composeapp.generated.resources.create_outfit_button
import whatsinmycloset.composeapp.generated.resources.favourite_section_title
import whatsinmycloset.composeapp.generated.resources.no_wardrobe_found
import whatsinmycloset.composeapp.generated.resources.outfit_day_button
import whatsinmycloset.composeapp.generated.resources.see_all_button

@Composable
@Preview
fun HomeTabScreenRoot(
    viewModel: HomeTabViewModel,
    navController: NavController,
    onWardrobeDetailsClick: (String) -> Unit = {},
    onCreateOutfitClick: () -> Unit = {},
    onAddItemClick: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }

    // Create a user profile
    val user = User(99999123, "TestName", email = "testmail", firebaseUuid = "", lastLogin = "01-01-2025", name = "testName", registeredAt = "01-01-2025", updatedAt = "01-01-2025")
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

@Composable
fun HomeTabScreen(
    viewModel: HomeTabViewModel?,
    navController: NavController,
    user: User,
    onAddItemClick: () -> Unit,
    onCreateOutfitClick: () -> Unit
){
    viewModel?.testDb()
    val wardrobe = viewModel?.defaultWardrobe


    Column {
        WardrobeHeader(itemCount = wardrobe?.getAllItems()?.count() ?: 0)
        HomeSection(title = Res.string.categories_section_title) {
            CategoriesSection({})
        }
        DropdownMenuLeading(wardrobe?.wardrobeName ?: stringResource(Res.string.no_wardrobe_found))
        HomeSection(title = Res.string.favourite_section_title) {
            FavouriteRow()
        }
        HomeSection(
            title = null,
            showSeeAll = false
        ) {
            BottomButtonsRow(
                navController = navController,
                launchAddItemScreen = onAddItemClick
            )
        }

        //SeeAllButton(onClick = onSeeAllClicked)
        //ActionButtonRow(outfit = outfitOfTheDay)

    }
}

@Composable
fun HomeSection(
    title: StringResource? = null,
    modifier: Modifier = Modifier,
    showSeeAll: Boolean = true,
    content: @Composable () -> Unit
){
    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            title?.let{
                Text(
                    stringResource(title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )
            }
            if(showSeeAll) {
                SeeAllButton{}
            }
        }
        content()
    }
}

@Composable
fun WardrobeHeader(itemCount: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$itemCount items in your Wardrobe",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun FavouriteRow() {
    // TODO Replace to display outfit
    val randomItems = generateRandomItems(6) // Generate random items for the preview
    LazyRowColourBox(items = randomItems)
}

private data class ImageVectorStringPair(
    val icon: ImageVector,
    val text: StringResource
)

@Composable
fun CategoriesSection(onCategoryClick: (String) -> Unit) {
    val itemCategories = listOf(
        Icons.Rounded.Home to Res.string.category_label_tops,
        Icons.Default.Add to Res.string.category_label_bottoms,
        Icons.Default.PlayArrow to Res.string.category_label_accessories,
        Icons.Default.Call to Res.string.category_label_footwear
    ).map { ImageVectorStringPair(it.first, it.second) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(itemCategories) { item ->
            CategoryItem(
                icon = item.icon,
                text = stringResource(item.text),
                onClick = { onCategoryClick(item.text.toString()) },
            )
        }
    }
}

@Composable
fun ActionButtonItem(
    icon: ImageVector,
    text: StringResource,
    onClick: () -> Unit
){
    ElevatedButton(onClick = onClick) {
        Row {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(text))
        }
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
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ActionButtonItem(
            icon = Icons.Rounded.Build,
            text = Res.string.create_outfit_button,
            onClick = {
                if (navController.currentBackStackEntry != null) {
                    navController.navigate(Routes.CreateOutfitScreen)
                }
            }
        )

        ActionButtonItem(
            icon = Icons.Rounded.DateRange,
            text = Res.string.outfit_day_button,
            onClick = {  }
        )

        AddNewItem(
            onClick = { launchAddItemScreen() }
        )
    }
}

// TODO remove
@Composable
fun AddNewItem(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() }
    ){
        Icon(Icons.Filled.Add, "Add new item")
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
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(88.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape)
        ) {
            // Icon
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = text, // Accessibility description
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Label
        text?.let {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .paddingFromBaseline(top = 24.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
fun SeeAllButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.padding(4.dp),
    ) {
        Text(text = stringResource(Res.string.see_all_button))
    }
}

//@Composable
//fun ActionButtonRow(outfit: String) {
//    Column(modifier = Modifier.padding(16.dp)) {
//        Button(
//            onClick = {},
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Text(text = "Outfit of the day")
//        }
//
//    }
//}

//@Composable
//fun FavouriteOutfitsRow() {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//    ) {
//        // Centered Text
//        Text(
//            text = "Favourite Outfits",
//            modifier = Modifier
//                .align(Alignment.Center) // Perfectly center the text in the Box
//        )
//        // Button aligned to the end
//        Button(
//            onClick = { /* Handle button click */ },
//            modifier = Modifier
//                .align(Alignment.CenterEnd) // Align the button to the end (rightmost side)
//                .padding(8.dp)
//        ) {
//            Text(text = "See all")
//        }
//    }
//}

@Composable
fun DropdownMenuLeading(text: String) {
    // State for managing dropdown visibility
    var expanded by remember { mutableStateOf(false) }

    // State for the selected option
    var selectedOption by remember { mutableStateOf(text) }

    // TODO implement logic to retrieve the wardrobes list
    // List of options
    val options = listOf(text)

    // Leading icon for the selected option
    val selectedOptionIcon = Icons.Default.Place

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Dropdown button
        androidx.compose.material3.TextButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = selectedOptionIcon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(selectedOption)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(),
            properties = PopupProperties(focusable = true)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(text = option)
                    },
                    onClick = {
                        selectedOption = option
                        expanded = false
                    }
                )
            }

            HorizontalDivider()

            // "Create New" option
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Create New")
                    }
                },
                onClick = {
                    // TODO Handle "Create New" action
                    expanded = false
                }
            )
        }
    }
}