package org.greenthread.whatsinmycloset

import android.content.res.Resources
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Actual function to get screen width on an Android phone
actual fun getScreenWidthDp(): Dp {
    val metrics = Resources.getSystem().displayMetrics
    return (metrics.widthPixels / metrics.density).dp
}