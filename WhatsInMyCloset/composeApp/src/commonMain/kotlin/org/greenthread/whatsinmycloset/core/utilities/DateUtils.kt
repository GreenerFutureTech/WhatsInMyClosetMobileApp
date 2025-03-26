package org.greenthread.whatsinmycloset.core.utilities

import androidx.compose.ui.input.key.Key.Companion.Calendar
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateUtils {
    fun millisToLocalDateString(millis: Long): String {
        return Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.UTC) // Use UTC to avoid timezone shifts
            .date
            .toString() // Returns format: 2024-03-24
    }

    fun millisToLocalDate(millis: Long): LocalDate {
        return Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.UTC) // Use UTC to avoid timezone shifts
            .date
    }

    fun CovertDateToString(date: LocalDate)
    {

    }

    fun getCurrentLocalDate(): LocalDate {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    fun getCurrentDate(): String {
        return getCurrentLocalDate().toString()
    }
}