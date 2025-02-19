package org.greenthread.whatsinmycloset

import androidx.compose.ui.window.ComposeUIViewController
import org.greenthread.whatsinmycloset.app.App
import org.greenthread.whatsinmycloset.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }