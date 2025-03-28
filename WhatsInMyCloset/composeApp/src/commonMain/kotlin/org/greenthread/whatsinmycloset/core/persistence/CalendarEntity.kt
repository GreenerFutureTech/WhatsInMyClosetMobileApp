package org.greenthread.whatsinmycloset.core.persistence

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Entity(
    tableName = "calendar",
    foreignKeys = [
        ForeignKey(
            entity = OutfitEntity::class,
            parentColumns = ["outfitId"], // Primary key in OutfitEntity
            childColumns = ["outfitId"], // Foreign key in CalendarEntity
        )
    ],
    indices = [Index("outfitId")]
)
data class CalendarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val outfitId: String,
    val userId: Int,
    val date: String
)