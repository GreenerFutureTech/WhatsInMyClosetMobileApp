package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData

class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.split(",") ?: emptyList()
    }

    @TypeConverter
    fun listToString(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }

    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromOffsetData(offsetData: OffsetData?): String? {
        return offsetData?.let { json.encodeToString(OffsetData.serializer(), it) }
    }

    @TypeConverter
    fun toOffsetData(jsonString: String?): OffsetData? {
        return jsonString?.let { json.decodeFromString(OffsetData.serializer(), it) }
    }
}