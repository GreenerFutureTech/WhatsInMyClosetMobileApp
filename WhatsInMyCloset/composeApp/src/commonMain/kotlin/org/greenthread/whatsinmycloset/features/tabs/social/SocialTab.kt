package org.greenthread.whatsinmycloset.features.tabs.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.domain.models.ClothingCategory
import org.greenthread.whatsinmycloset.core.domain.models.ClothingItem
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.ui.components.posts.LazyGridPosts
import org.greenthread.whatsinmycloset.core.ui.components.posts.Post
import org.greenthread.whatsinmycloset.core.ui.components.posts.getCurrentDate
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme

@Composable
fun SocialTabScreen(user: Account?, onNavigate: (String) -> Unit) {
    WhatsInMyClosetTheme {
        var showContent by remember { mutableStateOf(false) }

        // Create a user profile
        val currentUser = user

        val testOutfit = listOf(
            ClothingItem(
                id = "1",
                name = "Red Sweater",
                wardrobeId = "1",
                itemType = ClothingCategory.TOPS,
                mediaUrl = "url_to_sweater.png", // Local resource key or remote URL
                tags= listOf("red", "casual"),
                createdAt = "20202020"
            ),
            ClothingItem(
                id = "2",
                name = "Pink Pattern Leggings",
                wardrobeId = "1",
                itemType = ClothingCategory.BOTTOMS,
                mediaUrl = "url_to_leggings.png", // Local resource key or remote URL
                tags= listOf("pink", "casual"),
                createdAt = "20202020"
            )
        )

        // Generate outfits
        for (i in 0 until 10) {
            val newLook =  Outfit("$i", "Look${i}", testOutfit)
            currentUser?.addOutfit(newLook)
        }

        // Generate posts
        val postsList = mutableListOf<Post>()

        if (currentUser != null) {
        for (i in 0 until 10) {
            val post = currentUser.getOutfit("$i")?.let { Post("$i", currentUser, it, getCurrentDate()) }
            if (post != null) {
                postsList.add(post)
            }
        }
        }
        SocialFeedScreen(postsList)
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
        androidx.compose.material3.Text(
            text = "Friends' Outfits",
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SocialFeedScreen(postsList: MutableList<Post>) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.Start
    ) {
        FriendsOutfitsTitle()

        LazyGridPosts(postsList)
    }
}