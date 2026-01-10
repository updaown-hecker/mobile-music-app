package com.updaown.musicapp.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.updaown.musicapp.ui.theme.AppleSystemBlue

// A reused component for the "Liquid" background mesh
@Composable
fun LiquidBackground(
    dominantColor: Color,
    modifier: Modifier = Modifier
) {
    // Animate gradient positions for a "flowing" effect
    val infiniteTransition = rememberInfiniteTransition(label = "liquid")
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "offset1"
    )
    
    val offset2 by infiniteTransition.animateFloat(
        initialValue = 1000f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "offset2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black) // Deep base
    ) {
        // We layer multiple gradients
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(dominantColor.copy(alpha = 0.6f), Color.Transparent),
                        center = Offset(x = offset1 % 2000, y = offset2 % 2000),
                        radius = 1500f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(AppleSystemBlue.copy(alpha = 0.3f), Color.Transparent),
                        center = Offset(x = offset2 % 1500, y = offset1 % 1500),
                        radius = 1200f
                    )
                )
        )
         Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f)) // Overlay to darken
        )
    }
}
