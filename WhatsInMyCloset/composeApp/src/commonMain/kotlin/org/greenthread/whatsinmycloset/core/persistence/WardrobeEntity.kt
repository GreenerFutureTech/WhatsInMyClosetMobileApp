package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wardrobe")
data class WardrobeEntity(
    @PrimaryKey val id: String, // Matches backend UUID
    val wardrobeName: String,
    val createdAt: String,
    val lastUpdate: String,
    val userId: String // Foreign key reference to User
)