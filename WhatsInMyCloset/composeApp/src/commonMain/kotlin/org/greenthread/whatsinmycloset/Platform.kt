package org.greenthread.whatsinmycloset

import androidx.compose.runtime.Composable

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect class CameraManager {
    @Composable
    fun TakePhotoButton(onPhotoTaken: (ByteArray) -> Unit)
}