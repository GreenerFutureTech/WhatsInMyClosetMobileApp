package org.greenthread.whatsinmycloset

import android.Manifest
import android.R.attr.bitmap
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.graphics.Matrix
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
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
import java.io.File
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import whatsinmycloset.composeapp.generated.resources.profile_button
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.MutableState


class AndroidPlatform : Platform {
    override val name: String = "Android"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun BackHandler(enabled: Boolean, onBackPressed: () -> Unit) {
    BackHandler(enabled, onBackPressed)
}

actual class CameraManager(private val context: Context) {
    private var onPhotoTakenCallback: ((ByteArray) -> Unit)? = null

    fun getRotatedBitmap(imagePath: String): Bitmap {

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeFile(imagePath, options)

        options.inSampleSize = calculateInSampleSize(options, 1024, 1024)
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565

        val bitmap = BitmapFactory.decodeFile(imagePath, options)

        val exif = ExifInterface(imagePath)

        val rotation = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        return if (rotation != 0) {
            val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height, width) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    @Composable
    actual fun TakePhotoButton(buttonText: MutableState<String>, onPhotoTaken: (ByteArray) -> Unit) {
        val context = LocalContext.current
        val cacheDir = context.cacheDir
        var photoFile: File? = null

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && photoFile != null) {
                val bitmap = getRotatedBitmap(photoFile!!.absolutePath) // Fix rotation
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                onPhotoTaken(stream.toByteArray())

                buttonText.value = "Replace Photo" // <-- Change text after photo is taken
            }
        }

        fun takePicture() {
            photoFile = File.createTempFile("photo_", ".jpg", cacheDir)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile!!)

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, uri) // Store full-resolution image
            }
            launcher.launch(intent)
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                takePicture()
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        Button(onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePicture()
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }) {
            Text(buttonText.value)
        }
    }
}

actual class PhotoManager(private val context: Context) {

    @Composable
    actual fun SelectPhotoButton(onPhotoSelected: (ByteArray) -> Unit) {
        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    bitmap?.let { bmp ->
                        val stream = ByteArrayOutputStream()
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        onPhotoSelected(stream.toByteArray())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                galleryLauncher.launch("image/*")
            } else {
                Toast.makeText(context, "Need permission to access photos", Toast.LENGTH_SHORT).show()
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

                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    galleryLauncher.launch("image/*")
                } else {
                    permissionLauncher.launch(permission)
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = stringResource(Res.string.profile_button))
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
    // First decode with options to reduce memory usage
    val options = BitmapFactory.Options().apply {
        inSampleSize = 4 // Reduce image dimensions by factor of 4
        inPreferredConfig = Bitmap.Config.RGB_565 // Use less memory per pixel
    }

    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
    if (bitmap == null) {
        println("Failed to decode ByteArray into a Bitmap.")
        onResult(null)
        return
    }

    // Further resize if needed
    val maxDimension = 1024 // Keep maximum dimension at 1024px
    val scale = maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height)
    if (scale < 1.0f) {
        val scaledWidth = (bitmap.width * scale).toInt()
        val scaledHeight = (bitmap.height * scale).toInt()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
        bitmap.recycle() // Free the original bitmap memory
        processSegmentation(scaledBitmap, onResult)
    } else {
        processSegmentation(bitmap, onResult)
    }
}

private fun processSegmentation(bitmap: Bitmap, onResult: (ByteArray?) -> Unit) {
    val image = InputImage.fromBitmap(bitmap, 0)
    println("Processing bitmap with dimensions: ${bitmap.width}x${bitmap.height}")

    val options = SubjectSegmenterOptions.Builder()
        .enableForegroundBitmap()
        .build()

    val segmenter = SubjectSegmentation.getClient(options)

    segmenter.process(image)
        .addOnSuccessListener { result ->
            val foregroundBitmap = result.foregroundBitmap
            val segmentedByteArray = bitmapToByteArray(foregroundBitmap)
            foregroundBitmap?.recycle() // Free memory
            onResult(segmentedByteArray)
        }
        .addOnFailureListener {
            bitmap.recycle() // Free memory on failure
            onResult(null)
        }
}

fun ByteArray.toImageBitmap(maxDimension: Int = -1): ImageBitmap {
    val options = BitmapFactory.Options().apply {
        inSampleSize = if (maxDimension > 0) 2 else 1
        inPreferredConfig = Bitmap.Config.RGB_565
    }

    var bitmap = BitmapFactory.decodeByteArray(this, 0, size, options)

    // Further downscale if needed
    if (maxDimension > 0) {
        val scale = maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height)
        if (scale < 1.0f) {
            val scaledBitmap = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
            bitmap.recycle()
            bitmap = scaledBitmap
        }
    }

    return bitmap.asImageBitmap()
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