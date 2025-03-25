package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import org.greenthread.whatsinmycloset.core.dto.UserDto
import org.greenthread.whatsinmycloset.core.ui.components.models.Wardrobe

@Entity(tableName = "wardrobe")
@Serializable
data class WardrobeEntity(
    @PrimaryKey val id: String, // Matches backend UUID
    val wardrobeName: String,
    val createdAt: String,
    val lastUpdate: String,
    val user: UserDto // Foreign key reference to User
)

fun Wardrobe.toWardrobeEntity(): WardrobeEntity {
    return WardrobeEntity(
        id = id,
        wardrobeName = wardrobeName,
        createdAt = createdAt,
        lastUpdate = lastUpdate,
        user = user.toUserDto(),
    )
}

fun WardrobeEntity.toWardrobe(): Wardrobe {
    return Wardrobe(
        id = id,
        wardrobeName = wardrobeName,
        createdAt = createdAt,
        lastUpdate = lastUpdate,
        user = user.toModel()
    )
}