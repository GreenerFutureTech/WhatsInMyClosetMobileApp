package org.greenthread.whatsinmycloset

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.greenthread.whatsinmycloset.core.data.MyClosetDatabase

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect class CameraManager {
    @Composable
    fun TakePhotoButton(onPhotoTaken: (ByteArray) -> Unit)
}

expect class NotificationManager {
    fun requestPermissions()
    fun initialize()
}

expect suspend fun getFCMToken(): String?

expect fun ByteArray.toImageBitmap(): ImageBitmap

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<MyClosetDatabase> {
    override fun initialize(): MyClosetDatabase
}

expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<MyClosetDatabase>
}