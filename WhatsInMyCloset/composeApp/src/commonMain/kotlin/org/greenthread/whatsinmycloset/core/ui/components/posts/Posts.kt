package org.greenthread.whatsinmycloset.core.ui.components.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import org.greenthread.whatsinmycloset.core.domain.models.Account
import org.greenthread.whatsinmycloset.core.ui.components.listItems.ListItem
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.greenthread.whatsinmycloset.core.ui.components.listItems.RandomColourBox

data class Posts(
    val height: Dp,
    val user: Account,
    val outfit: ListItem,
    val createdAt: LocalDate
)

@Composable
fun CreateNewPost(post: Posts) {
    Box(
        modifier = Modifier
            .width(125.dp)
            .height(post.height)
            .padding(4.dp)
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(10.dp))
    ) {
        RandomColourBox(post.outfit)
    }
}

fun getCurrentDate(): LocalDate {
    // Get current Instant
    val currentInstant = Clock.System.now()

    // Convert it to LocalDateTime
    val localDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())

    // Extract and return the date only
    return localDateTime.date
}