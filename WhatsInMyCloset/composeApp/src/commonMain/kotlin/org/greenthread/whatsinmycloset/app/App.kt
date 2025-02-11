package org.greenthread.whatsinmycloset.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.greenthread.whatsinmycloset.features.tabs.home.HomeTabScreenRoot
import org.greenthread.whatsinmycloset.features.tabs.profile.ProfileTab
import org.greenthread.whatsinmycloset.features.tabs.social.SocialTab
import org.greenthread.whatsinmycloset.features.tabs.swap.SwapTab
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.HomeGraph,
                modifier = Modifier.padding(innerPadding)
            ) {
                navigation<Routes.HomeGraph>(startDestination = Routes.HomeTab) {
                    composable<Routes.HomeTab> {
                        HomeTabScreenRoot(
                            onWardrobeDetailsClick = { wardrobeAction ->
                                navController.navigate(Routes.WardrobeItemsScreen(wardrobeAction))
                            }
                        )
                    }
                    composable<Routes.WardrobeItemsScreen> {
                        Text("Made it to wardrobe items screen")
                        //WardrobeItemsScreen()
                    }
                }
                navigation<Routes.ProfileGraph>(startDestination = Routes.ProfileTab) {
                    composable<Routes.ProfileTab> {
                        ProfileTab {  }
                    }
                    composable<Routes.ProfileDetailsScreen> {
                        //ProfileDetailsScreen()
                    }
                }
                navigation<Routes.SwapGraph>(startDestination = Routes.SwapTab) {
                    composable<Routes.SwapTab> {
                        SwapTab {  }
                    }
                    composable<Routes.SwapDetailsScreen> {
                        //SwapDetailsScreen()
                    }
                }
                navigation<Routes.SocialGraph>(startDestination = Routes.SocialTab) {
                    composable<Routes.SocialTab> {
                        SocialTab {  }
                    }
                    composable<Routes.SocialDetailsScreen> {
                        //SocialDetailsScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation {
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
        listOf(
            Routes.HomeTab to Icons.Default.Home,
            Routes.ProfileTab to Icons.Default.Person,
            Routes.SwapTab to Icons.Default.ShoppingCart,
            Routes.SocialTab to Icons.Default.Person
        ).forEach { (route, icon) ->
            BottomNavigationItem(
                selected = currentDestination == route::class.simpleName,
                onClick = { navController.navigate(route) },
                icon = {
                    Icon(imageVector = icon, contentDescription = null)
                },
                label = { Text(route::class.simpleName ?: "Null tab name") }
            )
        }
    }
}