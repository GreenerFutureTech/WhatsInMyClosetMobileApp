package org.greenthread.whatsinmycloset.features.tabs.social.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.core.ui.components.outfits.OutfitBox
import org.greenthread.whatsinmycloset.core.utilities.DateUtils.formatDateString
import org.greenthread.whatsinmycloset.features.tabs.profile.presentation.ProfilePicture
import org.greenthread.whatsinmycloset.features.tabs.social.data.OutfitState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PostDetailScreen(
    outfitId: String,
    viewModel: PostViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val outfit = state.outfits.find { it.outfitId == outfitId }

    LaunchedEffect(outfitId) {
        if (outfit == null) {
            viewModel.fetchOutfitById(outfitId)
        } else if (outfit.items.isEmpty()) {
            viewModel.fetchItemsForOutfit(outfitId)
        }
    }

    if (outfit == null || outfit.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            UserInfoSection(outfit.username, outfit.profilePicture)

            Spacer(modifier = Modifier.height(16.dp))

            // Outfit box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp) // Optional inner padding
            ) {
                OutfitBox(
                    state = outfit,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tags list (if available)
            if (outfit.tags.isNotEmpty()) {
                TagsSection(tags = outfit.tags)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Creation date
            CreationDateSection(date = outfit.createdAt)
        }
    }
}

@Composable
private fun UserInfoSection(username: String?, profilePicture: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Placeholder for user avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            ProfilePicture(profilePicture)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "@${username ?: "unknown"}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsSection(tags: List<String>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tags.forEach { tag ->
                Text(
                    text = "#$tag",
                    modifier = Modifier
                        .padding(end = 8.dp, bottom = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun CreationDateSection(date: String?) {
    Text(
        text = "Posted on: ${formatDateString(date)}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
}