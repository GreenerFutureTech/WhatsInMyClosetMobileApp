package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = WardrobeEntity::class,
            parentColumns = ["id"],
            childColumns = ["wardrobeId"],
            onDelete = ForeignKey.CASCADE // Ensures items are deleted if the wardrobe is deleted
        )
    ],
    indices = [Index("wardrobeId")]
)
data class ItemEntity(
    @PrimaryKey val id: String, // Matches backend UUID
    val wardrobeId: String, // Foreign key
    val itemType: String,
    val mediaUrl: String,
    val tags: List<String>, // Converts Room 'simple-array' to List<String>
    val createdAt: String
)