package org.greenthread.whatsinmycloset.features.screens.notifications.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.greenthread.whatsinmycloset.features.screens.notifications.domain.model.Notification
import org.koin.compose.viewmodel.koinViewModel

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.LaunchedEffect
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.compose.resources.stringResource
import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.notification_sent
import whatsinmycloset.composeapp.generated.resources.notification_header
import whatsinmycloset.composeapp.generated.resources.notification_clear
import whatsinmycloset.composeapp.generated.resources.notification_none

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: NotificationsViewModel = koinViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )

    // Reset the new notifications state when the screen is opened
    LaunchedEffect(Unit) {
        viewModel.clearNewNotificationsState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.notification_header),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pullRefresh(refreshState) // Apply pullRefresh at this level
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                if (notifications.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(onClick = { viewModel.clearAllNotifications() }) {
                                Text(stringResource(Res.string.notification_clear))
                            }
                        }
                    }

                    items(notifications) { notification ->
                        NotificationItem(
                            notification = notification,
                            onClick = { viewModel.gotoNotification(navController, it) },
                            onDismiss = { notificationId -> viewModel.dismissNotification(notificationId) }
                        )
                    }
                } else {
                    // Keep an "invisible" item to allow pull-to-refresh
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 50.dp), // Adds spacing for the pull gesture
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(Res.string.notification_none))
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: (Notification) -> Unit,
    onDismiss: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(notification) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .alpha(if (notification.isRead) 0.7f else 1.0f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display notification details
            Column(modifier = Modifier.weight(1f)) {
                Text(text = notification.title, style = MaterialTheme.typography.titleMedium)
                Text(text = notification.body, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "${stringResource(Res.string.notification_sent)}: ${getRelativeTimeSpan(notification.createdAt)}",
                    style = MaterialTheme.typography.bodySmall

                )
            }

            // Dismiss button
            IconButton(onClick = { onDismiss(notification.id) }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss notification")
            }
        }
    }
}

// KMP-compatible relative time formatting
fun getRelativeTimeSpan(isoDateString: String): String {
    try {
        // Parse the ISO string
        val dateTime = parseIsoDate(isoDateString)

        // Get current time
        val now = Clock.System.now()

        // Calculate difference in seconds
        val diffSeconds = (now - dateTime).inWholeSeconds

        return when {
            diffSeconds < 60 -> "Just now"
            diffSeconds < 3600 -> "${diffSeconds / 60} minute(s) ago"
            diffSeconds < 86400 -> "${diffSeconds / 3600} hour(s) ago"
            diffSeconds < 172800 -> "Yesterday"
            diffSeconds < 2592000 -> "${diffSeconds / 86400} day(s) ago"
            diffSeconds < 31536000 -> "${diffSeconds / 2592000} month(s) ago"
            else -> "${diffSeconds / 31536000} year(s) ago"
        }
    } catch (e: Exception) {
        return isoDateString
    }
}

// Helper function to parse ISO date string
private fun parseIsoDate(isoDateString: String): Instant {
    val regex = """(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})""".toRegex()
    val matchResult = regex.find(isoDateString) ?: throw IllegalArgumentException("Invalid date format")

    val (yearStr, monthStr, dayStr, hourStr, minuteStr, secondStr) = matchResult.destructured

    val year = yearStr.toInt()
    val month = monthStr.toInt()
    val day = dayStr.toInt()
    val hour = hourStr.toInt()
    val minute = minuteStr.toInt()
    val second = secondStr.toInt()

    return LocalDateTime(year, month, day, hour, minute, second)
        .toInstant(TimeZone.UTC)
}
