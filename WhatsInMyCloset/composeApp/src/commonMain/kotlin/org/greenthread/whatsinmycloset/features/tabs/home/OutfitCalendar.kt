package org.greenthread.whatsinmycloset.features.tabs.home


import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.greenthread.whatsinmycloset.app.Routes
import org.greenthread.whatsinmycloset.core.utilities.DateUtils
import org.greenthread.whatsinmycloset.core.viewmodels.OutfitViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitDatePicker(
    onDismiss: () -> Unit,
    outfitViewModel: OutfitViewModel,
    selectedTags: Set<String>,
    onSuccess: () -> Unit = {}
) {
    val dateToday = Clock.System.now().toEpochMilliseconds()

    // State for the DatePicker
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateToday)
    var showConfirmation by remember { mutableStateOf(false) } // Track confirmation dialog
    var showDiscardDialog by remember { mutableStateOf(false) } // Track discard dialog
    var showOutfitNameDialog by remember { mutableStateOf(false) }

    var selectedDate by remember { mutableStateOf("") }

    // DatePickerDialog layout
    DatePickerDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight().fillMaxWidth(),
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    selectedDate = DateUtils.millisToLocalDateString(millis)
                    // pop up to get outfit name from user
                    showOutfitNameDialog = true
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

    // Get outfit name from user adding it to calendar
    if (showOutfitNameDialog) {
        OutfitNameDialog(
            showDialog = showOutfitNameDialog,
            onDismiss = {
                showOutfitNameDialog = false
                onDismiss()
            },
            onSave = { name ->
                outfitViewModel.viewModelScope.launch {
                    val success = outfitViewModel.saveOutfit(
                        selectedTags = selectedTags.toList(),
                        outfitName = name,
                        addToCalendar = true,
                        date = selectedDate
                    )

                    if (success) {
                        showConfirmation = true
                        onSuccess()
                    }
                    showOutfitNameDialog = false
                }
            }
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitOfTheDayCalendar(
    navController: NavController,
    outfitViewModel: OutfitViewModel
) {
    val calendarEvents by outfitViewModel.calendarEvents.collectAsState()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // TODO get outfit dates from backend Calendar Entity
    val outfitDates = remember(calendarEvents) {
    }

    val dateToday = Clock.System.now().toEpochMilliseconds()

    // State for the DatePicker
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateToday)
    var showConfirmation by remember { mutableStateOf(false) } // Track confirmation dialog
    var showDiscardDialog by remember { mutableStateOf(false) } // Track discard dialog


    // DatePickerDialog layout
    DatePickerDialog(
        onDismissRequest = { showDiscardDialog = true },
        modifier = Modifier.fillMaxHeight().fillMaxWidth(),
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = DateUtils.millisToLocalDate(millis)
                        // TODO check if the selected date has an outfit saved
                        // val hasOutfit = outfitDates.containsKey(selectedDate)
                        val hasOutfit = true

                        if (hasOutfit) {
                            navController.navigate(
                                Routes.OutfitDetailScreen(
                                    selectedDate.toString())
                            )
                        } else {
                            navController.navigate(
                                Routes.CreateOutfitScreen(selectedDate.toString())
                            )
                        }
                    }
                }
            ) {
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

    // Close the calendar
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Close Outfit Calendar?") },
            text = { Text("Are you sure you want to close the calendar?") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    navController.navigate(Routes.HomeTab)  // Only navigate after user confirms
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}