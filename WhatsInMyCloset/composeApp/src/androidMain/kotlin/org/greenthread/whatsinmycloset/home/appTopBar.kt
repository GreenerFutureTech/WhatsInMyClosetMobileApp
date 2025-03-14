package org.greenthread.whatsinmycloset.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.category_label_accessories
import whatsinmycloset.composeapp.generated.resources.category_label_bottoms
import whatsinmycloset.composeapp.generated.resources.category_label_footwear
import whatsinmycloset.composeapp.generated.resources.category_label_tops
import whatsinmycloset.composeapp.generated.resources.categories_section_title

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
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                AddNewItem {  }
            }
        ){
            Column (modifier = Modifier.padding(vertical = 120.dp)) {
//                Spacer(modifier = Modifier.height(150.dp))
                HomeSection(title = Res.string.categories_section_title) {
                    CategoriesRow({})
                }


            }
        }
    }
}

@Composable
fun HomeSection(
    title: StringResource,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
){
    Column(modifier) {
        Text(
            stringResource(title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .paddingFromBaseline(top = 40.dp, bottom = 8.dp)
                .padding(horizontal = 16.dp)
        )
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
                .background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
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
    ExtendedFloatingActionButton(
        onClick = { onClick() }
    ){
        Icon(Icons.Filled.Add, "Add new item")
        Text(text = "New Item")
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
                Text(
                    text = "Set",
                    style = MaterialTheme.typography.bodySmall
                )
            }

        }
    )
}
