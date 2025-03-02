package org.greenthread.whatsinmycloset.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.ui.components.listItems.generateRandomItems
import org.greenthread.whatsinmycloset.core.ui.components.outfits.OutfitComposable
import org.greenthread.whatsinmycloset.core.ui.components.posts.Posts
import org.greenthread.whatsinmycloset.core.ui.components.posts.getCurrentDate


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SocialTab() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }

        // Create a user profile
        val user = Account("user123", "rachelg")

        // Create clothing items
        val redSweater = ClothingItem("1", "Red Sweater", "1", "red_sweater.jpg",
            "Res.drawable.default", listOf("red", "casual"),"20202020")
        val leggings = ClothingItem("2", "Pink Pattern Leggings", "1", "pattern_leggings.jpg",
            "Res.drawable.leggings", listOf("pink", "casual"), "20202020")

        // Generate outfits
        for (i in 0 until 10) {
            val newLook =  Outfit("outfit${i}", "Look${i}", setOf("item1", "item2"))
            user.addOutfit(newLook)
        }

        val friendOutfit = generateRandomItems(1)

        val post = Posts(150.dp, user, friendOutfit[0], getCurrentDate())

        val outfitTest = Outfit(
            id = "1",
            name = "Casual outfit",
            itemIds = setOf("1", "2")
        )

        Column(
            Modifier
                .fillMaxWidth()
                .padding(50.dp),
            horizontalAlignment = Alignment.Start
        ) {
            FriendsOutfitsTitle()

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .width(161.dp)
                    .height(190.dp)
                    .padding(4.dp)
            ) {
                Column {
                    OutfitComposable(outfitTest)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingFromBaseline(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "@${user.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal
                        )

                        Text(
                            text = "${post.createdAt}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun FriendsOutfitsTitle() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Friends' Outfits",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}