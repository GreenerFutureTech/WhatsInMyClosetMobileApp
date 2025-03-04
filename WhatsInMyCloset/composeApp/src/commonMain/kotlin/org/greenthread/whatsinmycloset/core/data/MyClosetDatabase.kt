package org.greenthread.whatsinmycloset.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.greenthread.whatsinmycloset.core.data.daos.ItemDao
import org.greenthread.whatsinmycloset.core.data.daos.WardrobeDao
import org.greenthread.whatsinmycloset.core.persistence.Converters
import org.greenthread.whatsinmycloset.core.persistence.ItemEntity
import org.greenthread.whatsinmycloset.core.persistence.WardrobeEntity

@Database(entities = [WardrobeEntity::class, ItemEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class MyClosetDatabase : RoomDatabase() {
    abstract fun wardrobeDao(): WardrobeDao
    abstract fun itemDao(): ItemDao
}