package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.datetime.LocalDate
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.managers.CalendarManager
import org.greenthread.whatsinmycloset.features.tabs.social.presentation.PostDetailScreen
import org.greenthread.whatsinmycloset.features.tabs.social.presentation.PostViewModel
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OutfitDetailScreen(
    date: String,
    calendarManager: CalendarManager,
    navController: NavController,
    postViewModel: PostViewModel = koinViewModel()
) {
    WhatsInMyClosetTheme {
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }
        var outfitId by remember { mutableStateOf<String?>(null) }
        var outfitName by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(date) {
            try {
                val localDate = LocalDate.parse(date)
                val calendarOutfit = calendarManager.getOutfitForDate(localDate)
                if (calendarOutfit == null) {
                    error = "No outfit found for this date"
                } else {
                    outfitName = calendarOutfit.name
                    outfitId = calendarOutfit.id
                }
            } catch (e: Exception) {
                error = "Failed to load outfit: ${e.message}"
            } finally {
                isLoading = false
            }
        }


        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Only show header if we have an outfit name
            outfitName?.let { name ->
                OutfitScreenHeader(
                    onExit = { navController.navigate(Routes.HomeTab) },
                    title = name
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> CircularProgressIndicator()
                    error != null -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "No outfit",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = error!!,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    outfitId != null -> {
                        PostDetailScreen(
                            outfitId = outfitId!!,
                            viewModel = postViewModel
                        )
                    }

                    else -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "No outfit",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No outfit scheduled for this date",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}