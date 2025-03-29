package org.greenthread.whatsinmycloset.app

import AddSwapItemRoot
import AllSwapsScreen
import CategoryItemScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MailOutline
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.datetime.LocalDate
import org.greenthread.whatsinmycloset.CameraManager
import org.greenthread.whatsinmycloset.PhotoManager
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.MessageManager
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.dto.MessageUserDto
import org.greenthread.whatsinmycloset.core.dto.toOtherSwapDto
import org.greenthread.whatsinmycloset.core.managers.CalendarManager
import org.greenthread.whatsinmycloset.core.managers.WardrobeManager
import org.greenthread.whatsinmycloset.core.viewmodels.ClothingItemViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.OutfitViewModel
import org.greenthread.whatsinmycloset.features.screens.addItem.presentation.AddItemScreen
import org.greenthread.whatsinmycloset.features.screens.addItem.presentation.AddItemScreenViewModel
import org.greenthread.whatsinmycloset.features.screens.login.presentation.LoginScreenRoot
import org.greenthread.whatsinmycloset.features.screens.login.presentation.LoginViewModel
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.NotificationEventBus
import org.greenthread.whatsinmycloset.features.screens.notifications.presentation.NotificationsScreen
import org.greenthread.whatsinmycloset.features.screens.notifications.presentation.NotificationsViewModel
import org.greenthread.whatsinmycloset.features.screens.settings.EditProfileScreen
import org.greenthread.whatsinmycloset.features.screens.settings.EditProfileViewModel
import org.greenthread.whatsinmycloset.features.screens.settings.SettingsScreen
import org.greenthread.whatsinmycloset.features.screens.signup.SignupScreenRoot
import org.greenthread.whatsinmycloset.features.tabs.home.CategoryItemDetailScreen
import org.greenthread.whatsinmycloset.features.tabs.home.CategoryItemsScreen
import org.greenthread.whatsinmycloset.features.tabs.home.OutfitDetailScreen
import org.greenthread.whatsinmycloset.features.tabs.home.OutfitOfTheDayCalendar
import org.greenthread.whatsinmycloset.features.tabs.home.OutfitSaveScreen
import org.greenthread.whatsinmycloset.features.tabs.home.OutfitScreen
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.CategoryItemsViewModel
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.HomeTabScreenRoot
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.HomeTabViewModel
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.ItemDetailScreen
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.SelectedItemViewModel
import org.greenthread.whatsinmycloset.features.tabs.profile.presentation.ProfileScreen
import org.greenthread.whatsinmycloset.features.tabs.profile.presentation.ProfileScreenRoot
import org.greenthread.whatsinmycloset.features.tabs.profile.ProfileTabViewModel
import org.greenthread.whatsinmycloset.features.tabs.profile.presentation.UserFriendsScreen
import org.greenthread.whatsinmycloset.features.tabs.profile.presentation.UserSearchScreen
import org.greenthread.whatsinmycloset.features.tabs.swap.domain.SwapEventBus
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.AddSwap.AddSwapRoot
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.AddSwap.AddSwapViewModel
import org.greenthread.whatsinmycloset.features.tabs.social.presentation.PostDetailScreen
import org.greenthread.whatsinmycloset.features.tabs.social.presentation.PostViewModel
import org.greenthread.whatsinmycloset.features.tabs.social.presentation.SocialTabScreen
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message.ChatScreen
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message.MessageListScreen
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message.MessageViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.SelectedSwapViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.SwapDetailScreen
import org.greenthread.whatsinmycloset.features.tabs.swap.presentation.SwapScreenRoot
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.add_new_item_button

sealed class BarVisibility {
    data object Visible : BarVisibility()
    data object Hidden : BarVisibility()
    data class Custom(
        val topBar: Boolean = true,
        val bottomBar: Boolean = true,
        val onlyBack: Boolean = false,
        val disableBack: Boolean = false,
        val title: String = ""
    ) : BarVisibility()
}

