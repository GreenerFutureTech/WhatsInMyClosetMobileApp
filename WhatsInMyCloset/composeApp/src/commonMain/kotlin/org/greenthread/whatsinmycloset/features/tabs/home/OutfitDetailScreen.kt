package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme

@Composable
fun OutfitDetailScreen(
    outfitId: String,
    navController: NavController,
) {

    WhatsInMyClosetTheme {
        OutfitScreenHeader(
            onExit = { navController.navigate(Routes.HomeTab) },  // go to home screen on exit
            title = "Outfit {Name}" // TODO show outfit name in title
        )

        // TODO Use Outfit Composable here to show the outfit
    }

}