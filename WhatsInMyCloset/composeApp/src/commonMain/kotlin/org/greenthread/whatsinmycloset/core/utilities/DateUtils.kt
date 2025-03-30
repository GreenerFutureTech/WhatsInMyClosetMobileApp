package org.greenthread.whatsinmycloset.core.utilities

import androidx.compose.ui.input.key.Key.Companion.Calendar
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

object DateUtils {
    fun convertDate(
        millis: Long? = null,
        localDate: LocalDate? = null,
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): Pair<Long, LocalDate> {
        return when {
            millis != null -> {
                val instant = Instant.fromEpochMilliseconds(millis)
                // Get the UTC date directly without timezone conversion
                val utcDate = instant.toLocalDateTime(TimeZone.UTC).date
                millis to utcDate
            }
            localDate != null -> {
                val millis = localDate
                    .atStartOfDayIn(timeZone)
                    .toEpochMilliseconds()
                millis to localDate
            }
            else -> throw IllegalArgumentException("Either millis or localDate must be provided")
        }
    }


    // Simplified conversion functions
    fun millisToLocalDate(millis: Long): LocalDate {
        return convertDate(millis = millis).second
    }

    fun localDateToMillis(localDate: LocalDate): Long {
        return convertDate(localDate = localDate).first
    }

    fun getCurrentLocalDate(): LocalDate {
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }

    fun formatDateString(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "Unknown date"

        return try {
            // First try to parse as ISO instant (UTC)
            val instant = Instant.parse(dateString)
            val utcDate = instant.toLocalDateTime(TimeZone.UTC).date

            // Format as "Month Day, Year"
            "${utcDate.month.name.lowercase().replaceFirstChar { it.uppercase() }} " +
                    "${utcDate.dayOfMonth}, ${utcDate.year}"

        } catch (e: Exception) {
            // Fallback for non-ISO formats (assuming they're already UTC)
            try {
                val localDate = LocalDate.parse(dateString)
                "${localDate.month.name.lowercase().replaceFirstChar { it.uppercase() }} " +
                        "${localDate.dayOfMonth}, ${localDate.year}"
            } catch (e: Exception) {
                "Invalid date"
            }
        }
    }
}