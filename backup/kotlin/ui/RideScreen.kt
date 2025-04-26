package com.example.healthride.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
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
import kotlinx.coroutines.delay
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RidesScreen(
    onBackClick: () -> Unit = {},
    onRideClick: (String) -> Unit = {}
) {
    val tabTitles = listOf("Upcoming", "Past")
    var selectedTabIndex by remember { mutableStateOf(0) }

    // Animation states
    var showContent by remember { mutableStateOf(false) }

    // Use SampleData lists
    val upcomingRides = SampleData.upcomingRides
    val pastRides = SampleData.pastRides

    // Start animations
    LaunchedEffect(Unit) {
        delay(300) // Brief delay for startup animation
        showContent = true
    }

    GradientBackground(gradientType = GradientType.BLUE_LIGHT) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Rides", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Filter */ }) {
                            Icon(Icons.Outlined.FilterAlt, "Filter")
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
            ) {
                // Custom Animated Tabs
                CustomAnimatedTabs(
                    tabTitles = tabTitles,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )

                // Animated content
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Determine which list to show
                        val currentList = if (selectedTabIndex == 0) upcomingRides else pastRides
                        val emptyMsg = if (selectedTabIndex == 0) "No upcoming rides" else "No past rides"

                        // Display the list or empty message
                        EnhancedRideList(
                            rides = currentList,
                            onRideClick = onRideClick,
                            emptyMessage = emptyMsg
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomAnimatedTabs(
    tabTitles: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(24.dp)
            )
            .background(SurfaceWhite.copy(alpha = 0.8f)),
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = PrimaryBlue,
            indicator = { tabPositions ->
                // Custom animated indicator
                if (selectedTabIndex < tabPositions.size) {
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .fillMaxWidth(1f / tabPositions.size)
                            .padding(3.dp)
                            .height(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        PrimaryBlue.copy(alpha = 0.15f),
                                        PrimaryPurple.copy(alpha = 0.15f)
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(PrimaryBlue, PrimaryPurple)
                                ),
                                shape = RoundedCornerShape(18.dp)
                            )
                    )
                }
            },
            divider = {}
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedTabIndex == index) PrimaryBlue else TextMediumGray
                        )
                    },
                    modifier = Modifier.height(42.dp)
                )
            }
        }
    }
}

@Composable
fun EnhancedRideList(
    rides: List<Ride>,
    onRideClick: (String) -> Unit,
    emptyMessage: String
) {
    if (rides.isEmpty()) {
        // Enhanced empty state visualization
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Empty state illustration
                EmptyStateIllustration()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextMediumGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your rides will appear here once scheduled",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMediumGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* Book now functionality */ },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )
                ) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        "Book a Ride",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    } else {
        // Enhanced ride list with staggered animation
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(rides, key = { ride -> ride.id }) { ride ->
                // Staggered animation for each item
                var itemVisible by remember { mutableStateOf(false) }

                LaunchedEffect(ride.id) {
                    delay(rides.indexOf(ride) * 100L) // Staggered delay
                    itemVisible = true
                }

                AnimatedVisibility(
                    visible = itemVisible,
                    enter = fadeIn() + slideInVertically { it / 4 }
                ) {
                    EnhancedRideCard(
                        ride = ride,
                        onClick = { onRideClick(ride.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedRideCard(
    ride: Ride,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(24.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Top section with date and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date with icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = ride.dateTime.formatToPattern("EEE, MMM d"),
                        fontWeight = FontWeight.Medium,
                        color = TextDarkBlue
                    )
                }

                // Time with icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurpleLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = PrimaryPurple,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = ride.dateTime.formatToPattern("h:mm a"),
                        fontWeight = FontWeight.Medium,
                        color = TextDarkBlue
                    )
                }

                // Status badge
                StatusBadge(status = ride.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                PrimaryBlue.copy(alpha = 0.1f),
                                PrimaryPurple.copy(alpha = 0.1f),
                                PrimaryBlue.copy(alpha = 0.1f)
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Route info with visual connector
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Source and destination icons with connector
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Source dot
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue)
                    )

                    // Connector line
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(40.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        PrimaryBlue, PrimaryPurple
                                    )
                                )
                            )
                    )

                    // Destination dot
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Addresses
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.height(70.dp)
                ) {
                    // Pickup address
                    Text(
                        text = ride.pickupAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkBlue
                    )

                    // Destination address
                    Text(
                        text = ride.destinationAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkBlue
                    )
                }
            }

            // Actions section for upcoming rides
            if (ride.status == RideStatus.SCHEDULED) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { /* Cancel ride */ },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = ErrorRed
                        )
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            "Cancel",
                            fontWeight = FontWeight.Medium
                        )
                    }

                    TextButton(
                        onClick = { onClick() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = PrimaryBlue
                        )
                    ) {
                        Text(
                            "View Details",
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            Icons.Outlined.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: RideStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        RideStatus.SCHEDULED -> Triple(PrimaryBlue.copy(alpha = 0.1f), PrimaryBlue, "Scheduled")
        RideStatus.IN_PROGRESS -> Triple(WarningYellow.copy(alpha = 0.1f), WarningYellow, "In Progress")
        RideStatus.COMPLETED -> Triple(SuccessGreen.copy(alpha = 0.1f), SuccessGreen, "Completed")
        RideStatus.CANCELLED -> Triple(ErrorRed.copy(alpha = 0.1f), ErrorRed, "Cancelled")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun EmptyStateIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "empty_state_animation")
    val dotScale = infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_scale"
    )

    Canvas(
        modifier = Modifier
            .size(180.dp)
            .padding(16.dp)
    ) {
        // Background circle
        drawCircle(
            color = PrimaryPurpleLight.copy(alpha = 0.3f),
            radius = size.minDimension / 2.2f,
            center = center
        )

        // Car or transportation icon (simplified)
        // Center coordinates
        val cx = center.x
        val cy = center.y
        val size = size.minDimension * 0.4f

        // Car body
        drawRoundRect(
            color = PrimaryBlue,
            topLeft = androidx.compose.ui.geometry.Offset(cx - size / 2, cy - size / 4),
            size = androidx.compose.ui.geometry.Size(size, size / 2),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f)
        )

        // Car roof
        drawRoundRect(
            color = PrimaryPurple,
            topLeft = androidx.compose.ui.geometry.Offset(cx - size / 3, cy - size / 2),
            size = androidx.compose.ui.geometry.Size(size * 2 / 3, size / 4),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f)
        )

        // Wheels
        val wheelRadius = size / 8 * dotScale.value
        drawCircle(
            color = TextDarkBlue,
            radius = wheelRadius,
            center = androidx.compose.ui.geometry.Offset(cx - size / 3, cy + size / 4)
        )
        drawCircle(
            color = TextDarkBlue,
            radius = wheelRadius,
            center = androidx.compose.ui.geometry.Offset(cx + size / 3, cy + size / 4)
        )
    }
}