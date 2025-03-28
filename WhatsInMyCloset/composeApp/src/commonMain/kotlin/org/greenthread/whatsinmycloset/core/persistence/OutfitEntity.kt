package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.greenthread.whatsinmycloset.core.data.daos.ClothingItemDao
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.domain.models.Outfit
import org.greenthread.whatsinmycloset.core.domain.models.OffsetData

/* Represents the persistence model for outfits in the Room database.
    Maps between the domain model (Outfit) and the database entity.
*/

@Entity(
    tableName = "outfits",
    indices = [Index("outfitId")]
)
data class OutfitEntity(
    @PrimaryKey val outfitId: String,
    val name: String = "",
    val creatorId: Int,
    val items: String, // JSON string of Map<String, OffsetData>
    val tags: String = "[]", // Default empty list JSON
    val calendarDates: String = "[]", // Default empty list JSON
    val createdAt: String = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()).toString()
) {

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun create(
            outfitId: String,
            name: String = "",
            creatorId: Int,
            items: Map<String, OffsetData>,
            tags: List<String> = emptyList(),
            calendarDates: List<String> = emptyList()
        ): OutfitEntity {
            return OutfitEntity(
                outfitId = outfitId,
                name = name,
                creatorId = creatorId,
                items = json.encodeToString(items),
                tags = json.encodeToString(tags),
                calendarDates = json.encodeToString(calendarDates)
            )
        }
    }

    fun getItems(): Map<String, OffsetData> =
        json.decodeFromString(items) ?: emptyMap()

    fun getTags(): List<String> =
        json.decodeFromString(tags) ?: emptyList()

    fun getCalendarDates(): List<String> =
        json.decodeFromString(calendarDates) ?: emptyList()
}

@Serializable
@Embeddable
data class OutfitItems(
    val id: String? = "",  // The item ID
    val x: Float? = 0f,    // X position
    val y: Float? = 0f     // Y position
)

annotation class Embeddable