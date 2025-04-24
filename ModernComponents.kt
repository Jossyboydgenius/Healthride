// Create a new file: ModernComponents.kt
package com.example.healthride

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthride.data.model.RideStatus
import com.example.healthride.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Modern button with gradient, elevation and loading state
@Composable
fun ModernButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    Box(
        modifier = modifier
            .scale(scale.value)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = PrimaryBlue.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (enabled) {
                        listOf(PrimaryBlue, PrimaryPurple)
                    } else {
                        listOf(TextLightGray, TextLightGray)
                    }
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = {
                    scope.launch {
                        scale.animateTo(
                            0.95f,
                            animationSpec = tween(100)
                        )
                        scale.animateTo(
                            1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }
                    onClick()
                }
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )

            if (trailingIcon != null && !isLoading) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

// Modern card component
@Composable
fun ModernCard(
    modifier: Modifier = Modifier,
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = elevation,
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        )
    ) {
        content()
    }
}

// Enhanced status chip
@Composable
fun EnhancedStatusChip(status: RideStatus) {
    val (backgroundColor, iconTint, text) = when (status) {
        RideStatus.SCHEDULED -> Triple(InfoBlue.copy(alpha = 0.1f), InfoBlue, "Scheduled")
        RideStatus.IN_PROGRESS -> Triple(WarningYellow.copy(alpha = 0.1f), WarningYellow, "In Progress")
        RideStatus.COMPLETED -> Triple(SuccessGreen.copy(alpha = 0.1f), SuccessGreen, "Completed")
        RideStatus.CANCELLED -> Triple(ErrorRed.copy(alpha = 0.1f), ErrorRed, "Cancelled")
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp)),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(14.dp)
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = iconTint
            )
        }
    }
}



// Modern section header
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextDarkBlue
        )

        if (action != null) {
            action()
        }
    }
}

// Modern list item
@Composable
fun ModernListItem(
    title: String,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector = Icons.Default.ChevronRight,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 16.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextDarkBlue
            )

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMediumGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Icon(
            imageVector = trailingIcon,
            contentDescription = null,
            tint = TextLightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

