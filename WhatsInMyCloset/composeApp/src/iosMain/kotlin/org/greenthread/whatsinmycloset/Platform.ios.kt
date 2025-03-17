package org.greenthread.whatsinmycloset

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.Room
import androidx.room.RoomDatabase
import coil3.BitmapImage
import org.greenthread.whatsinmycloset.core.data.MyClosetDatabase
import platform.UIKit.UIDevice
import platform.UIKit.*

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

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    //val nsData = this.toNSData()
    //val uiImage = UIImage(data = nsData)
    return ImageBitmap(10,10)
}

actual fun ByteArray.toBitmap(): Any {
    //val nsData = this.toNSData()
    //val uiImage = UIImage(data = nsData)
    return ImageBitmap(10,10)
}

actual fun bitmapToByteArray(bitmap: Any?): ByteArray {
/*    val bitmapBMP = bitmap as Bitmap

    val stream = ByteArrayOutputStream()
    bitmapBMP.compress(Bitmap.CompressFormat.PNG, 100, stream) // Choose PNG, JPEG, or WEBP
    return stream.toByteArray()*/
    return ByteArray(0)
}

actual fun subjectSegmentation(byteArray: ByteArray, onResult: (ByteArray?) -> Unit) {
    return
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