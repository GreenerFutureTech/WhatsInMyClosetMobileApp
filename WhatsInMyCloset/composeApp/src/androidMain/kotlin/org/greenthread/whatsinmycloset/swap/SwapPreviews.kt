package org.greenthread.whatsinmycloset.swap

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.greenthread.whatsinmycloset.features.tabs.swap.SwapItemScreen
import org.greenthread.whatsinmycloset.features.tabs.swap.SwapScreenRoot

// Preview Composable
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewSwapItem() {
    SwapItemScreen()
}

// Preview Composable
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewSwapScreen() {
    SwapScreenRoot()
}