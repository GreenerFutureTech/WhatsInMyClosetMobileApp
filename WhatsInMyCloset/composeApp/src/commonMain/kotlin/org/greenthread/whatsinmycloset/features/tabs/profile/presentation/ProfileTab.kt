package org.greenthread.whatsinmycloset.features.tabs.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.features.tabs.profile.ProfileTabViewModel
import org.greenthread.whatsinmycloset.features.tabs.swap.State.SwapListState
import org.greenthread.whatsinmycloset.features.tabs.swap.viewmodel.SwapViewModel


@Composable
fun FullScreenLoading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

// TODO add try-catch for search
// TODO change design for search result

@Composable
fun ProfileScreenRoot(
    viewModel: ProfileTabViewModel,
    swapViewModel: SwapViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val swapState by swapViewModel.state.collectAsStateWithLifecycle(
        initialValue = SwapListState(),
        lifecycle = lifecycle
    )

    LaunchedEffect(Unit) {
        // Load the current user's profile by default
        viewModel.loadProfile(viewModel.currentUser.value?.id ?: -1)
    }

    LaunchedEffect(swapState) {
        if (swapState.getUserSwapResults.isEmpty()) {
            swapViewModel.fetchSwapData(swapViewModel.currentUser.value?.id.toString())
        }
    }

    LaunchedEffect(state.user) {
        // When user data is loaded, navigate to details screen
        if (state.user != null && !state.isLoading) {
            navController.navigate(Routes.ProfileDetailsScreen(state.user!!.id!!))
        }
    }

    if (state.isLoading) {
        FullScreenLoading()
    } else if (state.error != null) {
        // Handle error state
        Text("Error: ${state.error}")
    }
}