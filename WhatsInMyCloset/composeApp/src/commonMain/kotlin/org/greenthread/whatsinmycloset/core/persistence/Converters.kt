package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun outfitItemsToString(items: List<OutfitItem>): String =
        json.encodeToString(items)

    @TypeConverter
    fun stringToOutfitItems(jsonString: String): List<OutfitItem> =
        json.decodeFromString(jsonString)

    @TypeConverter
    fun stringListToString(list: List<String>): String =
        json.encodeToString(list)

    @TypeConverter
    fun stringToStringList(jsonString: String): List<String> =
        json.decodeFromString(jsonString)

    @TypeConverter
    fun offsetMapToString(map: Map<String, OffsetData>): String =
        json.encodeToString(map)

    @TypeConverter
    fun stringToOffsetMap(jsonString: String): Map<String, OffsetData> =
        json.decodeFromString(jsonString)
}