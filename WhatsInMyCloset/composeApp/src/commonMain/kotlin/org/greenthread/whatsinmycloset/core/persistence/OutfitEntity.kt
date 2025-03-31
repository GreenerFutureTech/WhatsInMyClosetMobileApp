package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.greenthread.whatsinmycloset.core.dto.OutfitDto

@Entity(
    tableName = "outfits",
    indices = [Index("id")]
)
@Serializable
data class OutfitEntity(
    @PrimaryKey val id: String,
    val name: String = "",
    val itemIds: String, // Serialized List<OutfitItems>
    val userId: Int,
    val tags: String = "[]", // Serialized List<String>
    val createdAt: String? = null,
    val creator: String? = null // Serialized CreatorDto?
) {
    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDto(dto: OutfitDto): OutfitEntity {
            return OutfitEntity(
                id = dto.id,
                name = dto.name,
                itemIds = json.encodeToString(dto.itemIds),
                userId = dto.userId,
                tags = json.encodeToString(dto.tags),
                createdAt = dto.createdAt,
                creator = dto.creator?.let { json.encodeToString(it) }
            )
        }
    }

    fun toDto(): OutfitDto {
        return OutfitDto(
            id = id,
            name = name,
            itemIds = json.decodeFromString(itemIds),
            userId = userId,
            tags = json.decodeFromString(tags),
            createdAt = createdAt,
            creator = creator?.let { json.decodeFromString(it) }
        )
    }
}

@Serializable
@Embeddable
data class OutfitItems(
    val id: String? = "",
    val x: Float? = 0f,
    val y: Float? = 0f
)

annotation class Embeddable