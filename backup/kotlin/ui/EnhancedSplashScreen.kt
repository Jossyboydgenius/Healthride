package com.example.healthride.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthride.ui.components.Blob
import com.example.healthride.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ModernSplashScreen(onSplashFinished: () -> Unit = {}) {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = 800,
            easing = EaseOutBack
        ),
        label = "logoScale"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = 300
        ),
        label = "textAlpha"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2200)
        onSplashFinished()
    }

    // Modern gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFC),
                        Color(0xFFF1F5FD)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background blobs
        Blob(
            color = PrimaryBlue,
            size = 600,
            xOffset = -300f,
            yOffset = -300f,
            modifier = Modifier.alpha(alpha = if (startAnimation) 0.08f else 0f)
        )

        Blob(
            color = PrimaryPurple,
            size = 500,
            xOffset = 300f,
            yOffset = 500f,
            modifier = Modifier.alpha(alpha = if (startAnimation) 0.08f else 0f)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .rotate(15f)
                    .size(120.dp)
                    .scale(logoScale)
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryBlue, PrimaryPurple)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {


            }

            Spacer(modifier = Modifier.height(24.dp))

            // App name with fade-in animation
            Column(
                modifier = Modifier.alpha(textAlpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "HealthRide",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Medical Transport Simplified",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMediumGray
                )
            }
        }
    }
}