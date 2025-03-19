package org.greenthread.whatsinmycloset

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.Room
import androidx.room.RoomDatabase
import org.greenthread.whatsinmycloset.core.data.MyClosetDatabase
import platform.UIKit.UIDevice
import platform.UIKit.*
import platform.FirebaseMessaging.FIRMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IOSPlatform: Platform {
    override val name: String = "iOS"
}

actual fun getPlatform(): Platform = IOSPlatform()

actual class CameraManager {
    @Composable
    actual fun TakePhotoButton(onPhotoTaken: (ByteArray) -> Unit) {
        Button(onClick = {

            // Use Swift/Objective-C interop to open the camera and capture the photo
            // For now, we'll simulate a photo capture with a placeholder byte array
            val placeholderImageBytes = ByteArray(0) // Replace with actual image data
            onPhotoTaken(placeholderImageBytes)
        }) {
            Text("Take Photo")
        }
    }
}

actual class NotificationManager {
    actual fun requestPermissions() {
        // iOS implementation for requesting notification permissions
        // This would use UNUserNotificationCenter to request authorization
    }

    actual fun initialize() {
        // iOS implementation for initializing notifications
        // This would register for remote notifications and handle tokens
    }
}

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    //val nsData = this.toNSData()
    //val uiImage = UIImage(data = nsData)
    return ImageBitmap(10,10)
}



actual suspend fun getFCMToken(): String? = suspendCoroutine { continuation ->
    FIRMessaging.messaging().tokenWithCompletion { token, error ->
        if (error == null) {
            continuation.resume(token)
        } else {
            continuation.resume(null)
        }
    }
}

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<MyClosetDatabase> {
        val dbFile = documentDirectory() + "/${MyClosetDatabase.DB_NAME}"
        return Room.databaseBuilder<MyClosetDatabase>(
            name = dbFile
        )
    }

    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        return requireNotNull(documentDirectory?.path)
    }
}