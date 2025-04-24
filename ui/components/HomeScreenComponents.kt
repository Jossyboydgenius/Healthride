package com.example.healthride.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthride.ui.theme.*

/**
 * Custom color scheme with modern vibrant colors
 */
object HealthRideColors {
    val PrimaryBlue = Color(0xFF0080FF)
    val PrimaryPurple = Color(0xFF7C3AED)
    val AccentTeal = Color(0xFF00E0B0)
    val BackgroundLight = Color(0xFFF8F9FF)
    val BackgroundWhite = Color(0xFFFFFFFF)
    val TextDark = Color(0xFF0F172A)
    val TextMedium = Color(0xFF64748B)
    val TextLight = Color(0xFFA0AEC0)
    val SurfaceLight = Color(0xFFF1F5F9)
    val SuccessGreen = Color(0xFF10B981)
    val WarningOrange = Color(0xFFFF9800)
    val ErrorRed = Color(0xFFEF4444)
}

// Helper class for multiple return values
data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

// Step indicator bar for booking flow
@Composable
fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    titles: List<String>,
    modifier: Modifier = Modifier
) {
    val progressAnimation = remember(currentStep) {
        Animatable(initialValue = currentStep.toFloat() / (totalSteps - 1).coerceAtLeast(1))
    }

    LaunchedEffect(currentStep) {
        progressAnimation.animateTo(
            targetValue = currentStep.toFloat() / (totalSteps - 1).coerceAtLeast(1),
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (currentStep < titles.size) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = titles[currentStep],
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = HealthRideColors.TextDark
                )

                Spacer(modifier = Modifier.weight(1f))

                // Step counter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    HealthRideColors.PrimaryBlue.copy(alpha = 0.1f),
                                    HealthRideColors.PrimaryPurple.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${currentStep + 1}/$totalSteps",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = HealthRideColors.PrimaryPurple
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Progress indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(HealthRideColors.TextLight.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressAnimation.value)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                HealthRideColors.PrimaryBlue,
                                HealthRideColors.PrimaryPurple
                            )
                        )
                    )
            )
        }
    }
}

// Location search bar with animations
@Composable
fun AddressSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isActive: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.7f,
        animationSpec = tween(durationMillis = 300),
        label = "backgroundAlpha"
    )

    val elevation by animateDpAsState(
        targetValue = if (isActive) 8.dp else 2.dp,
        animationSpec = tween(durationMillis = 300),
        label = "elevation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(HealthRideColors.BackgroundWhite.copy(alpha = backgroundAlpha))
            .padding(16.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(12.dp))
            }

            Text(
                text = if (value.isEmpty()) placeholder else value,
                color = if (value.isEmpty()) HealthRideColors.TextMedium else HealthRideColors.TextDark,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(12.dp))
                trailingIcon()
            }
        }
    }
}

// Ride type selection pill
@Composable
fun RideTypePill(
    title: String,
    icon: @Composable () -> Unit,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundBrush = if (isSelected) {
        Brush.horizontalGradient(
            colors = listOf(
                HealthRideColors.PrimaryBlue,
                HealthRideColors.PrimaryPurple
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                HealthRideColors.BackgroundWhite,
                HealthRideColors.BackgroundWhite
            )
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundBrush)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = if (isSelected) Color.White else HealthRideColors.TextDark,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

// Time slot chip for booking
@Composable
fun TimeSlotChip(
    time: String,
    isSelected: Boolean,
    isAvailable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        !isAvailable -> HealthRideColors.TextLight.copy(alpha = 0.1f)
        isSelected -> Brush.horizontalGradient(
            colors = listOf(
                HealthRideColors.PrimaryBlue,
                HealthRideColors.PrimaryPurple
            )
        )
        else -> Brush.horizontalGradient(
            colors = listOf(
                HealthRideColors.BackgroundWhite,
                HealthRideColors.BackgroundWhite
            )
        )
    }

    val textColor = when {
        !isAvailable -> HealthRideColors.TextLight
        isSelected -> Color.White
        else -> HealthRideColors.TextDark
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (isAvailable) backgroundColor else HealthRideColors.TextLight.copy(alpha = 0.1f))
            .clickable(enabled = isAvailable, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            color = textColor,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
} 