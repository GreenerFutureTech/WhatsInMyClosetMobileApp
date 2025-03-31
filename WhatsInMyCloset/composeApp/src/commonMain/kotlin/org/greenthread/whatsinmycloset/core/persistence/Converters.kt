package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.dto.UserDto

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

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