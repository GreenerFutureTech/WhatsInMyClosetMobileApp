package org.greenthread.whatsinmycloset.features.screens.notifications.presentation

import androidx.compose.foundation.clickable
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

@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: NotificationsViewModel = koinViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (notifications.isNotEmpty()) {
            Button(
                onClick = { viewModel.clearAllNotifications() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Clear All")
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = { viewModel.markAsRead(it.id) },
                        onDismiss = { notificationId -> viewModel.dismissNotification(notificationId) }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No notifications")
            }
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
                Text(text = "Type: ${notification.type}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Created: ${notification.createdAt}", style = MaterialTheme.typography.bodySmall)
            }

            // Dismiss button
            IconButton(onClick = { onDismiss(notification.id) }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss notification")
            }
        }
    }
}