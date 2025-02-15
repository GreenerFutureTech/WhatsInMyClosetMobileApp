package org.greenthread.whatsinmycloset.features.tabs.swap.Action

sealed interface SwapAction {
    data class OnSwapClick(val itemId: String): SwapAction
}