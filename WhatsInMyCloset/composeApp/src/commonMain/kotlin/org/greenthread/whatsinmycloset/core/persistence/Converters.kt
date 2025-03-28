package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData
import kotlinx.serialization.serializer
import org.greenthread.whatsinmycloset.core.dto.UserDto

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun outfitItemsToString(items: List<OutfitItems>): String =
        json.encodeToString(items)

    @TypeConverter
    fun stringToOutfitItems(jsonString: String): List<OutfitItems> =
        json.decodeFromString(jsonString)

    @TypeConverter
    fun offsetMapToString(map: Map<String, OffsetData>): String =
        json.encodeToString(map)

    @TypeConverter
    fun stringToOffsetMap(jsonString: String): Map<String, OffsetData> =
        json.decodeFromString(jsonString)

    @TypeConverter
    fun fromUserDto(userDto: UserDto): String =
        json.encodeToString(userDto)

    @TypeConverter
    fun toUserDto(jsonString: String): UserDto =
        json.decodeFromString(jsonString)

    // Only keep one pair of converters for List<String>
    @TypeConverter
    fun stringListToString(list: List<String>): String =
        json.encodeToString(list)

    @TypeConverter
    fun stringToStringList(jsonString: String): List<String> =
        json.decodeFromString(jsonString)
}