@Composable
fun NavController.getBarVisibility(): BarVisibility {
    val navBackStackEntry by currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringAfterLast(".")?.substringBefore("/")?.substringBefore("?")

    return when (currentRoute) {
        // Login
        Routes.LoginTab::class.simpleName -> BarVisibility.Hidden
        Routes.SignUpTab::class.simpleName -> BarVisibility.Hidden

        // Notification Screen
        Routes.NotificationsScreen::class.simpleName -> BarVisibility.Custom(bottomBar = false, onlyBack = true, title = "Notifications")

        // Swaps
        Routes.AddSwapScreen::class.simpleName -> BarVisibility.Custom(onlyBack = true, title = "Wardrobes")
        Routes.AddSwapItemScreen::class.simpleName -> BarVisibility.Custom(onlyBack = true, title = "Add To Swap")
        Routes.ChatScreen::class.simpleName -> BarVisibility.Custom(bottomBar = false, onlyBack = true, title = "Chat")
        Routes.MessageListScreen::class.simpleName -> BarVisibility.Custom(title = "Messages", onlyBack = true)
        Routes.AllSwapScreen::class.simpleName -> BarVisibility.Custom(title = "All Swaps", onlyBack = true)

        // Main Tabs
        Routes.HomeTab::class.simpleName -> BarVisibility.Custom(disableBack = true, title = "Home")
        Routes.SwapTab::class.simpleName -> BarVisibility.Custom(disableBack = true, title = "Swaps")
        Routes.SocialTab::class.simpleName -> BarVisibility.Custom(disableBack = true, title = "Social")
        Routes.ProfileTab::class.simpleName -> BarVisibility.Custom(disableBack = true, title = "Profile")
        Routes.AddItemScreen::class.simpleName -> BarVisibility.Custom(onlyBack = true, bottomBar = false, title = "Add Item")

        // Outfit
        //Routes.CreateOutfitScreen::class.simpleName -> BarVisibility.Hidden
        //Routes.CategoryItemScreen::class.simpleName -> BarVisibility.Hidden
        //Routes.OutfitSaveScreen::class.simpleName -> BarVisibility.Hidden

        // Misc
        Routes.SettingsScreen::class.simpleName -> BarVisibility.Custom(onlyBack = true, title = "Settings")
        Routes.EditProfileScreen::class.simpleName -> BarVisibility.Custom(onlyBack = true, title = "Edit Profile")

        // Profile
        Routes.UserSearchScreen::class.simpleName -> BarVisibility.Custom(onlyBack = true, title = "Search Users")
        Routes.UserFriendsScreen::class.simpleName -> BarVisibility.Custom(onlyBack = true, title = "Friends")
        Routes.ProfileDetailsScreen::class.simpleName -> BarVisibility.Custom(disableBack = true, title = "Profile")

        // Add more specific route configurations as needed
        else -> BarVisibility.Visible
    }
}

