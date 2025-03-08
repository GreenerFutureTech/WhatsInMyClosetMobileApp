package org.greenthread.whatsinmycloset.app

import AllSwapsScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import org.greenthread.whatsinmycloset.CameraManager
import org.greenthread.whatsinmycloset.features.screens.login.presentation.LoginScreenRoot
import org.greenthread.whatsinmycloset.features.screens.login.presentation.LoginViewModel
import org.greenthread.whatsinmycloset.features.screens.signup.SignupScreenRoot
import org.greenthread.whatsinmycloset.features.screens.addItem.presentation.AddItemScreen
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.viewmodels.ClothingItemViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.OutfitViewModel
import org.greenthread.whatsinmycloset.features.screens.addItem.presentation.AddItemScreenViewModel
import org.greenthread.whatsinmycloset.features.tabs.home.CategoryItemDetailScreen
import org.greenthread.whatsinmycloset.features.tabs.home.CategoryItemsScreen
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.HomeTabScreenRoot
import org.greenthread.whatsinmycloset.features.tabs.home.OutfitSaveScreen
import org.greenthread.whatsinmycloset.features.tabs.home.OutfitScreen
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.HomeTabViewModel
import org.greenthread.whatsinmycloset.features.tabs.profile.ProfileTabScreen
import org.greenthread.whatsinmycloset.features.tabs.social.SocialTabScreen
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message.ChatScreen
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message.MessageListScreen
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message.MessageViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.SelectedSwapViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.SwapDetailScreen
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.SwapScreenRoot
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    cameraManager: CameraManager?,
) {
    WhatsInMyClosetTheme {
        val wardrobeManager = koinInject<WardrobeManager>()
        //wardrobeManager.test()

        val navController = rememberNavController()
        // Create shared ViewModels for the outfit screens
        val sharedClothingItemViewModel: ClothingItemViewModel = koinViewModel()
        val sharedOutfitViewModel: OutfitViewModel = viewModel()

        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.LoginGraph,
                modifier = Modifier.padding(innerPadding)
            ) {
                navigation<Routes.LoginGraph>(startDestination = Routes.LoginTab) {
                    composable<Routes.LoginTab> {
                        val loginViewModel : LoginViewModel = koinViewModel()
                        LoginScreenRoot(loginViewModel, navController)
                    }
                    composable<Routes.SignUpTab> {
                        val viewModel: LoginViewModel = koinViewModel()
                        SignupScreenRoot(viewModel, navController)
                    }
                }
                navigation<Routes.HomeGraph>(startDestination = Routes.HomeTab) {
                    composable<Routes.HomeTab> {
                        val viewModel = koinViewModel<HomeTabViewModel>()
                        HomeTabScreenRoot(
                            viewModel = viewModel,
                            navController = navController,
                            onWardrobeDetailsClick = { homeTabAction ->
                                navController.navigate(Routes.WardrobeItemsScreen(homeTabAction))
                            },
                            onAddItemClick = {
                                navController.navigate(Routes.AddItemScreen)
                            },
                            onCreateOutfitClick =
                            {
                                if (navController.currentBackStackEntry != null) {
                                    navController.navigate(Routes.CreateOutfitScreen)
                                }
                            }
                        )
                    }
                    composable<Routes.AddItemScreen> {
                        if (cameraManager != null) {
                            val viewmodel = koinViewModel<AddItemScreenViewModel>()
                            AddItemScreen(viewModel = viewmodel, cameraManager = cameraManager, onBack = {navController.navigate(Routes.HomeTab)})
                        }
                    }
                    composable<Routes.WardrobeItemsScreen> {
                        Text("Made it to wardrobe items screen")
                        //WardrobeItemsScreen()
                    }

                    // -- Create Outfit Screens Routes below -- //

                    // add CreateOutfitScreen Route to separate composable in nav graph
                    composable<Routes.CreateOutfitScreen> {
                        // to test the Save Outfit, Add to Calendar and Create New Outfit buttons
                        /*clothingItemViewModel.initializeClothingItems(
                            listOf(
                            ClothingItem(
                                id = "1",
                                name = "Blue Top",
                                category = ClothingCategory.TOPS,
                                clothingImage = null,
                                tags = setOf("casual", "summer")
                            ),
                            ClothingItem(
                                id = "2",
                                name = "Denim Jeans",
                                category = ClothingCategory.BOTTOMS,
                                clothingImage = null,
                                tags = setOf("casual", "summer")
                            )))*/

                        OutfitScreen(
                            navController = navController,
                            clothingItemViewModel = sharedClothingItemViewModel,
                            outfitViewModel = sharedOutfitViewModel,
                        )
                    }

                    // open screen for category selected.
                    // for example, if user selected "Bottom", open screen to show all Bottoms
                    composable<Routes.CategoryItemScreen>
                    { backStackEntry ->
                        val category = backStackEntry.arguments?.getString("category") ?: ""

                        println("Category: $category")  // Log the category string

                        val categoryEnum = ClothingCategory.fromString(category)

                        if (categoryEnum != null) {

                            CategoryItemsScreen(
                                navController = navController,
                                category = categoryEnum.categoryName,
                                onBack = { navController.popBackStack() },
                                onDone = {Routes.CreateOutfitScreen },
                                viewModel = sharedClothingItemViewModel
                            )
                        } else {
                            // Handle invalid category (e.g., show an error message)
                            Text("Invalid category: $category")
                        }
                    }

                    composable<Routes.CategoryItemDetailScreen>
                    {
                        backStackEntry ->
                        val wardrobeId =
                            backStackEntry.arguments?.getString("clickedItemWardrobeID") ?: ""

                        val itemId =
                            backStackEntry.arguments?.getString("clickedItemID") ?: ""

                        val categoryStr =
                            backStackEntry.arguments?.getString("clickedItemCategory") ?: ""

                        val category = ClothingCategory.fromString(categoryStr)

                        if (category != null) {
                            CategoryItemDetailScreen(
                                navController = navController,
                                wardrobeId = wardrobeId,
                                itemId = itemId,
                                category = category,
                                onBack = { navController.popBackStack() },
                                viewModel = sharedClothingItemViewModel
                            )
                        }
                    }

                    // Save Outfit Screen
                    // -- opens repository screen
                    // -- where user can see the outfit folders to save outfit in
                    // -- or create a new folder
                    composable<Routes.OutfitSaveScreen>
                    {
                          OutfitSaveScreen(
                            navController = navController,
                            onExit = { },
                            onDone = { },
                            viewModel = sharedOutfitViewModel
                        )
                    }

                    // end of outfit screens

                }   // end of Home Graph

                navigation<Routes.ProfileGraph>(startDestination = Routes.ProfileTab) {
                    composable<Routes.ProfileTab> {
                        ProfileTabScreen { }
                    }
                    composable<Routes.ProfileDetailsScreen> {
                        //ProfileDetailsScreen()
                    }
                }
                navigation<Routes.SwapGraph>(startDestination = Routes.SwapTab) {
                    composable<Routes.SwapTab> {
                        val viewModel: SwapViewModel = koinViewModel()
                        val selectedSwapViewModel = it.sharedKoinViewModel<SelectedSwapViewModel>(navController)

                        LaunchedEffect(true) {
                            selectedSwapViewModel.onSelectSwap(null)
                        }

                        SwapScreenRoot(
                            viewModel = viewModel,
                            onSwapClick = { swap ->
                                selectedSwapViewModel.onSelectSwap(swap)
                                navController.navigate(Routes.SwapDetailsScreen(swap.itemId.id))
                            },
                            onAllSwapClick = { navController.navigate(Routes.AllSwapScreen) },
                            onMessageClick = { navController.navigate(Routes.MessageListScreen)}
                        )
                    }

                    composable<Routes.AllSwapScreen> {
                        val viewModel: SwapViewModel = koinViewModel()
                        val selectedSwapViewModel = it.sharedKoinViewModel<SelectedSwapViewModel>(navController)

                        AllSwapsScreen(
                            viewModel = viewModel,
                            navController = navController,
                            onSwapClick = { swap ->
                                selectedSwapViewModel.onSelectSwap(swap)
                                navController.navigate(Routes.SwapDetailsScreen(swap.itemId.id))
                            }
                        )
                    }

                    composable<Routes.SwapDetailsScreen> {
                        val selectedSwapViewModel = it.sharedKoinViewModel<SelectedSwapViewModel>(navController)
                        val selectedSwap by selectedSwapViewModel.selectedSwap.collectAsStateWithLifecycle()

                        SwapDetailScreen(swap = selectedSwap, onBackClick = { navController.popBackStack() } )
                    }

                }
                navigation<Routes.MessageGraph>(startDestination = Routes.MessageListScreen) {

                    composable<Routes.MessageListScreen>{
                        val viewModel: MessageViewModel = koinViewModel()
                        MessageListScreen(
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                    composable<Routes.ChatScreen>{
                        backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")
                        val viewModel: MessageViewModel = koinViewModel()
                        if(userId != null) {
                            ChatScreen(
                                viewModel = viewModel,
                                otherUserId = userId,
                                navController = navController
                            )
                        }

                    }
                }
                navigation<Routes.SocialGraph>(startDestination = Routes.SocialTab) {
                    composable<Routes.SocialTab> {
                        SocialTabScreen { }
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