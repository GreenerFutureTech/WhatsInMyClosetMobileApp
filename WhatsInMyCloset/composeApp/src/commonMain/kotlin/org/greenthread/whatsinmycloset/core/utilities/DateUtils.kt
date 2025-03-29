package org.greenthread.whatsinmycloset.core.utilities

import androidx.compose.ui.input.key.Key.Companion.Calendar
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateUtils {
    fun millisToLocalDateString(millis: Long): String {
        val instant = Instant.fromEpochMilliseconds(millis)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return localDate.toString()
    }

    fun millisToLocalDate(millis: Long): LocalDate {
        return Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }


    fun getCurrentLocalDate(): LocalDate {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    fun formatDateString(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "Unknown date"

        return try {
            val instant = Instant.parse(dateString)
            val localDateTime = instant.toLocalDateTime(TimeZone.UTC)
            "${localDateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${localDateTime.dayOfMonth}, ${localDateTime.year}"
        } catch (e: Exception) {
            "Invalid date"
        }
    }
}