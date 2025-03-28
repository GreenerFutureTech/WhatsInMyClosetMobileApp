package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.repositories.CalendarRepository
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme

@Composable
fun OutfitDetailScreen(
    outfitId: String,
    //userManager: UserManager,
    //calendarRepository: CalendarRepository,
    navController: NavController,
) {

    WhatsInMyClosetTheme {

        //val currentUser = userManager.currentUser

        //currentUser.value.id?.let { calendarRepository.getOutfitsFromCalendar(it) }

        OutfitScreenHeader(
            onExit = { navController.navigate(Routes.HomeTab) },  // go to home screen on exit
            title = "Outfit {Name}" // TODO show outfit name in title
        )

        // TODO Use Outfit Composable here to show the outfit
    }

}