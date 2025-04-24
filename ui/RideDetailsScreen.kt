package com.example.healthride.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.healthride.data.model.Ride
import com.example.healthride.data.model.RideStatus
import com.example.healthride.SampleData
import com.example.healthride.formatToPattern
import com.example.healthride.ui.components.GradientBackground
import com.example.healthride.ui.components.GradientType
import com.example.healthride.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideDetailsScreen(
    ride: Ride = SampleData.upcomingRides.firstOrNull() ?: SampleData.pastRides.first(),
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    // Animation states
    var showContent by remember { mutableStateOf(false) }

    // Start animations
    LaunchedEffect(Unit) {
        delay(300) // Brief delay before showing content
        showContent = true
    }

    // Choose background gradient based on ride status
    val gradientType = when (ride.status) {
        RideStatus.COMPLETED -> GradientType.SUCCESS_LIGHT
        RideStatus.SCHEDULED -> GradientType.BLUE_LIGHT
        RideStatus.IN_PROGRESS -> GradientType.BLUE_PURPLE
        RideStatus.CANCELLED -> GradientType.PURPLE_LIGHT
    }

    GradientBackground(gradientType = gradientType) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Ride Details", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = TextDarkBlue
                    )
                )
            },
            containerColor = Color.Transparent // Make scaffold transparent to show gradient
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
            ) {
                // Animated visibility for content
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Enhanced Status Card with animation
                        EnhancedStatusCard(ride = ride)

                        // Route Visualization Card
                        RouteVisualizationCard(ride = ride)

                        // Ride Details Card
                        RideDetailsCard(ride = ride)

                        // Driver Card - with improved styling
                        if (!ride.driverName.isNullOrBlank()) {
                            DriverCard(ride = ride)
                        }

                        // Action Card - based on ride status
                        ActionCard(status = ride.status)

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedStatusCard(
    ride: Ride,
    modifier: Modifier = Modifier
) {
    val (color, icon, title, description) = when (ride.status) {
        RideStatus.SCHEDULED -> {
            val dateFormatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
            val date = dateFormatter.format(ride.dateTime)
            val time = timeFormatter.format(ride.dateTime)
            Quad(InfoBlue, Icons.Rounded.Schedule, "Ride Scheduled", "Your ride is scheduled for $date at $time")
        }
        RideStatus.IN_PROGRESS -> Quad(WarningYellow, Icons.Rounded.DirectionsCar, "Ride in Progress", "Your driver is on the way to your destination")
        RideStatus.COMPLETED -> Quad(SuccessGreen, Icons.Rounded.CheckCircle, "Ride Completed", "You've successfully completed this ride")
        RideStatus.CANCELLED -> Quad(ErrorRed, Icons.Outlined.Cancel, "Ride Cancelled", "This ride was cancelled")
    }

    // Animation for the icon
    val infiniteTransition = rememberInfiniteTransition(label = "status_icon_animation")
    val scale = if (ride.status == RideStatus.IN_PROGRESS) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "icon_pulse"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        spotColor = color.copy(alpha = 0.3f)
                    )
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
                    .scale(scale.value),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = color,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDarkBlue
                )
            }
        }
    }
}

@Composable
fun RouteVisualizationCard(
    ride: Ride,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Route visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(16.dp)
            ) {
                // Start point
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                        .align(Alignment.TopStart)
                )

                // Vertical line connecting points
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(84.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(PrimaryBlue, PrimaryPurple)
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                        .align(Alignment.TopStart)
                        .offset(x = 6.dp, y = 18.dp)
                )

                // End point
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(PrimaryPurple)
                        .align(Alignment.BottomStart)
                )

                // Origin address
                Text(
                    text = ride.pickupAddress,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDarkBlue,
                    modifier = Modifier
                        .padding(start = 24.dp, end = 16.dp)
                        .align(Alignment.TopStart)
                )

                // Destination address
                Text(
                    text = ride.destinationAddress,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDarkBlue,
                    modifier = Modifier
                        .padding(start = 24.dp, end = 16.dp)
                        .align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
fun RideDetailsCard(
    ride: Ride,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Ride Details",
                style = MaterialTheme.typography.titleLarge,
                color = TextDarkBlue,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            DetailRow(
                icon = Icons.Outlined.CalendarToday,
                title = "Date & Time",
                value = ride.dateTime.formatToPattern("EEEE, MMMM d, hh:mm a"),
                iconColor = PrimaryBlue
            )

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = BackgroundGray
            )

            // Add passenger info if available
            DetailRow(
                icon = Icons.Outlined.Person,
                title = "Passenger",
                value = ride.passengerName,
                iconColor = PrimaryPurple
            )

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = BackgroundGray
            )

            // Add ride type info
            DetailRow(
                icon = if (ride.rideType == "AMBULATORY") Icons.Outlined.DirectionsCar else Icons.Outlined.Accessible,
                title = "Ride Type",
                value = if (ride.rideType == "AMBULATORY") "Standard Ride" else "Wheelchair Accessible",
                iconColor = if (ride.rideType == "AMBULATORY") PrimaryBlue else PrimaryPurple
            )

            // Show price info if available
            val priceToShow = ride.actualPrice ?: ride.estimatedPrice
            if (priceToShow != null) {
                Divider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = BackgroundGray
                )

                DetailRow(
                    icon = Icons.Outlined.CreditCard,
                    title = "Ride Cost",
                    value = String.format(Locale.US, "$%.2f", priceToShow) + if(ride.actualPrice == null) " (estimated)" else "",
                    iconColor = SuccessGreen
                )
            }

            if (ride.status == RideStatus.COMPLETED) {
                Divider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = BackgroundGray
                )

                DetailRow(
                    icon = Icons.Outlined.ConfirmationNumber,
                    title = "Ride ID",
                    value = ride.id,
                    iconColor = TextMediumGray
                )
            }
        }
    }
}

