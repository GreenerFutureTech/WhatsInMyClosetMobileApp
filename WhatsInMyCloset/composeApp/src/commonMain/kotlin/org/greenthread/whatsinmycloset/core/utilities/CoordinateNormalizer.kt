package org.greenthread.whatsinmycloset.core.utilities

import org.greenthread.whatsinmycloset.core.domain.models.OffsetData

object CoordinateNormalizer {
    // Standard canvas dimensions for coordinate normalization
    const val STANDARD_CANVAS_WIDTH = 450f
    const val STANDARD_CANVAS_HEIGHT = 300f

    const val STANDARD_ITEM_SIZE = 45f

    /**
     * Normalizes coordinates from the current canvas to the standard canvas
     *
     * @param x Current x coordinate
     * @param y Current y coordinate
     * @param currentCanvasWidth Width of the current canvas
     * @param currentCanvasHeight Height of the current canvas
     * @return Pair of normalized x and y coordinates
     */
    fun normalizeCoordinates(
        x: Float,
        y: Float,
        currentCanvasWidth: Float,
        currentCanvasHeight: Float
    ): Pair<Float, Float> {
        // Scale coordinates proportionally to the standard canvas
        val normalizedX = (x / currentCanvasWidth) * STANDARD_CANVAS_WIDTH
        val normalizedY = (y / currentCanvasHeight) * STANDARD_CANVAS_HEIGHT

        return normalizedX to normalizedY
    }

    /**
     * Denormalizes coordinates from the standard canvas to the current canvas
     *
     * @param normalizedX Normalized x coordinate
     * @param normalizedY Normalized y coordinate
     * @param currentCanvasWidth Width of the current canvas
     * @param currentCanvasHeight Height of the current canvas
     * @param itemWidth Width of the item (default 45f)
     * @param itemHeight Height of the item (default 45f)
     * @return Pair of denormalized x and y coordinates
     */
    fun denormalizeCoordinates(
        normalizedX: Float,
        normalizedY: Float,
        currentCanvasWidth: Float,
        currentCanvasHeight: Float,
        itemWidth: Float = 45f,
        itemHeight: Float = 45f
    ): Pair<Float, Float> {
        // Calculate the scaling factor
        val scaleX = currentCanvasWidth / STANDARD_CANVAS_WIDTH
        val scaleY = currentCanvasHeight / STANDARD_CANVAS_HEIGHT

        // Scale coordinates back to the current canvas
        // Add a small offset to adjust left alignment
        val x = (normalizedX * scaleX) - (itemWidth * 0.1f)
        val y = (normalizedY * scaleY)

        return x to y
    }

    /**
     * Calculate dynamic item size based on canvas dimensions
     *
     * @param currentCanvasWidth Width of the current canvas
     * @param currentCanvasHeight Height of the current canvas
     * @return Pair of width and height for the dynamic item size
     */
    fun calculateDynamicItemSize(
        currentCanvasWidth: Float,
        currentCanvasHeight: Float
    ): Pair<Float, Float> {
        // Calculate scaling factor
        val scaleX = currentCanvasWidth / STANDARD_CANVAS_WIDTH
        val scaleY = currentCanvasHeight / STANDARD_CANVAS_HEIGHT

        // Calculate dynamic item size
        val dynamicItemWidth = STANDARD_ITEM_SIZE * scaleX
        val dynamicItemHeight = STANDARD_ITEM_SIZE * scaleY

        return dynamicItemWidth to dynamicItemHeight
    }
}