package org.greenthread.whatsinmycloset.features.tabs.swap.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddSwapRoot(
    viewModel: AddSwapViewModel = koinViewModel()
) {
    val wardrobes by viewModel.wardrobes.collectAsState()

    WhatsInMyClosetTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Wardrobe",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )

            LazyColumn {
                items(wardrobes) { wardrobe ->
                    WardrobeItem(wardrobe)
                }
            }
        }
    }
}

@Composable
fun WardrobeItem(wardrobe: Wardrobe) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = wardrobe.wardrobeName,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}