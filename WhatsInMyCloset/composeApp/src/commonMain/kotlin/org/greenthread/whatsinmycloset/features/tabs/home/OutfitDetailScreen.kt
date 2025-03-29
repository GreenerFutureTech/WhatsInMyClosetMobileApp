package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import kotlinx.datetime.LocalDate
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.managers.CalendarManager
import org.greenthread.whatsinmycloset.features.tabs.social.presentation.PostDetailScreen
import org.greenthread.whatsinmycloset.features.tabs.social.presentation.PostViewModel
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OutfitDetailScreen(
    date: String,
    userManager: UserManager,
    calendarManager: CalendarManager,
    navController: NavController,
    postViewModel: PostViewModel = koinViewModel()
) {
    WhatsInMyClosetTheme {
        val currentUser = userManager.currentUser
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }
        var outfitId by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(date) {
            try {
                val localDate = LocalDate.parse(date)
                val calendarOutfit = calendarManager.getOutfitForDate(localDate)

                if (calendarOutfit == null) {
                    error = "No outfit found for this date"
                } else {
                    outfitId = calendarOutfit.id
                }
            } catch (e: Exception) {
                error = "Failed to load outfit: ${e.message}"
            } finally {
                isLoading = false
            }
        }

        when {
            isLoading -> CircularProgressIndicator()
            error != null -> Text(error!!)
            outfitId != null -> {
                PostDetailScreen(
                    outfitId = outfitId!!,
                    viewModel = postViewModel
                )
            }
        }
    }
}