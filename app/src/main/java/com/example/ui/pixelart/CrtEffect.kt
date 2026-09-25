package com.example.ui.pixelart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun CrtOverlay(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    if (!isEnabled) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Horizontal scanlines
        val scanlineSpacing = 4f
        var y = 0f
        val scanlineColor = Color(0x2B000000)
        while (y < height) {
            drawLine(
                color = scanlineColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.5f
            )
            y += scanlineSpacing
        }

        // 2. Vintage Vignette / Curvature around edges
        val vignette = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                Color(0x10000000),
                Color(0x66000000),
                Color(0xCC05060A)
            ),
            center = Offset(width * 0.5f, height * 0.5f),
            radius = maxOf(width, height) * 0.72f
        )
        drawRect(brush = vignette, topLeft = Offset.Zero, size = Size(width, height))

        // 3. Subtle phosphor tint
        drawRect(
            color = Color(0x0500FF66), // very faint arcade phosphor green
            topLeft = Offset.Zero,
            size = Size(width, height)
        )
    }
}
