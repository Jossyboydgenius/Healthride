package com.example.healthride.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.healthride.ui.theme.*

/**
 * Creates a beautiful gradient background with animated subtle patterns.
 * This is a more reliable alternative to the original Blob component.
 */
@Composable
fun ModernGradientBackground(
    modifier: Modifier = Modifier,
    primaryColor: Color = PrimaryBlue,
    secondaryColor: Color = PrimaryPurple,
    content: @Composable BoxScope.() -> Unit
) {
    // Define animations
    val infiniteTransition = rememberInfiniteTransition(label = "background-animation")

    // Animation for the first gradient shift
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient-shift"
    )

    // Animation for the subtle pattern opacity
    val patternOpacity by infiniteTransition.animateFloat(
        initialValue = 0.04f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pattern-opacity"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Base gradient background
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Create a beautiful gradient that works consistently
            val gradientBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFF2F6FF),
                    Color(0xFFF7F9FF),
                    Color(0xFFF8FBFF)
                ),
                start = Offset(size.width * gradientShift, 0f),
                end = Offset(0f, size.height)
            )
            drawRect(brush = gradientBrush)

            // Add subtle pattern overlay
            val patternBrush = Brush.radialGradient(
                colors = listOf(
                    Color(0x10000000),
                    Color(0x00000000)
                ),
                center = Offset(size.width * 0.2f, size.height * 0.3f),
                radius = size.width * 0.8f
            )
            drawRect(brush = patternBrush, alpha = patternOpacity)

            // Second subtle pattern for depth
            val patternBrush2 = Brush.radialGradient(
                colors = listOf(
                    Color(0x08000000),
                    Color(0x00000000)
                ),
                center = Offset(size.width * 0.8f, size.height * 0.7f),
                radius = size.width * 0.6f
            )
            drawRect(brush = patternBrush2, alpha = patternOpacity)
        }

        // Add animated gradient orbs/blobs
        GradientOrb(
            color = primaryColor,
            size = 300.dp,
            xOffset = (-100).dp,
            yOffset = (-50).dp,
            alpha = 0.1f
        )

        GradientOrb(
            color = secondaryColor,
            size = 250.dp,
            xOffset = 250.dp,
            yOffset = 400.dp,
            alpha = 0.1f
        )

        // Add a smaller accent orb
        GradientOrb(
            color = AccentTeal,
            size = 180.dp,
            xOffset = 50.dp,
            yOffset = 600.dp,
            alpha = 0.06f
        )

        // Container for content
        content()
    }
}

/**
 * A modern, animated gradient orb that reliably renders on all devices.
 * This replaces the original Blob component with something more visually appealing.
 */
@Composable
fun GradientOrb(
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    xOffset: androidx.compose.ui.unit.Dp,
    yOffset: androidx.compose.ui.unit.Dp,
    alpha: Float = 0.1f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb-animation")

    // Scale animation
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb-scale"
    )

    // Position animation - slight floating effect
    val moveX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb-moveX"
    )

    val moveY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb-moveY"
    )

    Box(
        modifier = Modifier
            .offset(x = xOffset + moveX.dp, y = yOffset + moveY.dp)
            .size(size)
            .scale(scale)
            .alpha(alpha)
            .blur(radius = 30.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.7f),
                        color.copy(alpha = 0.0f)
                    )
                ),
                shape = CircleShape
            )
    )
}

/**
 * Creates a professional gradient card background.
 * Use this for important cards that need to stand out.
 */
@Composable
fun PremiumCardBackground(
    modifier: Modifier = Modifier,
    startColor: Color = PrimaryBlue,
    endColor: Color = PrimaryPurple,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "card-animation")

    // Subtle rotation animation for gradient
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient-rotation"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                this.rotationZ = rotation
            }
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        startColor,
                        endColor
                    )
                )
            )
            .graphicsLayer {
                this.rotationZ = -rotation
            }
    ) {
        content()
    }
}

/**
 * A decorative wavy pattern that can be used as a divider or accent.
 */
@Composable
fun WavyDivider(
    modifier: Modifier = Modifier,
    startColor: Color = PrimaryBlue.copy(alpha = 0.5f),
    endColor: Color = PrimaryPurple.copy(alpha = 0.5f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave-animation")

    val waveShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave-shift"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val waveBrush = Brush.linearGradient(
            colors = listOf(startColor, endColor),
            start = Offset(width * waveShift, 0f),
            end = Offset(width * (1f + waveShift), height)
        )

        // Draw a wave pattern
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, height / 2)

            // Create wave pattern
            for (i in 0..width.toInt() step 40) {
                val x = i.toFloat()
                val y = height / 2 + kotlin.math.sin((x / width + waveShift) * 2 * Math.PI) * (height / 4).toFloat()
                lineTo(x, y.toFloat())
            }

            lineTo(width, height / 2)
        }

        drawPath(
            path = path,
            brush = waveBrush,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
        )
    }
}