@Composable
fun DriverCard(
    ride: Ride,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Driver Information",
                style = MaterialTheme.typography.titleLarge,
                color = TextDarkBlue,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Driver avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            spotColor = PrimaryPurple.copy(alpha = 0.3f)
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    PrimaryPurple.copy(alpha = 0.7f),
                                    PrimaryBlue.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ride.driverName?.firstOrNull()?.toString() ?: "?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = ride.driverName ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDarkBlue,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (!ride.vehicleInfo.isNullOrBlank()) {
                        Text(
                            text = ride.vehicleInfo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMediumGray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating (Keep placeholder or fetch actual rating later)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = if (index < 4) WarningYellow else TextLightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "4.8",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMediumGray
                        )
                    }
                }
            }

            // Buttons only shown for certain statuses
            if (ride.status == RideStatus.SCHEDULED || ride.status == RideStatus.IN_PROGRESS) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Call button
                    Button(
                        onClick = { /* Call driver */ },
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Outlined.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            "Call Driver",
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Message button
                    OutlinedButton(
                        onClick = { /* Message driver */ },
                        shape = RoundedCornerShape(50.dp),
                        border = BorderStroke(
                            width = 2.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(PrimaryBlue, PrimaryPurple)
                            )
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            "Message",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    status: RideStatus,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                RideStatus.COMPLETED -> SuccessGreen.copy(alpha = 0.05f)
                RideStatus.SCHEDULED -> InfoBlue.copy(alpha = 0.05f)
                RideStatus.IN_PROGRESS -> WarningYellow.copy(alpha = 0.05f)
                RideStatus.CANCELLED -> SurfaceWhite
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (status) {
                RideStatus.SCHEDULED -> {
                    Text(
                        "Need to change your plans?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextDarkBlue,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { /* Cancel ride */ },
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorRed
                        ),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Icon(
                            Icons.Outlined.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            "Cancel Ride",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                RideStatus.COMPLETED -> {
                    Text(
                        "How was your ride?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextDarkBlue,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rating stars with animations
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        repeat(5) { index ->
                            val selected = remember { mutableStateOf(index < 4) }
                            val scale = animateFloatAsState(
                                targetValue = if (selected.value) 1.2f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "star_scale"
                            )

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .scale(scale.value)
                                    .clip(CircleShape)
                                    .clickable { selected.value = !selected.value }
                                    .background(
                                        color = if (selected.value) WarningYellow.copy(alpha = 0.1f) else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.Star,
                                    contentDescription = null,
                                    tint = if (selected.value) WarningYellow else TextLightGray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedButton(
                        onClick = { /* Leave review */ },
                        shape = RoundedCornerShape(50.dp),
                        border = BorderStroke(
                            width = 2.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(PrimaryBlue, PrimaryPurple)
                            )
                        ),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Icon(
                            Icons.Outlined.RateReview,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            "Leave a Review",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                else -> {
                    Button(
                        onClick = { /* Book new ride */ },
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        ),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            "Book a New Ride",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    title: String,
    value: String,
    iconColor: Color = TextMediumGray,
    valueColor: Color = TextDarkBlue
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = TextMediumGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = valueColor
            )
        }
    }
}

// Helper class
private data class Quad<T1, T2, T3, T4>(val first: T1, val second: T2, val third: T3, val fourth: T4)