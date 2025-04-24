package com.example.healthride.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.healthride.ui.components.Blob
import com.example.healthride.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_animation")

    // Animation for the logo
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    // Animation for the button
    var isButtonVisible by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        delay(800)
        isButtonVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFF1F5FD), Color(0xFFF1EFFF))
                )
            )
    ) {
        // Background blobs for visual interest
        Blob(
            color = PrimaryBlue,
            size = 600,
            xOffset = -300f,
            yOffset = -200f,
            modifier = Modifier.alpha(0.08f)
        )

        Blob(
            color = PrimaryPurple,
            size = 500,
            xOffset = 300f,
            yOffset = 400f,
            modifier = Modifier.alpha(0.08f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .rotate(15f)
                    .scale(logoScale)
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryBlue, PrimaryPurple)
                        )
                    )
                    .rotate(15f),
                contentAlignment = Alignment.Center
            ) {

            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name
            Text(
                text = "HealthRide",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TextDarkBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Medical Transport Simplified",
                style = MaterialTheme.typography.bodyLarge,
                color = TextMediumGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Animated get started button
            AnimatedVisibility(
                visible = isButtonVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                modifier = Modifier.fillMaxWidth()
            ) {
                GradientButton(
                    text = "Get Started",
                    onClick = onGetStartedClick,
                    endIcon = Icons.Default.ArrowForward,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                )
            }
        }
    }
}