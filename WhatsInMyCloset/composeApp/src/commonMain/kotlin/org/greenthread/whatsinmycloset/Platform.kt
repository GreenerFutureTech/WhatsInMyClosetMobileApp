package org.greenthread.whatsinmycloset

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect class CameraManager {
    @Composable
    fun TakePhotoButton(onPhotoTaken: (ByteArray) -> Unit)
}

expect fun ByteArray.toImageBitmap(): ImageBitmap