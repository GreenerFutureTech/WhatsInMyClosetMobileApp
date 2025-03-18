package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.split(",") ?: emptyList()
    }

    @TypeConverter
    fun listToString(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Json.decodeFromString(value)
    }
}