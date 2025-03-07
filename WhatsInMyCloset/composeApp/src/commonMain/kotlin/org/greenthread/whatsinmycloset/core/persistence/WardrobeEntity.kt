package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

@Entity(tableName = "wardrobe")
data class WardrobeEntity(
    @PrimaryKey val id: String, // Matches backend UUID
    val wardrobeName: String,
    val createdAt: String,
    val lastUpdate: String,
    val userId: String // Foreign key reference to User
)

fun Wardrobe.toWardrobeEntity(): WardrobeEntity {
    return WardrobeEntity(
        id = id,
        wardrobeName = wardrobeName,
        createdAt = createdAt,
        lastUpdate = lastUpdate,
        userId = userId,
    )
}

fun WardrobeEntity.toWardrobe(): Wardrobe {
    return Wardrobe(
        id = id,
        wardrobeName = wardrobeName,
        createdAt = createdAt,
        lastUpdate = lastUpdate,
        userId = userId,
    )
}