@Composable
@Preview
fun App(
    cameraManager: CameraManager?,
    photoManager: PhotoManager?
) {
    val wardrobeManager = koinInject<WardrobeManager>()
    val userManager = koinInject<UserManager>()
    val calendarManager = koinInject<CalendarManager>()
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = koinViewModel()

    // Create shared ViewModels for the outfit screens
    val user: User = koinInject()
    val sharedClothingItemViewModel: ClothingItemViewModel = koinViewModel()
    val sharedOutfitViewModel: OutfitViewModel = koinViewModel()

    val barVisibility = navController.getBarVisibility()

    WhatsInMyClosetTheme {
        Scaffold(
            topBar = {
                if (barVisibility is BarVisibility.Visible ||
                    (barVisibility is BarVisibility.Custom && barVisibility.topBar)) {
                    AppTopBar(
                        title =  if (barVisibility is BarVisibility.Custom) {
                            barVisibility.title
                        }
                        else "",
                        navController = navController,
                        onlyBackButton = barVisibility is BarVisibility.Custom &&
                                barVisibility.onlyBack,
                        disableBack = barVisibility is BarVisibility.Custom &&
                                barVisibility.disableBack
                    )
                }
            },
            bottomBar = {
                if (barVisibility is BarVisibility.Visible ||
                    (barVisibility is BarVisibility.Custom && barVisibility.bottomBar)) {
                    BottomNavigationBar(navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.LoginGraph ,
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

                    composable<Routes.HomeCategoryItemScreen> { backStackEntry ->
                        val category = backStackEntry.arguments?.getString("category") ?: ""
                        val viewModel = koinViewModel<CategoryItemsViewModel>()
                        val selectedItemViewModel = backStackEntry.sharedKoinViewModel<SelectedItemViewModel>(navController)

                        CategoryItemScreen(
                            categoryName = category,
                            viewModel = viewModel,
                            onItemClick = { item ->
                                selectedItemViewModel.onSelectItem(item)
                                navController.navigate(Routes.ItemDetailScreen(item.id))
                            }
                        )
                    }

                    composable<Routes.ItemDetailScreen> {
                        val selectedItemViewModel = it.sharedKoinViewModel<SelectedItemViewModel>(navController)
                        val selectedItem by selectedItemViewModel.selectedItem.collectAsStateWithLifecycle()

                        ItemDetailScreen(
                            item = selectedItem
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

                    composable<Routes.OutfitOfTheDay> {

                        OutfitOfTheDayCalendar(
                            navController = navController,
                            outfitViewModel = sharedOutfitViewModel
                        )
                    }

                    composable<Routes.OutfitDetailScreen> { backStackEntry ->
                        val outfitId = backStackEntry.arguments?.getString("outfitId")
                        val postViewModel: PostViewModel = koinViewModel()

                        if (outfitId != null) {
                            OutfitDetailScreen(outfitId, calendarManager,
                                navController, postViewModel)
                        }
                    }

                    // add CreateOutfitScreen Route to separate composable in nav graph
                    composable<Routes.CreateOutfitScreen> { backStackEntry ->
                        // when user navigates to create outfit screen from outfit calendar
                        val dateString = backStackEntry.arguments?.getString("date")
                        val selectedDate = dateString?.let { LocalDate.parse(it) }

                        OutfitScreen(
                            navController = navController,
                            clothingItemViewModel = sharedClothingItemViewModel,
                            outfitViewModel = sharedOutfitViewModel.apply {
                                if (selectedDate != null) {
                                    // Set the selected date in ViewModel if provided
                                    setSelectedDate(selectedDate.toString())
                                }
                            }
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
                                onDone = {Routes.CreateOutfitScreen},
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
                        val swapViewModel: SwapViewModel = koinViewModel()

                        ProfileScreenRoot(
                            viewModel,
                            swapViewModel,
                            navController
                        )
                    }

                    composable<Routes.UserSearchScreen> {
                        val viewModel: ProfileTabViewModel = koinViewModel()

                        UserSearchScreen(
                            profileViewModel = viewModel,
                            navController = navController
                        )
                    }

                    composable<Routes.UserFriendsScreen> {
                        val viewModel: ProfileTabViewModel = koinViewModel()

                        UserFriendsScreen(
                            profileViewModel = viewModel,
                            navController = navController
                        )
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
                                navController.navigate(Routes.SwapDetailsScreen(swap.swap.itemId.id))
                            },
                            onAllSwapClick = { navController.navigate(Routes.AllSwapScreen(userManager.currentUser.value?.id?:0)) },
                            onAddSwapClick = { navController.navigate(Routes.AddSwapScreen)}
                        )
                    }

                    composable<Routes.AllSwapScreen> { backStackEntry ->
                        val args = backStackEntry.toRoute<Routes.AllSwapScreen>()
                        val selectedSwapViewModel = backStackEntry.sharedKoinViewModel<SelectedSwapViewModel>(navController)

                        AllSwapsScreen(
                            userId = args.userId,
                            onSwapClick = { swap ->
                                selectedSwapViewModel.onSelectSwap(
                                    swap.toOtherSwapDto(user = MessageUserDto(id = args.userId))
                                )
                                navController.navigate(Routes.SwapDetailsScreen(swap.itemId.id))
                            }
                        )
                    }

                    composable<Routes.AddSwapScreen>{
                        val viewModel: AddSwapViewModel = koinViewModel()
                        AddSwapRoot(
                            viewModel = viewModel,
                            onWardrobeClick = { navController.navigate(Routes.AddSwapItemScreen) }
                        )
                    }

                    composable<Routes.AddSwapItemScreen> {
                        val viewModel: AddSwapViewModel = koinViewModel()
                        AddSwapItemRoot(
                            viewModel = viewModel,
                            onAddClick = { navController.navigate(Routes.SwapTab) }
                        )
                    }

                    composable<Routes.SwapDetailsScreen> {
                        val selectedSwapViewModel = it.sharedKoinViewModel<SelectedSwapViewModel>(navController)
                        val selectedSwap by selectedSwapViewModel.selectedSwap.collectAsStateWithLifecycle()
                        val messageViewModel: MessageViewModel = koinViewModel()
                        val currentUser by messageViewModel.currentUser.collectAsStateWithLifecycle()

                        SwapDetailScreen(
                            swap = selectedSwap,
                            onBackClick = { navController.navigate(Routes.SwapTab)},
                            onRequestClick = { swapItem ->
                                // Navigate to chat
                                val otherUser = MessageManager.currentOtherUser
                                val currentUserId = currentUser?.id
                                if (currentUser != null && otherUser != null && currentUserId != null) {
                                    val initialMessage = "I'm interested in this item: ${selectedSwap?.swap?.itemId?.name} ${selectedSwap?.swap?.itemId?.size}"
                                    messageViewModel.sendMessage(
                                        senderId = currentUserId,
                                        receiverId = otherUser.id,
                                        content = initialMessage
                                    )
                                }

                                navController.navigate(Routes.ChatScreen)
                            }
                        )
                    }

                    composable<Routes.ProfileDetailsScreen> {backStackEntry ->
                        val viewModel: ProfileTabViewModel = koinViewModel()
                        val args = backStackEntry.toRoute<Routes.ProfileDetailsScreen>()
                        val swapViewModel: SwapViewModel = koinViewModel()
                        val selectedSwapViewModel = backStackEntry.sharedKoinViewModel<SelectedSwapViewModel>(navController)

                        LaunchedEffect(args.userId) {
                            viewModel.loadProfile(args.userId)
                        }

                        ProfileScreen(
                            userId = args.userId,
                            profileViewModel = viewModel,
                            navController = navController,
                            swapViewModel = swapViewModel,
                            onSwapClick = { swap ->
                                selectedSwapViewModel.onSelectSwap(swap)
                                navController.navigate(Routes.SwapDetailsScreen(swap.swap.itemId.id))
                            },
                            onAllSwapClick = { navController.navigate(Routes.AllSwapScreen(args.userId)) },
                        )
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
                        val viewModel: PostViewModel = koinViewModel()
                        SocialTabScreen(
                            user = userAccount,
                            onNavigate = {},
                            navController = navController,
                            viewModel = viewModel,
                        )
                    }
                    composable<Routes.SocialDetailsScreen> {
                        val args = it.toRoute<Routes.SocialDetailsScreen>()
                        val viewModel: PostViewModel = koinViewModel()

                        PostDetailScreen(viewModel = viewModel, navController = navController, outfitId = args.outfitId)
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

                composable<Routes.EditProfileScreen> {
                    val viewModel = koinViewModel<EditProfileViewModel>()
                    if (photoManager != null) {
                        EditProfileScreen(
                            viewModel = viewModel,
                            photoManager = photoManager
                        )
                    }
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
                    if (!isSelected) {
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(imageVector = icon, contentDescription = null) },
                label = { Text(formattedLabel) }
            )
        }

        AddNewItem { navController.navigate(Routes.AddItemScreen) }

        tabs.takeLast(2).forEachIndexed { index, (route, icon) ->
            val isSelected = currentTab == route::class.simpleName
            val formattedLabel = route::class.simpleName?.removeSuffix("Tab") ?: "Null tab name"

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
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
    onlyBackButton: Boolean = false,
    disableBack: Boolean = false
) {
    val hasNewNotifications by NotificationEventBus.hasNewNotifications.collectAsState()
    val newMessages by SwapEventBus.hasNewNotifications.collectAsState()

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            if (!disableBack)
            {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (!onlyBackButton)
            {
                Box {
                    IconButton(onClick = { navController.navigate(Routes.MessageListScreen) })
                    {
                        Icon(
                            imageVector = Icons.Default.MailOutline,
                            contentDescription = "Messages",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    if (newMessages) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(12.dp)
                                .background(MaterialTheme.colorScheme.error, CircleShape)
                        )
                    }
                }

                // Notification Icon with Indicator
                Box {
                    IconButton(onClick = { navController.navigate(Routes.NotificationsScreen) }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            modifier = Modifier.size(30.dp)
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
                        contentDescription = "Settings",
                        modifier = Modifier.size(30.dp)
                    )
                }
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
        Icon(Icons.Filled.Add, stringResource(Res.string.add_new_item_button))
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}