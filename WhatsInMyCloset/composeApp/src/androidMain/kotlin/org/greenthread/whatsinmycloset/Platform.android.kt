package org.greenthread.whatsinmycloset

import android.Manifest
import android.R.attr.bitmap
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.greenthread.whatsinmycloset.core.data.MyClosetDatabase
import org.jetbrains.compose.resources.stringResource
import whatsinmycloset.composeapp.generated.resources.Res
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import whatsinmycloset.composeapp.generated.resources.profile_button
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

actual class PhotoManager(private val context: Context) {

    @Composable
    actual fun SelectPhotoButton(onPhotoSelected: (ByteArray) -> Unit) {
        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                bitmap?.let {
                    val stream = ByteArrayOutputStream()
                    it.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    onPhotoSelected(stream.toByteArray())
                }
            }
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                galleryLauncher.launch("image/*")
            } else {
                Toast.makeText(context, "Need permission to access photos", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        TextButton(
            onClick = {
                val permission =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }

                if (ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    galleryLauncher.launch("image/*")
                } else {
                    permissionLauncher.launch(permission)
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.profile_button)
            )
        }
    }
}

actual class NotificationManager(private val context: Context) {
    // Store the permission launcher at the class level
    private var permissionLauncher: ActivityResultLauncher<String>? = null

    fun registerPermissionLauncher(activity: ComponentActivity) {
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(context, "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Notifications will not be shown", Toast.LENGTH_SHORT).show()
            }
        }
    }

    actual fun requestPermissions() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                // Use the stored permission launcher to request permissions
                permissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    actual fun initialize() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("Fetching FCM registration token failed")
                return@OnCompleteListener
            }
            val token = task.result
            println("FCM TOKEN: $token")
        })
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

actual fun subjectSegmentation(byteArray: ByteArray, onResult: (ByteArray?) -> Unit) {

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
             val segmentedByteArray = foregroundBitmap?.let { bmp ->
                val size = bmp.rowBytes * bmp.height
                val byteBuffer = ByteBuffer.allocate(size)
                bmp.copyPixelsToBuffer(byteBuffer)
                byteBuffer.array()
            }
            onResult(bitmapToByteArray(foregroundBitmap)) // Pass the result to the callback
        }
        .addOnFailureListener {
            onResult(null) // Pass null on failure
        }
}

actual fun bitmapToByteArray(bitmap: Any?): ByteArray {
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

actual suspend fun getFCMToken(): String? = suspendCoroutine { continuation ->
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val token = task.result
            println("FCM Token: $token")
            continuation.resume(token)
        } else {
            continuation.resume(null)
        }
    }
}