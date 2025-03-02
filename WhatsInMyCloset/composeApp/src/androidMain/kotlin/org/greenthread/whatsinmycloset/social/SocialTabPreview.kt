package org.greenthread.whatsinmycloset.social

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.ui.components.posts.Post
import org.greenthread.whatsinmycloset.core.ui.components.posts.SocialFeedScreen
import org.greenthread.whatsinmycloset.core.ui.components.posts.getCurrentDate


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SocialTab() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }

        // Create a user profile
        val user = Account("user123", "rachelg")

        // Generate outfits
        for (i in 0 until 10) {
            val newLook =  Outfit("$i", "Look${i}", setOf("1", "2"))
            user.addOutfit(newLook)
        }

        // Generate posts
        val postsList = mutableListOf<Post>()

        for (i in 0 until 10) {
            val post = user.getOutfit("${i}")?.let { Post(190.dp, user, it, getCurrentDate()) }
            if (post != null) {
                postsList.add(post)
            }
        }

        SocialFeedScreen(postsList)
    }
}

