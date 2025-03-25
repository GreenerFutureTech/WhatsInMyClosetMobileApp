package org.greenthread.whatsinmycloset


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.greenthread.whatsinmycloset.app.App

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationManager = NotificationManager(this)
        notificationManager.registerPermissionLauncher(this)
        notificationManager.requestPermissions()
        notificationManager.initialize()
        val photoManager = PhotoManager(this)

        setContent {
            val cameraManager = CameraManager(this)
            App(cameraManager, notificationManager, photoManager)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(null, null, null)
}