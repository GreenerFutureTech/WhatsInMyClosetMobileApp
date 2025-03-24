package org.greenthread.whatsinmycloset.core.utilities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateUtils {
    fun millisToLocalDate(millis: Long): String {
        return Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.UTC) // Use UTC to avoid timezone shifts
            .date
            .toString() // Returns format: 2024-03-24
    }

    fun getCurrentDate(): String {
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .toString()
    }

    fun formatDate(localDate: LocalDate): String {
        return "${localDate.dayOfMonth}/${localDate.monthNumber}/${localDate.year}"
    }
}