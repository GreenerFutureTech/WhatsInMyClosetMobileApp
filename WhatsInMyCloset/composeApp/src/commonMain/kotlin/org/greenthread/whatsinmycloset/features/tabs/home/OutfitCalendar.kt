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
import org.greenthread.whatsinmycloset.core.viewmodels.ClothingItemViewModel
import org.greenthread.whatsinmycloset.core.viewmodels.OutfitViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitDatePicker(
    onDismiss: () -> Unit,
    outfitViewModel: OutfitViewModel,
    clothingItemViewModel: ClothingItemViewModel,
    selectedTags: Set<String>,
    navController: NavController
) {
    val currentDate = remember { DateUtils.getCurrentLocalDate() }
    val initialMillis = remember { DateUtils.localDateToMillis(currentDate) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    val outfitName by outfitViewModel.outfitName.collectAsState()
    var showDateErrorDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }

    // DatePickerDialog layout
    DatePickerDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight().fillMaxWidth(),
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    selectedDate = DateUtils.convertDate(millis = millis).second.toString()
                    outfitViewModel.viewModelScope.launch {
                        val success = outfitViewModel.saveOutfit(
                            selectedTags = selectedTags.toList(),
                            outfitName = outfitName,
                            addToCalendar = true,
                            date = selectedDate
                        )
                        if (!success) {
                            showDateErrorDialog = true
                        } else {
                            onDismiss()
                        }
                    }
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        })
    {
        DatePicker(state = datePickerState)
    }

    // Show error dialog when outfit exists for selected date
    if (showDateErrorDialog) {
        AlertDialog(
            onDismissRequest = { showDateErrorDialog = false },
            title = { Text("Calendar Error") },
            text = { Text("There was an error saving the calendar... Please try again") },
            confirmButton = {
                TextButton(
                    onClick = { showDateErrorDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitOfTheDayCalendar(
    navController: NavController,
    outfitViewModel: OutfitViewModel,
    onDismiss: () -> Unit
) {
    val calendarEvents by outfitViewModel.calendarEvents.collectAsState()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    val dateToday = Clock.System.now().toEpochMilliseconds()

    // State for the DatePicker
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateToday)

    // DatePickerDialog layout
    DatePickerDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight().fillMaxWidth(),
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = DateUtils.millisToLocalDate(millis)
                        navController.navigate(
                            Routes.OutfitDetailScreen(
                                selectedDate.toString())
                        )
                        onDismiss()
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { navController.navigate(Routes.HomeTab) }) {
                Text("Cancel")
            }
        })
    {
        DatePicker(state = datePickerState)
    }
}