package org.greenthread.whatsinmycloset.features.tabs.social.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.ui.components.outfits.OutfitBox
import org.greenthread.whatsinmycloset.features.tabs.profile.presentation.ProfilePicture
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PostDetailScreen(
    outfitId: String,
    viewModel: PostViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val outfit = state.outfits.find { it.outfitId == outfitId }

    LaunchedEffect(outfitId) {
        if (outfit == null) {
            viewModel.fetchOutfitById(outfitId) // Fetch if not loaded
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
            UserInfoSection(user = viewModel.currentUser.value)

            Spacer(modifier = Modifier.height(16.dp))

            // Outfit box
            OutfitBox(
                state = outfit,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun UserInfoSection(user: User?) {
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
            if (user != null) {
                ProfilePicture(user)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "@${user?.username}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}