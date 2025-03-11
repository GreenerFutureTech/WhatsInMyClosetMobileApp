package org.greenthread.whatsinmycloset.features.tabs.swap.presentation.Message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.greenthread.whatsinmycloset.core.domain.models.UserManager
import org.greenthread.whatsinmycloset.core.dto.MessageDto

@Composable
fun MessageItem(
    message: MessageDto,
    isSender: Boolean
) {
    val alignment = if (isSender) Alignment.End else Alignment.Start
    val backgroundColor = if (isSender) Color(0xFFDCF8C6) else Color(0xFFECECEC)
    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .widthIn(max = 250.dp)
                .background(backgroundColor)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message.content,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = formatTime(message.sentAt),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}



fun formatTime(isoTime: String): String {
    val instant = Instant.parse(isoTime)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    var hour = localDateTime.hour % 12
    if (hour == 0) {
        hour = 12
    }

    val minute = if (localDateTime.minute < 10) "0${localDateTime.minute}" else "${localDateTime.minute}"
    val amPm = if (localDateTime.hour < 12) "AM" else "PM"

    val formattedTime = "$hour:$minute $amPm"
    return formattedTime
}


@Composable
fun ChatList(
    modifier: Modifier = Modifier,
    currentUserId: Int,
    messages: List<MessageDto>,
) {

    val listState = rememberLazyListState()

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }
    LazyColumn(
        modifier = modifier
            .padding(8.dp),
        state = listState,
        reverseLayout = false
    ) {
        items(messages) { message ->
            val isSender = message.sender.id == currentUserId
            MessageItem(
                message = message,
                isSender = isSender
            )
        }
    }
}
