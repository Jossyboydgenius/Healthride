package com.example.healthride.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.healthride.ui.theme.*

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    gradientType: GradientType = GradientType.BLUE_PURPLE,
    content: @Composable BoxScope.() -> Unit
) {
    val gradient = when (gradientType) {
        GradientType.BLUE_PURPLE -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF0F4FF),
                Color(0xFFF1EFFF)
            )
        )
        GradientType.BLUE_LIGHT -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFEBF5FF),
                Color(0xFFF5FAFF)
            )
        )
        GradientType.PURPLE_LIGHT -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF5F0FF),
                Color(0xFFFCF9FF)
            )
        )
        GradientType.SUCCESS_LIGHT -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF0FFF8),
                Color(0xFFF5FFF9)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
        content = content
    )
}

enum class GradientType {
    BLUE_PURPLE, BLUE_LIGHT, PURPLE_LIGHT, SUCCESS_LIGHT
}