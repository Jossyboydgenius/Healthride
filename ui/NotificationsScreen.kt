package com.example.healthride.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.healthride.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val time: Date,
    val type: NotificationType,
    val isRead: Boolean
)

enum class NotificationType {
    RIDE_CONFIRMED, DRIVER_ASSIGNED, RIDE_REMINDER, RIDE_COMPLETED, PROMOTION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit = {}
) {
    // Sample notifications
    val notifications = remember {
        listOf(
            Notification(
                id = "n1",
                title = "Driver Assigned",
                message = "Michael Johnson will be your driver for your upcoming ride to Medical Center.",
                time = Calendar.getInstance().apply { add(Calendar.HOUR, -1) }.time,
                type = NotificationType.DRIVER_ASSIGNED,
                isRead = false
            ),
            Notification(
                id = "n2",
                title = "Ride Reminder",
                message = "Reminder: You have a scheduled ride tomorrow at 9:30 AM to City Hospital.",
                time = Calendar.getInstance().apply { add(Calendar.HOUR, -5) }.time,
                type = NotificationType.RIDE_REMINDER,
                isRead = false
            ),
            Notification(
                id = "n3",
                title = "Ride Completed",
                message = "Your ride to Medical Center has been completed. Thank you for using HealthRide!",
                time = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
                type = NotificationType.RIDE_COMPLETED,
                isRead = true
            ),
            Notification(
                id = "n4",
                title = "Special Offer",
                message = "Get 20% off on your next 3 rides! Offer valid for the next 7 days.",
                time = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) }.time,
                type = NotificationType.PROMOTION,
                isRead = true
            ),
            Notification(
                id = "n5",
                title = "Ride Confirmed",
                message = "Your ride to Wellness Clinic has been confirmed for next Monday.",
                time = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -3) }.time,
                type = NotificationType.RIDE_CONFIRMED,
                isRead = true
            )
        )
    }

    // Animation for content appearance
    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Beautiful gradient background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gradientBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFF0F4FF),  // Light blue
                    Color(0xFFF5F0FF),  // Light purple
                    Color(0xFFF8FBFF)   // White-ish
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )
            drawRect(brush = gradientBrush)

            // Abstract decorative shape
            val accentBrush = Brush.radialGradient(
                colors = listOf(
                    PrimaryBlue.copy(alpha = 0.1f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.1f, size.height * 0.1f),
                radius = size.width * 0.5f
            )
            drawCircle(
                brush = accentBrush,
                radius = size.width * 0.5f,
                center = Offset(size.width * 0.1f, size.height * 0.1f)
            )

            val accentBrush2 = Brush.radialGradient(
                colors = listOf(
                    PrimaryPurple.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.9f, size.height * 0.7f),
                radius = size.width * 0.6f
            )
            drawCircle(
                brush = accentBrush2,
                radius = size.width * 0.6f,
                center = Offset(size.width * 0.9f, size.height * 0.7f)
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Notifications", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        // Mark all as read button with animation
                        IconButton(
                            onClick = { /* Mark all as read */ }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DoneAll,
                                contentDescription = "Mark all as read",
                                tint = PrimaryBlue
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = TextDarkBlue
                    )
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                // Animated content appearance
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                ) {
                    if (notifications.isEmpty()) {
                        // Empty state with animation
                        ColorfulEmptyState()
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Unread count badge
                            val unreadCount = notifications.count { !it.isRead }
                            if (unreadCount > 0) {
                                GradientUnreadBanner(unreadCount = unreadCount)
                            }

                            // Group notifications by day
                            val todayStart = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.time

                            val yesterdayStart = Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_YEAR, -1)
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.time

                            val groupedNotifications = notifications.groupBy { notification ->
                                when {
                                    notification.time.after(todayStart) -> "Today"
                                    notification.time.after(yesterdayStart) -> "Yesterday"
                                    else -> "Earlier"
                                }
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                groupedNotifications.forEach { (dayGroup, notificationsInGroup) ->
                                    // Date header
                                    item {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 16.dp, horizontal = 4.dp)
                                        ) {
                                            Text(
                                                text = dayGroup,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = TextDarkBlue
                                            )

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Divider(
                                                modifier = Modifier.weight(1f),
                                                color = TextLightGray.copy(alpha = 0.5f)
                                            )
                                        }
                                    }

                                    // Notifications for this day
                                    items(notificationsInGroup) { notification ->
                                        // Staggered animation
                                        var itemVisible by remember { mutableStateOf(false) }
                                        LaunchedEffect(notification.id) {
                                            delay(100)
                                            itemVisible = true
                                        }

                                        AnimatedVisibility(
                                            visible = itemVisible,
                                            enter = fadeIn() + expandVertically(
                                                expandFrom = Alignment.Top,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            )
                                        ) {
                                            ColorfulNotificationCard(notification = notification)
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GradientUnreadBanner(unreadCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = PrimaryBlue.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PrimaryBlue.copy(alpha = 0.9f),
                            PrimaryPurple.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pulsating counter
                val infiniteTransition = rememberInfiniteTransition(label = "pulse_animation")
                val pulsate = infiniteTransition.animateFloat(
                    initialValue = 0.9f,
                    targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = EaseInOut),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer {
                            scaleX = pulsate.value
                            scaleY = pulsate.value
                        }
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = unreadCount.toString(),
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "You have $unreadCount unread notification${if (unreadCount > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ColorfulNotificationCard(notification: Notification) {
    val (iconColor, gradientStart, gradientEnd) = when (notification.type) {
        NotificationType.RIDE_CONFIRMED -> Triple(
            SuccessGreen,
            SuccessGreen,
            Color(0xFF34D399)
        )
        NotificationType.DRIVER_ASSIGNED -> Triple(
            PrimaryBlue,
            PrimaryBlue,
            Color(0xFF4CC2FF)
        )
        NotificationType.RIDE_REMINDER -> Triple(
            WarningYellow,
            WarningYellow,
            Color(0xFFFCD34D)
        )
        NotificationType.RIDE_COMPLETED -> Triple(
            SuccessGreen,
            SuccessGreen,
            Color(0xFF34D399)
        )
        NotificationType.PROMOTION -> Triple(
            PrimaryPurple,
            PrimaryPurple,
            Color(0xFF9F6EFF)
        )
    }

    val icon = when (notification.type) {
        NotificationType.RIDE_CONFIRMED -> Icons.Rounded.CheckCircle
        NotificationType.DRIVER_ASSIGNED -> Icons.Rounded.Person
        NotificationType.RIDE_REMINDER -> Icons.Rounded.Alarm
        NotificationType.RIDE_COMPLETED -> Icons.Rounded.DoneAll
        NotificationType.PROMOTION -> Icons.Rounded.LocalOffer
    }

    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (notification.isRead) 4.dp else 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = if (notification.isRead)
                    Color.Gray.copy(alpha = 0.1f)
                else
                    gradientStart.copy(alpha = 0.2f)
            )
            .graphicsLayer {
                alpha = if (notification.isRead) 0.85f else 1f
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Colorful icon with gradient background
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(gradientStart, gradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        color = TextDarkBlue
                    )

                    // Unread indicator
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(gradientStart, gradientEnd)
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (notification.isRead) TextMediumGray else TextDarkBlue,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFFF0F4FF))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = timeFormat.format(notification.time),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMediumGray
                    )
                }
            }
        }
    }
}

@Composable
fun ColorfulEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated empty state
        val infiniteTransition = rememberInfiniteTransition(label = "floating_animation")
        val translateY = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 20f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "float"
        )

        Box(
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer {
                    translationY = translateY.value
                }
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    spotColor = PrimaryBlue.copy(alpha = 0.3f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PrimaryBlue.copy(alpha = 0.7f),
                            PrimaryPurple.copy(alpha = 0.7f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "No Notifications Yet",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextDarkBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "We'll notify you when you have new messages, ride updates, or special offers",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMediumGray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Beautiful gradient button
        Card(
            modifier = Modifier
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(50.dp),
                    spotColor = PrimaryBlue.copy(alpha = 0.3f)
                ),
            shape = RoundedCornerShape(50.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                PrimaryBlue,
                                PrimaryPurple
                            )
                        )
                    )
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Book a Ride",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}