package org.greenthread.whatsinmycloset.app

import AllSwapsScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import org.greenthread.whatsinmycloset.CameraManager
import org.greenthread.whatsinmycloset.NotificationManager
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.viewmodels.ClothingItemViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.OutfitViewModel
import org.greenthread.whatsinmycloset.features.screens.addItem.presentation.AddItemScreen
import org.greenthread.whatsinmycloset.features.screens.addItem.presentation.AddItemScreenViewModel
import org.greenthread.whatsinmycloset.features.screens.login.presentation.LoginScreenRoot
import org.greenthread.whatsinmycloset.features.screens.login.presentation.LoginViewModel
import org.greenthread.whatsinmycloset.features.screens.notifications.presentation.NotificationsScreen
import org.greenthread.whatsinmycloset.features.screens.notifications.presentation.NotificationsViewModel
import org.greenthread.whatsinmycloset.features.screens.settings.SettingsScreen
import org.greenthread.whatsinmycloset.features.screens.signup.SignupScreenRoot
import org.greenthread.whatsinmycloset.features.tabs.home.CategoryItemDetailScreen
import org.greenthread.whatsinmycloset.features.tabs.home.CategoryItemsScreen
import org.greenthread.whatsinmycloset.features.tabs.home.OutfitSaveScreen
import org.greenthread.whatsinmycloset.features.tabs.home.OutfitScreen
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.HomeTabScreenRoot
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.HomeTabViewModel
import org.greenthread.whatsinmycloset.features.tabs.profile.ProfileTabScreen
import org.greenthread.whatsinmycloset.features.tabs.profile.ProfileTabViewModel
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
    notificationManager: NotificationManager?
) {
    val wardrobeManager = koinInject<WardrobeManager>()
    val userManager = koinInject<UserManager>()
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = koinViewModel()

    // Get the current user from the manager
    val currentUser by userManager.currentUser.collectAsState()

    // Get the current load state (Default to true)
    val isLoading = loginViewModel.state.isLoading

    // When the app launches, check the current user
    // if not null, log them in!
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate(Routes.LoginTab) {
                popUpTo(Routes.HomeTab) { inclusive = true }
            }
        }
    }

    // Create shared ViewModels for the outfit screens
    val user: User = koinInject() // Retrieve the logged-in user's account
    val sharedClothingItemViewModel: ClothingItemViewModel = koinViewModel()
    val sharedOutfitViewModel: OutfitViewModel = koinViewModel()

    WhatsInMyClosetTheme {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "WIMC",
                    navController = navController,
                    showBackButton = true
                )
            },
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (currentUser == null && !isLoading) Routes.LoginGraph else Routes.HomeGraph,
                modifier = Modifier.padding(innerPadding)
            ) {
                navigation<Routes.LoginGraph>(startDestination = Routes.LoginTab) {
                    composable<Routes.LoginTab> {
                        LoginScreenRoot(loginViewModel, navController)
                    }
                    composable<Routes.SignUpTab> {
                        SignupScreenRoot(loginViewModel, navController)
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

                        OutfitScreen(
                            navController = navController,
                            clothingItemViewModel = sharedClothingItemViewModel,
                            outfitViewModel = sharedOutfitViewModel
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
                            outfitViewModel = sharedOutfitViewModel,
                            clothingItemViewModel = sharedClothingItemViewModel
                        )
                    }

                    // end of outfit screens

                }   // end of Home Graph

                navigation<Routes.ProfileGraph>(startDestination = Routes.ProfileTab) {
                    composable<Routes.ProfileTab> {
                        val viewModel: ProfileTabViewModel = koinViewModel()
                        ProfileTabScreen(userState = viewModel.userState, onNavigate = {})
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
                        val userAccount by userManager.currentUser.collectAsState() // Collect StateFlow as a normal value

                        SwapDetailScreen(swap = selectedSwap, onBackClick = { navController.navigate(Routes.SwapTab)}, userUser = userAccount )
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
                        val viewModel: MessageViewModel = koinViewModel()

                        ChatScreen(
                            viewModel = viewModel
                        )


                    }
                }
                navigation<Routes.SocialGraph>(startDestination = Routes.SocialTab) {
                    composable<Routes.SocialTab> {
                        val userAccount by userManager.currentUser.collectAsState()
                        SocialTabScreen(user = userAccount, onNavigate = {})
                    }
                    composable<Routes.SocialDetailsScreen> {
                        //SocialDetailsScreen()
                    }
                }

                composable<Routes.SettingsScreen> {
                    val viewModel: LoginViewModel = koinViewModel()

                    SettingsScreen(
                        navController = navController,
                        viewModel
                    )
                }

                composable<Routes.NotificationsScreen> {
                    val viewModel = koinViewModel<NotificationsViewModel>()
                    NotificationsScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val tabs = listOf(
        Routes.HomeTab to Icons.Rounded.Home,
        Routes.SwapTab to Icons.Rounded.ShoppingCart,
        Routes.SocialTab to Icons.Rounded.Person,
        Routes.ProfileTab to Icons.Rounded.Person
        )

    // Current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    // Extract the last part of the route (e.g., "HomeTab")
    val currentTab = currentDestination?.substringAfterLast(".")

    val selectedIndex = tabs.indexOfFirst { it.first::class.simpleName == currentTab }

    NavigationBar () {
        tabs.take(2).forEachIndexed { index, (route, icon) ->
            val isSelected = currentTab == route::class.simpleName
            val formattedLabel = route::class.simpleName?.removeSuffix("Tab") ?: "Null tab name"

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(imageVector = icon, contentDescription = null) },
                label = { Text(formattedLabel) }
            )
        }

        // Display action bar only on the home tab
        val homeTabIndex = 0
        val showFab = homeTabIndex == selectedIndex
        if (showFab) {
            AddNewItem {  }
        }

        tabs.takeLast(2).forEachIndexed { index, (route, icon) ->
            val isSelected = currentTab == route::class.simpleName
            val formattedLabel = route::class.simpleName?.removeSuffix("Tab") ?: "Null tab name"

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(imageVector = icon, contentDescription = null) },
                label = { Text(formattedLabel) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navController: NavController,
    showBackButton: Boolean = false
) {
    val notificationsViewModel: NotificationsViewModel = koinViewModel()
    val hasNewNotifications by notificationsViewModel.hasNewNotifications.collectAsState()

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            // Notification Icon with Indicator
            Box {
                IconButton(onClick = { navController.navigate(Routes.NotificationsScreen) }) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }
                if (hasNewNotifications) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(12.dp)
                            .background(MaterialTheme.colorScheme.error, CircleShape)
                    )
                }
            }

            IconButton(onClick = { navController.navigate(Routes.SettingsScreen) }) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )
}

// FAB to add new item in the wardrobe
// It redirects the user to the screen AddItemScreen
@Composable
fun AddNewItem(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary
    ){
        Icon(Icons.Filled.Add, "Add new item")
    }
}