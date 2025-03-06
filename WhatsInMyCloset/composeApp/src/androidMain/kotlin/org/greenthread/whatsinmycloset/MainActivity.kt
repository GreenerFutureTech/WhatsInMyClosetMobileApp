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

        setContent {
            val cameraManager = CameraManager(this)
            //val wardrobeManager by inject<WardrobeManager>()
            //val wardrobeManager: WardrobeManager = get()
            App(cameraManager)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(null)
}