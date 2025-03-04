package org.greenthread.whatsinmycloset.core.ui.components.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.ui.components.outfits.OutfitComposable

data class Post(
    val postID: String,
    val creator: Account,
    val outfit: Outfit,
    val createdAt: LocalDate
)

fun getCurrentDate(): LocalDate {
    // Get current Instant
    val currentInstant = Clock.System.now()

    // Convert it to LocalDateTime
    val localDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())

    // Extract and return the date only
    return localDateTime.date
}

@Composable
fun CreateNewPost(post: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutfitComposable(post.outfit)

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "@${post.creator.name}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${post.createdAt}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LazyGridPosts(postsList: MutableList<Post>) {
    if (postsList.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(postsList) { item ->
                CreateNewPost(post = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("No Posts to display")
        }
    }
}



