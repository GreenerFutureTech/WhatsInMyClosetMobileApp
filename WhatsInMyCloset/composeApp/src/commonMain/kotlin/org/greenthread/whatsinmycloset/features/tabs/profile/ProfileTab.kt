package org.greenthread.whatsinmycloset.features.tabs.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyGridColourBox
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyRowColourBox
import org.greenthread.whatsinmycloset.core.ui.components.listItems.generateRandomItems
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.SeeAllButton
import org.greenthread.whatsinmycloset.getScreenWidthDp
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.my_outfits_title

@Composable
fun ProfileTabScreen(userState: StateFlow<User?>, onNavigate: () -> Unit) {
    val currentUser by userState.collectAsState()

    WhatsInMyClosetTheme {
        var showContent by remember { mutableStateOf(false) }

        // Generate outfits
        /*val numberOfOutfits = 10
        val outfits = List(numberOfOutfits) { i ->
            Outfit(
                id = "outfit1",
                userId = 1,
                public = true,
                favorite = true,
                mediaURL = "",
                name = "Summer Look",
                itemIds = listOf("15", "7", "9"),
                createdAt = "08/03/2025"
            )
        }

        // Add generated outfits to the user
        outfits.forEach { currentUser?.addOutfit(it, listOf("Public Outfits", "Fancy")) }*/

        val randomItems = generateRandomItems(currentUser?.getAllOutfits()?.size ?: 0) // Generate 10 random items for the preview
        val swapItems = generateRandomItems(10)

        Column(Modifier
            .fillMaxWidth(),
            horizontalAlignment = Alignment.Start) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Profile Image
                ProfilePicture()

                Column(Modifier.padding(16.dp)) {
                    // Username
                    Username(currentUser?.name ?: "No username found")

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        FriendsCount(12)

                        Spacer(modifier = Modifier.width(16.dp))

                        SwapsCount(10)
                    }
                }
            }

            SearchBar()

            SwapTitle()

            LazyRowColourBox(items = swapItems)

            MyOutfitsTitle(Res.string.my_outfits_title)

            LazyGridColourBox(items = randomItems)
        }
    }
}

