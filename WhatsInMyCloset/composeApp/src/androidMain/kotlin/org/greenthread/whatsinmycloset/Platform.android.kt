package org.greenthread.whatsinmycloset

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import android.Manifest

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

// androidMain/src/.../CameraManager.kt
actual class CameraManager(private val context: Context) {
    private var onPhotoTakenCallback: ((ByteArray) -> Unit)? = null

    @Composable
    actual fun TakePhotoButton(onPhotoTaken: (ByteArray) -> Unit) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as? android.graphics.Bitmap
                bitmap?.let {
                    val stream = ByteArrayOutputStream()
                    it.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, stream)
                    onPhotoTaken(stream.toByteArray())
                }
            }
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // Permission granted, launch the camera
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                launcher.launch(intent)
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        Button(onClick = {
            // Check if the camera permission is granted
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Permission already granted, launch the camera
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                launcher.launch(intent)
            } else {
                // Request the camera permission
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }) {
            Text("Take Photo")
        }
    }
}

