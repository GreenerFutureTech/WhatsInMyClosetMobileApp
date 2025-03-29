package org.greenthread.whatsinmycloset.core.ui.components.posts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.User
import org.greenthread.whatsinmycloset.core.ui.components.outfits.OutfitBox
import org.greenthread.whatsinmycloset.features.tabs.social.data.OutfitState

data class Post(
    val postID: String,
    val creator: User,
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
fun PostCard(
    outfit: OutfitState,
    currentUser: User?,
    modifier: Modifier = Modifier,
    onPostClick: (() -> Unit)? = null
) {
    ElevatedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onPostClick?.invoke() }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            OutfitBox(
                state = outfit,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                outfit.username?.let {
                    Text(
                        text = outfit.username,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = outfit.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}