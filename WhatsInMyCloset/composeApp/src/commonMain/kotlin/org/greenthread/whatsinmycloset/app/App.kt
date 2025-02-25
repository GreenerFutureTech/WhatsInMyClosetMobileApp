package org.greenthread.whatsinmycloset.app


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.util.Logger
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.generateSampleClothingItems
import org.greenthread.whatsinmycloset.core.viewmodels.ClothingItemViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.MockClothingItemViewModel
import org.greenthread.whatsinmycloset.features.tabs.home.CategoryItemsScreen
import org.greenthread.whatsinmycloset.features.tabs.home.HomeTabScreenRoot
import org.greenthread.whatsinmycloset.features.tabs.home.OutfitScreen
import org.greenthread.whatsinmycloset.features.tabs.profile.ProfileTab
import org.greenthread.whatsinmycloset.features.tabs.social.SocialTab
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.SelectedSwapViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.SwapScreenRoot
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

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
                // NEED TO UPDATE TO KoinViewModel
//                navigation<Routes.LoginGraph>(startDestination = Routes.LoginTab) {
//                    composable<Routes.LoginTab> {
//                        val loginViewModel: LoginViewModel = viewModel()
//                        LoginScreenRoot(viewModel = loginViewModel, navController = navController)
//                    }
//                    composable<Routes.SignUpTab> {
//                        val loginViewModel: LoginViewModel = viewModel()
//                        SignupScreenRoot(viewModel = loginViewModel, navController = navController)
//                    }
//                }
                navigation<Routes.HomeGraph>(startDestination = Routes.HomeTab) {
                    composable<Routes.HomeTab> {
                        HomeTabScreenRoot(
                            navController = navController,
                            onWardrobeDetailsClick =
                            { wardrobeAction ->
                                navController.navigate(Routes.WardrobeItemsScreen(wardrobeAction))
                            },
                            onCreateOutfitClick =
                            {
                                if (navController.currentBackStackEntry != null) {
                                    navController.navigate(Routes.CreateOutfitScreen)
                                }
                            }
                        )
                    }
                    composable<Routes.WardrobeItemsScreen> {
                        Text("Made it to wardrobe items screen")
                        //WardrobeItemsScreen()
                    }
                    // add CreateOutfitScreen Route to separate composable in nav graph
                    composable<Routes.CreateOutfitScreen> {
                        OutfitScreen(
                            navController = navController,
                            onSave = { /* Handle save */ },
                            onAddToCalendar = { date -> /* Handle add to calendar */ },
                            onCreateNew = { /* Handle create new */ },
                            viewModel = MockClothingItemViewModel()
                        )
                    }

                    // Navigation graph setup
                    composable<Routes.CategoryItemScreen>
                    { backStackEntry ->
                        val category = backStackEntry.arguments?.getString("category") ?: ""

                        println("Category: $category")  // Log the category string

                        val categoryEnum = ClothingCategory.fromString(category)
                        val viewModel: ClothingItemViewModel = viewModel()

                        // open screen for category selected.
                        // for example, if user selected "Bottom", open screen to show all Bottoms
                        if (categoryEnum != null) {

                            CategoryItemsScreen(
                                category = categoryEnum.categoryName,
                                onDone = { selectedItems ->
                                    // Update the ViewModel with the selected items
                                    viewModel.addClothingItems(selectedItems)
                                    // Navigate back to the previous screen (OutfitScreen)
                                    navController.popBackStack()
                                },
                                onBack = {
                                    // Handle back navigation (e.g., without saving selected items)
                                    navController.popBackStack()
                                },
                                viewModel = viewModel
                            )
                        } else {
                            // Handle invalid category (e.g., show an error message)
                            Text("Invalid category: $category")
                        }
                    }
                }   // end of Home Graph

                navigation<Routes.ProfileGraph>(startDestination = Routes.ProfileTab) {
                    composable<Routes.ProfileTab> {
                        ProfileTab { }
                    }
                    composable<Routes.ProfileDetailsScreen> {
                        //ProfileDetailsScreen()
                    }
                }
                navigation<Routes.SwapGraph>(startDestination = Routes.SwapTab) {
                    composable<Routes.SwapTab> {
                        val viewModel:  SwapViewModel = koinViewModel()
                        val selectedSwapViewModel =
                            it.sharedKoinViewModel<SelectedSwapViewModel>(navController)

                        LaunchedEffect(true) {
                            selectedSwapViewModel.onSelectSwap(null)
                        }

                        SwapScreenRoot(
                            viewModel = viewModel,
                            onSwapClick = { swap ->
                                selectedSwapViewModel.onSelectSwap(swap.itemId)
                                navController.navigate(
                                    Routes.SwapDetailsScreen(swap.itemId)
                                )
                            }
                        )
                    }
                    composable<Routes.SwapDetailsScreen> {
                        val selectedSwapViewModel =
                            it.sharedKoinViewModel<SelectedSwapViewModel>(navController)
                        val selectedSwap by selectedSwapViewModel.selectedSwap.collectAsStateWithLifecycle()

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Swap Detail Screen for " + "${selectedSwap}" )
                        }
                    }
                }
                navigation<Routes.SocialGraph>(startDestination = Routes.SocialTab) {
                    composable<Routes.SocialTab> {
                        SocialTab { }
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
        val currentDestination =
            navController.currentBackStackEntryAsState().value?.destination?.route
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

@Composable
private inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel (
        viewModelStoreOwner = parentEntry
    )
}