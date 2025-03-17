package org.greenthread.whatsinmycloset

import android.Manifest
import android.R.attr.bitmap
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import org.greenthread.whatsinmycloset.core.data.MyClosetDatabase
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.coroutines.resume


class AndroidPlatform : Platform {
    override val name: String = "Android"
}

actual fun getPlatform(): Platform = AndroidPlatform()

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

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    val bitmap = android.graphics.BitmapFactory.decodeByteArray(this, 0, this.size)
    return bitmap.asImageBitmap()
}

actual fun ByteArray.toBitmap(): Any {
    val bitmap = android.graphics.BitmapFactory.decodeByteArray(this, 0, this.size)
    return bitmap
}

fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    if (bitmap == null) {
        println("Caleb1 Error: Bitmap decoding failed! ByteArray might not be a valid image.")
    } else {
        println("Caleb1 Bitmap width: ${bitmap.width}, height: ${bitmap.height}")
    }
    return bitmap
}

actual fun subjectSegmentation(byteArray: ByteArray, onResult: (ImageBitmap?) -> Unit) {

    val bitmap = byteArrayToBitmap(byteArray)
    if (bitmap == null) {
        println("Failed to decode ByteArray into a Bitmap.")
        return
    }

    val image = InputImage.fromBitmap(bitmap, 0)
    println("byteArray size: ${byteArray.size}")

    val options = SubjectSegmenterOptions.Builder()
        .enableForegroundBitmap()
        .build()

    val segmenter = SubjectSegmentation.getClient(options)
    var foregroundBitmap: Bitmap? = null

    segmenter.process(image)
        .addOnSuccessListener { result ->
            foregroundBitmap = result.foregroundBitmap
            /* val segmentedByteArray = foregroundBitmap?.let { bmp ->
                val size = bmp.rowBytes * bmp.height
                val byteBuffer = ByteBuffer.allocate(size)
                bmp.copyPixelsToBuffer(byteBuffer)
                byteBuffer.array()
            } */
            onResult(foregroundBitmap?.asImageBitmap()) // Pass the result to the callback
        }
        .addOnFailureListener {
            onResult(null) // Pass null on failure
        }
}

actual fun bitmapToByteArray(bitmap: Any): ByteArray {
    val bitmapBMP = bitmap as Bitmap

    val stream = ByteArrayOutputStream()
    bitmapBMP.compress(Bitmap.CompressFormat.PNG, 100, stream) // Choose PNG, JPEG, or WEBP
    return stream.toByteArray()
}

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<MyClosetDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(MyClosetDatabase.DB_NAME)

        return Room.databaseBuilder(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}