package org.greenthread.whatsinmycloset.features.tabs.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import org.greenthread.whatsinmycloset.core.ui.components.listItems.LazyGridCalendarUI
import org.greenthread.whatsinmycloset.core.utilities.DateUtils
import org.greenthread.whatsinmycloset.theme.WhatsInMyClosetTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitDatePicker(
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val dateToday = Clock.System.now().toEpochMilliseconds()

    // State for the DatePicker
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateToday)
    var showConfirmation by remember { mutableStateOf(false) } // Track confirmation dialog
    var showDiscardDialog by remember { mutableStateOf(false) } // Track discard dialog

    var selectedDate by remember { mutableStateOf("") }

    // DatePickerDialog layout
    DatePickerDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight().fillMaxWidth(),
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    selectedDate = DateUtils.millisToLocalDate(millis)
                    onDateSelected(selectedDate)
                    showConfirmation = true // This will trigger recomposition
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { showDiscardDialog = true }) {
                Text("Cancel")
            }
        })
        {
            DatePicker(state = datePickerState)
        }

    // Show confirmation dialog when outfit is added
    if (showConfirmation) {
        ConfirmationDialog(

            message = "Outfit added to date $selectedDate",
            onDismiss = {
                showConfirmation = false
                onDismiss() // Only dismiss after confirmation is closed
            }
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


@Composable
fun ConfirmationDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Success") },
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