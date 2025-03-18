package org.greenthread.whatsinmycloset.core.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import org.greenthread.whatsinmycloset.core.data.daos.ClothingItemDao
import org.greenthread.whatsinmycloset.core.data.daos.WardrobeDao
import org.greenthread.whatsinmycloset.core.persistence.ClothingItemEntity
import org.greenthread.whatsinmycloset.core.persistence.Converters
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity

@Database(
    version = 1,
    entities = [
        WardrobeEntity::class,
        ClothingItemEntity::class
    ]
)
@TypeConverters(
    Converters::class
)
@ConstructedBy(ClosetDatabaseConstructor::class)
abstract class MyClosetDatabase : RoomDatabase() {
    abstract fun wardrobeDao(): WardrobeDao
    abstract fun itemDao(): ClothingItemDao

    companion object {
        const val DB_NAME = "my_closet.db"
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ClosetDatabaseConstructor: RoomDatabaseConstructor<MyClosetDatabase> {
    override fun initialize(): MyClosetDatabase
}

