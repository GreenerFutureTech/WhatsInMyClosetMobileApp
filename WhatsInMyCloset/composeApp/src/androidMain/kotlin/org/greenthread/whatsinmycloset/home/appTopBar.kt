package org.greenthread.whatsinmycloset.home

import android.annotation.SuppressLint
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyRowColourBox
import org.greenthread.whatsinmycloset.core.ui.components.listItems.generateRandomItems
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.CategoryItem
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.SeeAllButton
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.WardrobeHeader
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.category_label_accessories
import whatsinmycloset.composeapp.generated.resources.category_label_bottoms
import whatsinmycloset.composeapp.generated.resources.category_label_footwear
import whatsinmycloset.composeapp.generated.resources.category_label_tops
import whatsinmycloset.composeapp.generated.resources.categories_section_title
import whatsinmycloset.composeapp.generated.resources.favourite_section_title
import whatsinmycloset.composeapp.generated.resources.actions_section_title
import whatsinmycloset.composeapp.generated.resources.create_outfit_button
import whatsinmycloset.composeapp.generated.resources.outfit_day_button

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showSystemUi = true, showBackground = true)
fun AppScreenTest() {
    WhatsInMyClosetTheme {
        Scaffold(
            topBar = {
                TopNavigation()
            },
            bottomBar = {
                NavigationBottomBar()
            },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                AddNewItem{}
            }
        ){
            Column (modifier = Modifier
                .padding(vertical = 110.dp)
                .verticalScroll(rememberScrollState())
            ) {
                WardrobeHeader(10)
                HomeSection(title = Res.string.categories_section_title) {
                    CategoriesRow({})
                }
                HomeSection(title = Res.string.favourite_section_title) {
                    FavouriteRow()
                }
                HomeSection(
                    title = null,
                    showSeeAll = false
                ) {
                    BottomButtonsRow(
                        navController = {},
                        launchAddItemScreen = {}
                    )
                }
            }
        }
    }
}
@Composable
fun IconTextButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ElevatedButton(onClick = onClick) {
        Row {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp)) // Adds space between icon and text
            Text(text)
        }
    }
}

@Composable
fun ActionButtonItem(
    text: StringResource,
    icon: ImageVector,
    onClick: () -> Unit
){
    ElevatedButton(onClick = onClick) {
        Row {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp)) // Adds space between icon and text
            Text(stringResource(text))
        }
    }

}

@Composable
private fun FavouriteRow() {
    // TODO Replace to display outfit
    val randomItems = generateRandomItems(6) // Generate 10 random items for the preview
    LazyRowColourBox(items = randomItems)
}

@Composable
fun BottomButtonsRow(
    navController: () -> Unit,
    launchAddItemScreen: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ActionButtonItem(
            onClick = {},
            text = Res.string.create_outfit_button,
            icon = Icons.Rounded.Build,
        )
        ActionButtonItem(
            onClick = {},
            text =  Res.string.outfit_day_button,
            icon = Icons.Rounded.DateRange,
        )


//        CategoryItem(
//            icon = Icons.Rounded.Build,
//            text = "Create Outfit",
//            onClick = {
////                if (navController.currentBackStackEntry != null) {
////                    navController.navigate(Routes.CreateOutfitScreen)
////                }
//            }
//        )
//        CategoryItem(
//            icon = Icons.Rounded.DateRange,
//            text = "Outfit of the Day",
//            onClick = {  }
//        )
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

private data class ImageVectorStringPair(
    val icon: ImageVector,
    val text: StringResource
)

@Composable
fun CategoriesRow(onCategoryClick: (String) -> Unit) {
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
            CategoryItemTest(
                icon = item.icon,
                text = stringResource(item.text),
                onClick = { onCategoryClick(item.text.toString()) },
            )
        }
    }
}

@Composable
fun CategoryItemTest(icon: ImageVector?, text: String?, onClick: (() -> Unit)? = null) {
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
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp)) // Space between icon and text

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
fun AddNewItem(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() }
    ){
        Icon(Icons.Filled.Add, "Add new item")
//        Text(text = "New Item")
    }
}

@Composable
fun NavigationBottomBar() {
    val tabs = listOf(
        Routes.HomeTab to Icons.Rounded.AddCircle,
        Routes.SwapTab to Icons.Rounded.ShoppingCart,
        Routes.SocialTab to Icons.Rounded.Build,
        Routes.ProfileTab to Icons.Rounded.Person
    )

    //val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
    //val selectedIndex = tabs.indexOfFirst { it.first::class.simpleName == currentDestination }
    val selectedIndex = 1

    NavigationBar () {
        tabs.forEachIndexed { index, (route, icon) ->
            val formattedLabel = route::class.simpleName?.removeSuffix("Tab") ?: "Null tab name"

            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = {},
                icon = { Icon(imageVector = icon, contentDescription = null) },
                label = { Text(formattedLabel) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigation() {
    TopAppBar(
        title = {
            Text(
                text = "App",
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        }
    )
}
