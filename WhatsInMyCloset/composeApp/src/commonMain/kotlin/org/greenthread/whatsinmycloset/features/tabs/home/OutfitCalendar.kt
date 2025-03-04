package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyGridCalendarUI
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme

@Composable
fun CalendarDialog(
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    WhatsInMyClosetTheme {
        var selectedDay by remember { mutableStateOf(7) }  // Default day
        var selectedMonth by remember { mutableStateOf("February") }  // Default month
        var selectedYear by remember { mutableStateOf(2027) } // Default year
        var showConfirmation by remember { mutableStateOf(false) } // Track confirmation dialog
        var showDiscardDialog by remember { mutableStateOf(false) } // Track discard dialog

        AlertDialog(
            onDismissRequest = { showDiscardDialog = true },  // Show discard dialog when closing
            title = { Text("Add Outfit to Calendar") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$selectedMonth $selectedYear", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Generate list of days (1-31)
                    val daysInMonth = (1..31).toList()
                    LazyGridCalendarUI(
                        items = daysInMonth,
                        selectedDay = selectedDay,
                        onDayClick = { day -> selectedDay = day }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmation = true // Show confirmation dialog
                        val selectedDate = "$selectedMonth $selectedDay, $selectedYear"
                        onDateSelected(selectedDate)
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = true }) { // Show discard dialog
                    Text("Cancel")
                }
            }
        )

        // Show confirmation dialog when outfit is added
        if (showConfirmation) {
            ConfirmationDialog(
                message = "Outfit added to date $selectedMonth $selectedDay, $selectedYear",
                onDismiss = { showConfirmation = false }
            )
        }

        // Show discard confirmation dialog when cancel is clicked
        if (showDiscardDialog) {
            DiscardConfirmationDialog(
                onConfirm = {
                    showDiscardDialog = false
                    onDismiss()
                },
                onDismiss = { showDiscardDialog = false }
            )
        }
    }
}


@Composable
fun ConfirmationDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmation") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun DiscardConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancel adding outfit to calendar") },
        text = { Text("Are you sure you don't want to add outfit to calendar?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}