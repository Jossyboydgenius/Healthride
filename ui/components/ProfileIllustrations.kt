package com.example.healthride.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.healthride.ui.theme.*

@Composable
fun ProfileAvatar(
    initial: String,
    modifier: Modifier = Modifier,
    primaryColor: Color = PrimaryPurple,
    secondaryColor: Color = PrimaryBlue
) {
    Canvas(modifier = modifier.size(120.dp)) {
        // Background circle
        drawCircle(
            color = Color.White,
            radius = size.minDimension / 2,
            center = Offset(size.width / 2, size.height / 2)
        )

        // Gradient circle inside
        drawCircle(
            color = primaryColor.copy(alpha = 0.8f),
            radius = size.minDimension / 2.2f,
            center = Offset(size.width / 2, size.height / 2)
        )

        // Draw decorative arcs
        val path = Path().apply {
            moveTo(size.width * 0.3f, size.height * 0.2f)
            cubicTo(
                size.width * 0.4f, size.height * 0.1f,
                size.width * 0.6f, size.height * 0.1f,
                size.width * 0.7f, size.height * 0.2f
            )
        }

        drawPath(
            path = path,
            color = secondaryColor.copy(alpha = 0.6f),
            style = Stroke(width = 5f)
        )

        // Initial text would need to be drawn separately with drawText
        // For simplicity, this would be handled by combining this canvas
        // with a Text composable in the parent
    }
}

@Composable
fun InsuranceIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(80.dp)) {
        // Simplified illustration of an insurance card
        drawRoundRect(
            color = PrimaryBlueLight,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f),
            size = size
        )

        // Card stripe
        drawRect(
            color = PrimaryBlue.copy(alpha = 0.7f),
            topLeft = Offset(0f, size.height * 0.2f),
            size = size.copy(height = size.height * 0.2f)
        )

        // Card chip
        drawCircle(
            color = PrimaryPurple.copy(alpha = 0.8f),
            radius = size.minDimension * 0.1f,
            center = Offset(size.width * 0.25f, size.height * 0.6f)
        )

        // Card details lines
        drawLine(
            color = TextMediumGray.copy(alpha = 0.5f),
            start = Offset(size.width * 0.5f, size.height * 0.6f),
            end = Offset(size.width * 0.9f, size.height * 0.6f),
            strokeWidth = 5f
        )

        drawLine(
            color = TextMediumGray.copy(alpha = 0.5f),
            start = Offset(size.width * 0.5f, size.height * 0.7f),
            end = Offset(size.width * 0.8f, size.height * 0.7f),
            strokeWidth = 5f
        )
    }
}

@Composable
fun SecurityIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(80.dp)) {
        // Shield outline
        val path = Path().apply {
            moveTo(size.width / 2, 0f)
            lineTo(size.width, size.height * 0.3f)
            lineTo(size.width, size.height * 0.7f)
            cubicTo(
                size.width, size.height * 0.9f,
                size.width * 0.5f, size.height,
                size.width * 0.5f, size.height
            )
            cubicTo(
                size.width * 0.5f, size.height,
                0f, size.height * 0.9f,
                0f, size.height * 0.7f
            )
            lineTo(0f, size.height * 0.3f)
            close()
        }

        drawPath(
            path = path,
            color = PrimaryPurple.copy(alpha = 0.2f)
        )

        // Shield inner
        val innerPath = Path().apply {
            val padding = size.width * 0.1f
            moveTo(size.width / 2, padding)
            lineTo(size.width - padding, size.height * 0.3f)
            lineTo(size.width - padding, size.height * 0.7f)
            cubicTo(
                size.width - padding, size.height * 0.85f,
                size.width * 0.5f, size.height - padding,
                size.width * 0.5f, size.height - padding
            )
            cubicTo(
                size.width * 0.5f, size.height - padding,
                padding, size.height * 0.85f,
                padding, size.height * 0.7f
            )
            lineTo(padding, size.height * 0.3f)
            close()
        }

        drawPath(
            path = innerPath,
            color = PrimaryPurple.copy(alpha = 0.5f)
        )

        // Check mark
        val checkPath = Path().apply {
            moveTo(size.width * 0.3f, size.height * 0.5f)
            lineTo(size.width * 0.45f, size.height * 0.65f)
            lineTo(size.width * 0.7f, size.height * 0.35f)
        }

        drawPath(
            path = checkPath,
            color = Color.White,
            style = Stroke(width = 8f)
        )
    }
}