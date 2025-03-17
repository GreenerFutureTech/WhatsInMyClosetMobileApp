package org.greenthread.whatsinmycloset.features.tabs.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
//import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.generateSampleClothingItems
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyGridColourBox
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyRowColourBox
import org.greenthread.whatsinmycloset.core.ui.components.listItems.generateRandomItems
import org.greenthread.whatsinmycloset.features.tabs.home.presentation.CategoryItem
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.add_friend_button
import whatsinmycloset.composeapp.generated.resources.this_week_outfits_title

/*@Composable
fun FriendProfileScreen() {
    var showContent by remember { mutableStateOf(false) }
    // Create a user profile
    val user = Account("friend123", "Monica Geller")

    // Generate outfits
    val numberOfOutfits = 5
    val outfits = List(numberOfOutfits) { i ->
        Outfit(
            id = "outfit$i",
            name = "Look$i",
            itemIds = generateSampleClothingItems()
        )
    }

    // Add generated outfits to the user
    outfits.forEach { user.addOutfit(it) }

    val randomItems = generateRandomItems(user.getAllOutfits().size) // Generate 10 random items for the preview
    val swapItems = generateRandomItems(3)

    WhatsInMyClosetTheme {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Profile Image
                ProfilePicture()

                Column(Modifier.padding(16.dp)) {
                    // Username
                    Username(user.name)

                    Spacer(modifier = Modifier.height(16.dp))

                    SwapsCount(10)
                }

                CategoryItem(
                    icon = Icons.Default.Star,
                    text = "Super Swapper",
                    onClick = {}
                )
            }

            SearchBar()

            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ManageFriendButton(
                    onClick = {},
                    action = Res.string.add_friend_button
                )
            }

            SwapTitle()

            LazyRowColourBox(items = swapItems)

            MyOutfitsTitle(Res.string.this_week_outfits_title)

            LazyGridColourBox(items = randomItems)
        }
    }
}*/

@Composable
fun ManageFriendButton(onClick: () -> Unit, action: StringResource) {
    androidx.compose.material.Button(
        onClick = onClick,
        modifier = Modifier
            .padding(4.dp)
    ) {
        Text(text = stringResource(action))
    }
}