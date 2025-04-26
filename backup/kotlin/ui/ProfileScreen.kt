package com.example.healthride.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.healthride.data.model.User
import com.example.healthride.ui.components.Blob
import com.example.healthride.ui.theme.*
import com.example.healthride.ui.viewmodel.AuthViewModel
import com.example.healthride.ui.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    onBackClick: () -> Unit = {},
    onConfirmSignOut: () -> Unit = {},
    // Mock user data for preview/testing
    user: User? = User(
    )
) {
    val scrollState = rememberScrollState()
    var showSignOutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Animation states
    var showBackground by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    var headerScale = remember { Animatable(0.8f) }

    // Start animations
    LaunchedEffect(Unit) {
        delay(100)
        showBackground = true

        delay(300)
        coroutineScope.launch {
            headerScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }

        delay(200)
        showContent = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDarkBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = TextDarkBlue
                )
            )
        }
    ) { innerPadding ->
        // Background container with gradient
        Box(modifier = Modifier.fillMaxSize()) {
            // Add subtle gradient background
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


            // Add decorative blobs in background
            AnimatedVisibility(
                visible = showBackground,
                enter = fadeIn(initialAlpha = 0f)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Blob(
                        color = PrimaryBlue,
                        size = 300,
                        xOffset = -150f,
                        yOffset = -50f,
                        modifier = Modifier.alpha(0.08f)
                    )

                    Blob(
                        color = PrimaryPurple,
                        size = 280,
                        xOffset = 150f,
                        yOffset = 400f,
                        modifier = Modifier.alpha(0.08f)
                    )
                }
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)
            ) {
                // Profile Header
                ProfileHeader(
                    user = user,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(headerScale.value)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Main content sections
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Personal Information Section
                        SettingsCard(
                            title = "Personal Information",
                            icon = Icons.Outlined.Person,
                            onClick = { navController.navigate("personalInfo") }
                        ) {
                            SettingsItem(
                                title = "Email",
                                value = user?.email ?: "",
                                icon = Icons.Outlined.Email,
                                iconTint = PrimaryBlue
                            )

                            SettingsItem(
                                title = "Phone",
                                value = user?.phone ?: "",
                                icon = Icons.Outlined.Phone,
                                iconTint = PrimaryBlue
                            )

                            SettingsItem(
                                title = "Address",
                                value = user?.let { "${it.address}, ${it.city}, ${it.state} ${it.zipCode}" } ?: "",
                                icon = Icons.Outlined.Home,
                                iconTint = PrimaryBlue,
                                isLast = true
                            )
                        }

                        // Insurance & Payment Section
                        SettingsCard(
                            title = "Insurance & Payment",
                            icon = Icons.Outlined.Shield,
                            onClick = { navController.navigate("insurancePayment") }
                        ) {
                            SettingsItem(
                                title = "Insurance Provider",
                                value = user?.insuranceProvider ?: "Not set",
                                icon = Icons.Outlined.HealthAndSafety,
                                iconTint = PrimaryBlue,
                                isLast = true
                            )
                        }

                        // Settings & Support Section
                        SettingsCard(
                            title = "Settings & Support",
                            icon = Icons.Outlined.Settings,
                            onClick = { navController.navigate("settingsSupport") }
                        ) {
                            SettingsItem(
                                title = "Notifications",
                                value = "Manage your notifications",
                                icon = Icons.Outlined.Notifications,
                                iconTint = PrimaryBlue
                            )

                            SettingsItem(
                                title = "Privacy & Security",
                                value = "Manage privacy settings",
                                icon = Icons.Outlined.Security,
                                iconTint = PrimaryBlue
                            )

                            SettingsItem(
                                title = "Help & Support",
                                value = "Get assistance",
                                icon = Icons.Outlined.HelpOutline,
                                iconTint = PrimaryBlue,
                                isLast = true
                            )
                        }

                        // App Information
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceWhite
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "HealthRide",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkBlue
                                )

                                Text(
                                    text = "Version 1.0.0",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMediumGray
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "© 2025 HealthRide Inc.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMediumGray
                                )
                            }
                        }

                        // Sign Out Button
                        OutlinedButton(
                            onClick = { showSignOutDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = ErrorRed.copy(alpha = 0.5f)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = ErrorRed
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Logout,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Sign Out",
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Bottom spacing
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // Sign out confirmation dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDarkBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to sign out of your account?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMediumGray,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        onConfirmSignOut()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSignOutDialog = false },
                    shape = RoundedCornerShape(50.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Cancel", color = TextDarkBlue)
                }
            }
        )
    }
}

@Composable
fun ProfileHeader(
    user: User?,
    modifier: Modifier = Modifier
) {
    val displayName = user?.fullName?.takeIf { it.isNotBlank() } ?: "${user?.firstName} ${user?.lastName}"
    val initial = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "U"

    // Subtle pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "profile_header_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(vertical = 16.dp)
    ) {
        // Profile avatar with animation
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(scale)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    spotColor = PrimaryBlue.copy(alpha = 0.2f)
                )
                .clip(CircleShape)
                .background(Color.White)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PrimaryBlue.copy(alpha = 0.5f),
                            PrimaryPurple.copy(alpha = 0.5f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Gradient circle inside
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryBlue, PrimaryPurple)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User name
        Text(
            text = displayName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextDarkBlue
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Email
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = null,
                tint = TextMediumGray,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = user?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMediumGray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Verification Badge
        user?.verificationStatus?.let { status ->
            when (status.uppercase()) {
                "VERIFIED" -> {
                    Surface(
                        color = SuccessGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.VerifiedUser,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "Verified Account",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = SuccessGreen
                            )
                        }
                    }
                }
                "PENDING" -> {
                    Surface(
                        color = WarningYellow.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Pending,
                                contentDescription = null,
                                tint = WarningYellow,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "Verification Pending",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = WarningYellow
                            )
                        }
                    }
                }
                else -> {
                    Surface(
                        color = PrimaryBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "Complete Verification",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var cardScale by remember { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = cardScale,
        animationSpec = tween(150)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .clickable(
                onClick = {
                    // Simple press animation
                    cardScale = 0.98f
                    // Reset scale after a short delay
                    val handler = android.os.Handler(android.os.Looper.getMainLooper())
                    handler.postDelayed({
                        cardScale = 1f
                    }, 150)
                    onClick()
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Card header with chevron
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon with background
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkBlue
                    )
                }

                // Chevron icon
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Open",
                    tint = TextLightGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Divider(color = Color(0xFFF0F4F8))

            Spacer(modifier = Modifier.height(16.dp))

            // Card content
            content()
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    isLast: Boolean = false
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            // Icon with background
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text content
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextDarkBlue
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMediumGray
                )
            }
        }

        if (!isLast) {
            Divider(
                color = Color(0xFFF1F5F9),
                modifier = Modifier.padding(start = 48.dp)
            )
        }
    }
}

// Blob Animation Component
@Composable
private fun Blob(
    color: Color,
    size: Int,
    xOffset: Float,
    yOffset: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "blob-animation")

    // Scale animation
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // X position animation
    val moveX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "moveX"
    )

    // Y position animation
    val moveY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "moveY"
    )

    Box(
        modifier = modifier
            .offset(x = (xOffset + moveX).dp, y = (yOffset + moveY).dp)
            .size(size.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.08f))
    )
}