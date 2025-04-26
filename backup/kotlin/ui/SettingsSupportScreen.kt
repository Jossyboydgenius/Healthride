package com.example.healthride.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.healthride.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSupportScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Animation states
    var showContent by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    // Settings states
    var isNotificationsEnabled by remember { mutableStateOf(true) }
    var isLocationEnabled by remember { mutableStateOf(true) }
    var isReminderEnabled by remember { mutableStateOf(true) }
    var isDarkMode by remember { mutableStateOf(false) }
    var isAccessibilityEnabled by remember { mutableStateOf(false) }
    var isBiometricEnabled by remember { mutableStateOf(false) }
    var measurementSystem by remember { mutableStateOf("Imperial") }
    var languagePreference by remember { mutableStateOf("English") }

    // Trigger animations
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings & Support",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Notification Settings Card
                    SettingsCard(
                        title = "Notifications",
                        icon = Icons.Outlined.Notifications,
                        iconTint = PrimaryBlue
                    ) {
                        SwitchSettingItem(
                            title = "Enable Notifications",
                            description = "Receive important updates about your rides",
                            checked = isNotificationsEnabled,
                            onCheckedChange = { isNotificationsEnabled = it }
                        )

                        SwitchSettingItem(
                            title = "Ride Reminders",
                            description = "Get reminded 1 hour before scheduled rides",
                            checked = isReminderEnabled,
                            onCheckedChange = { isReminderEnabled = it },
                            enabled = isNotificationsEnabled
                        )

                        SwitchSettingItem(
                            title = "Driver Updates",
                            description = "Get notified when your driver is assigned or nearby",
                            checked = isNotificationsEnabled,
                            onCheckedChange = { /* This is controlled by the main notification toggle */ },
                            enabled = isNotificationsEnabled,
                            isLast = true
                        )
                    }

                    // Privacy & Security Card
                    SettingsCard(
                        title = "Privacy & Security",
                        icon = Icons.Outlined.Security,
                        iconTint = PrimaryPurple
                    ) {
                        SwitchSettingItem(
                            title = "Location Services",
                            description = "Allow the app to access your location",
                            checked = isLocationEnabled,
                            onCheckedChange = { isLocationEnabled = it }
                        )

                        SwitchSettingItem(
                            title = "Biometric Authentication",
                            description = "Use fingerprint or face ID to sign in",
                            checked = isBiometricEnabled,
                            onCheckedChange = { isBiometricEnabled = it }
                        )

                        ClickableSettingItem(
                            title = "Privacy Policy",
                            description = "Read about how we handle your data",
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/privacy"))
                                context.startActivity(intent)
                            },
                            isLast = true
                        )
                    }

                    // Appearance Card
                    SettingsCard(
                        title = "Appearance",
                        icon = Icons.Outlined.Palette,
                        iconTint = Color(0xFF4EA8DE) // Light blue
                    ) {
                        SwitchSettingItem(
                            title = "Dark Mode",
                            description = "Switch between light and dark themes",
                            checked = isDarkMode,
                            onCheckedChange = { isDarkMode = it }
                        )

                        SwitchSettingItem(
                            title = "Accessibility Features",
                            description = "Larger text, increased contrast, etc.",
                            checked = isAccessibilityEnabled,
                            onCheckedChange = { isAccessibilityEnabled = it },
                            isLast = true
                        )
                    }

                    // Preferences Card
                    SettingsCard(
                        title = "Preferences",
                        icon = Icons.Outlined.Tune,
                        iconTint = Color(0xFFE76F51) // Orange
                    ) {
                        DropdownSettingItem(
                            title = "Language",
                            description = "Choose your preferred language",
                            currentValue = languagePreference,
                            options = listOf("English", "Spanish", "French", "German", "Chinese"),
                            onValueChange = { languagePreference = it }
                        )

                        DropdownSettingItem(
                            title = "Measurement System",
                            description = "Distance and other measurements",
                            currentValue = measurementSystem,
                            options = listOf("Imperial", "Metric"),
                            onValueChange = { measurementSystem = it },
                            isLast = true
                        )
                    }

                    // Help & Support Card
                    SettingsCard(
                        title = "Help & Support",
                        icon = Icons.Outlined.ContactSupport,
                        iconTint = SuccessGreen
                    ) {
                        ClickableSettingItem(
                            title = "FAQs",
                            description = "Find answers to common questions",
                            onClick = {
                                dialogMessage = "FAQs would open here"
                                showSuccessDialog = true
                            }
                        )

                        ClickableSettingItem(
                            title = "Contact Support",
                            description = "Get help from our team",
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:support@healthride.com")
                                        putExtra(Intent.EXTRA_SUBJECT, "HealthRide Support Request")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not open email app", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                        ClickableSettingItem(
                            title = "Live Chat",
                            description = "Chat with our support team",
                            onClick = {
                                dialogMessage = "Live chat would open here"
                                showSuccessDialog = true
                            },
                            isLast = true
                        )
                    }

                    // About Card
                    SettingsCard(
                        title = "About",
                        icon = Icons.Outlined.Info,
                        iconTint = PrimaryBlue
                    ) {
                        ClickableSettingItem(
                            title = "App Version",
                            description = "1.0.0 (Build 1234)",
                            onClick = { /* No action */ },
                            showArrow = false
                        )

                        ClickableSettingItem(
                            title = "Terms of Service",
                            description = "Read the terms of service",
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/terms"))
                                context.startActivity(intent)
                            }
                        )

                        ClickableSettingItem(
                            title = "Open Source Licenses",
                            description = "View licenses for third-party software",
                            onClick = {
                                dialogMessage = "Licenses would show here"
                                showSuccessDialog = true
                            },
                            isLast = true
                        )
                    }

                    // Delete Account Button (at the bottom)
                    Button(
                        onClick = {
                            dialogMessage = "Account deletion would be processed here"
                            showSuccessDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorRed.copy(alpha = 0.9f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteForever,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Delete Account",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDarkBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = dialogMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMediumGray,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun SettingsCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
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
            modifier = Modifier.fillMaxWidth()
        ) {
            // Card header with expand/collapse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon with background
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(iconTint.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkBlue
                    )
                }

                // Expand/collapse icon
                val rotation = animateFloatAsState(
                    targetValue = if (isExpanded) 0f else 180f,
                    label = "arrow_rotation"
                )

                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = TextMediumGray,
                    modifier = Modifier.graphicsLayer { rotationZ = rotation.value }
                )
            }

            // Divider
            Divider(color = Color(0xFFF0F4F8))

            // Card content (collapsible)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun SwitchSettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    isLast: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .alpha(if (enabled) 1f else 0.6f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = TextDarkBlue
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMediumGray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Switch(
                checked = checked,
                onCheckedChange = { if (enabled) onCheckedChange(it) },
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryBlue,
                    checkedBorderColor = PrimaryBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray.copy(alpha = 0.6f),
                    uncheckedBorderColor = Color.LightGray.copy(alpha = 0.6f)
                )
            )
        }

        if (!isLast) {
            Divider(
                color = Color(0xFFF1F5F9),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ClickableSettingItem(
    title: String,
    description: String,
    onClick: () -> Unit,
    isLast: Boolean = false,
    showArrow: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = TextDarkBlue
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMediumGray
                )
            }

            if (showArrow) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Open",
                    tint = TextLightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (!isLast) {
            Divider(
                color = Color(0xFFF1F5F9),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun DropdownSettingItem(
    title: String,
    description: String,
    currentValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    isLast: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = TextDarkBlue
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMediumGray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentValue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Medium
                )

                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Select",
                    tint = PrimaryBlue
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(SurfaceWhite)
                    .width(200.dp)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontWeight = if (option == currentValue) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                        leadingIcon = {
                            if (option == currentValue) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = PrimaryBlue
                                )
                            }
                        }
                    )
                }
            }
        }

        if (!isLast) {
            Divider(
                color = Color(0xFFF1F5F9),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}