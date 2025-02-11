package org.greenthread.whatsinmycloset.features.tabs.social

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.greenthread.whatsinmycloset.core.ui.components.controls.Greeting
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import whatsinmycloset.composeapp.generated.resources.Res
import whatsinmycloset.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun SocialTab(onNavigate: (String) -> Unit) {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) {
                Text("Social Tab test")
            }
            AnimatedVisibility(showContent) {

                val greeting = remember { Greeting().greet() }

                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("SOCIAL TAB!")
                }
            }
        }
    }
}