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

        NotificationService.isAppInForeground = true

        setContent {
            val cameraManager = CameraManager(this)
            App(cameraManager, photoManager)
        }
    }

    override fun onResume() {
        super.onResume()
        NotificationService.isAppInForeground = true
    }

    override fun onPause() {
        super.onPause()
        NotificationService.isAppInForeground = false
    }

    override fun onDestroy() {
        super.onDestroy()
        NotificationService.isAppInForeground = false
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(null, null)
}