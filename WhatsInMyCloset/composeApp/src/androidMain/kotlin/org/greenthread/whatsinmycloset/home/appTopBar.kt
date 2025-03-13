package org.greenthread.whatsinmycloset.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.greenthread.whatsinmycloset.app.AppTopBar
import org.greenthread.whatsinmycloset.app.BottomNavigationBar
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme

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
            }
        ){ }
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
