package org.greenthread.whatsinmycloset.core.ui.components.controls

import org.greenthread.whatsinmycloset.getPlatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}