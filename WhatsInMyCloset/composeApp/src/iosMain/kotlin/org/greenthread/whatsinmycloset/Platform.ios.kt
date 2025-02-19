package org.greenthread.whatsinmycloset

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual class CameraManager {
    @Composable
    actual fun TakePhotoButton(onPhotoTaken: (ByteArray) -> Unit) {
        Button(onClick = {
            //idk if this compiles

            // Use Swift/Objective-C interop to open the camera and capture the photo
            // For now, we'll simulate a photo capture with a placeholder byte array
            val placeholderImageBytes = ByteArray(0) // Replace with actual image data
            onPhotoTaken(placeholderImageBytes)
        }) {
            Text("Take Photo")
        }
    }
}