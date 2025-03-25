package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
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
    @PrimaryKey val outfitId: String,   // Matches backend UUID
    val name: String = "",              // Name of the outfit (e.g., "Summer Look")
    val creatorId: Int,                 // Matches with User ID

    // Direct list of items with their positions
    @Embedded(prefix = "item_")
    val items: String,                  // Serialized JSON for List<OutfitItem>

    val tags: String,                   // Serialized JSON for List<String>
    val calendarDates: String,          // Serialized JSON for List<String>
    val createdAt: String = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()).toString()
) {

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun create(
            outfitId: String,
            name: String = "",
            creatorId: Int,
            items: List<OutfitItem>,
            tags: List<String>,
            calendarDates: List<String>
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

    // Helper functions to deserialize
    // for example:
    /*
        val items = outfit.getItems() // Returns List<OutfitItem>
        val tags = outfit.getTags()   // Returns List<String>
     */
    fun getItems(): List<OutfitItem> = json.decodeFromString(items)
    fun getTags(): List<String> = json.decodeFromString(tags)
    fun getCalendarDates(): List<String> = json.decodeFromString(calendarDates)
}

@Serializable
@Embeddable
data class OutfitItem(
    val id: String,  // The item ID
    val x: Float,    // X position
    val y: Float     // Y position
)

annotation class Embeddable