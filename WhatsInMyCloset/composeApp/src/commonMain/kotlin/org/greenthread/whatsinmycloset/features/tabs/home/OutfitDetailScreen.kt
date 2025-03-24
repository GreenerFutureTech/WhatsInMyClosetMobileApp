package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun OutfitDetailScreen(
    outfitId: String,
    navController: NavController,
) {

    OutfitScreenHeader(
        onExit = { navController.popBackStack() },  // go to prev screen
        title = "Outfit {Name}" // TODO show outfit name in title
    )

    // TODO Use Outfit Composable here to show the outfit
}