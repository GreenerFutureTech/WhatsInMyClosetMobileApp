package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.datetime.LocalDate
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.managers.CalendarManager
import org.greenthread.whatsinmycloset.core.persistence.OutfitItems
import org.greenthread.whatsinmycloset.core.ui.components.outfits.OutfitBox
import org.greenthread.whatsinmycloset.features.tabs.social.data.OutfitState
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme

@Composable
fun OutfitDetailScreen(
    date: String, // selected date
    userManager: UserManager,
    calendarManager: CalendarManager,
    navController: NavController,
) {

    WhatsInMyClosetTheme {

        val currentUser = userManager.currentUser
        var outfit by remember { mutableStateOf<Outfit?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }
        var outfitState by remember { mutableStateOf<OutfitState?>(null) }

        LaunchedEffect(date) {
            try {
                val localDate = LocalDate.parse(date)
                outfit = calendarManager.getOutfitForDate(localDate)

                if (outfit == null) {
                    error = "No outfit found for this date"
                } else {
                    // Convert Outfit to OutfitState for the OutfitBox component
                    outfitState = OutfitState(
                        outfitId = outfit!!.id,
                        name = outfit!!.name,
                        itemIds = outfit!!.items.map { (itemId, offsetData) ->
                            OutfitItems(
                                id = itemId,
                                x = offsetData.x,
                                y = offsetData.y
                            )
                        },
                        items = emptyList(), // You'll need to fetch actual items here
                        isLoading = false
                    )
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
            outfitState != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutfitScreenHeader(
                        onExit = { navController.navigate(Routes.HomeTab) },
                        title = "Outfit ${outfit?.name ?: "Untitled"}"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Display the outfit using OutfitBox
                    OutfitBox(
                        state = outfitState!!,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(1f)
                    )

                    // Add additional outfit details if needed
                    OutfitDetailsSection(outfit!!)
                }
            }
        }
    }
}

@Composable
private fun OutfitDetailsSection(outfit: Outfit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (outfit.tags.isNotEmpty()) {
            Text(
                text = "Tags: ${outfit.tags.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp))
        }
        Text(
            text = "Created: ${outfit.createdAt}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}