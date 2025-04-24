package com.example.healthride.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.icons.filled.Event // Make sure this import is added
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import android.content.Context
import androidx.compose.ui.zIndex
import android.content.ContextWrapper
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.healthride.SampleData
import com.example.healthride.data.model.Ride
import com.example.healthride.data.model.RideType
import com.example.healthride.network.AutocompletePrediction
import com.example.healthride.ui.theme.*
import com.example.healthride.ui.viewmodel.UserViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.min
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.draw.shadow
import kotlin.math.cos
import kotlin.math.sin

// Helper class for multiple return values
data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

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

@Composable
fun RouteInfoItem(
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    label: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Enhanced icon with background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = HealthRideColors.TextMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Address text
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = HealthRideColors.TextDark
            )
        }
    }
}

// First step in booking flow - Trip Details
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsStep(
    pickupAddress: String,
    destinationAddress: String,
    selectedAppointmentType: String?,
    onAppointmentTypeSelect: (String) -> Unit,
    appointmentTypes: List<String>,
    showAppointmentDropdown: Boolean,
    onShowAppointmentDropdown: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    var showAppointmentTypeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Appointment Type Card with enhanced styling
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = HealthRideColors.BackgroundLight,
                contentColor = HealthRideColors.TextDark
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Card title with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Outlined.MedicalServices,
                        contentDescription = null,
                        tint = HealthRideColors.PrimaryPurple,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        "Appointment Type",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = HealthRideColors.TextDark
                    )
                }

                OutlinedTextField(
                    value = selectedAppointmentType ?: "",
                    onValueChange = { /* read-only */ },
                    readOnly = true,
                    placeholder = {
                        Text(
                            "Select appointment type",
                            style = MaterialTheme.typography.bodyLarge,
                            color = HealthRideColors.TextMedium
                        )
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = HealthRideColors.PrimaryPurple
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            showAppointmentTypeDialog = true
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthRideColors.PrimaryPurple,
                        unfocusedBorderColor = HealthRideColors.TextLight.copy(alpha = 0.5f),
                        focusedContainerColor = HealthRideColors.BackgroundWhite,
                        unfocusedContainerColor = HealthRideColors.BackgroundWhite
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = HealthRideColors.TextDark,
                        fontWeight = FontWeight.Medium
                    ),
                    enabled = false
                )

                if (showAppointmentTypeDialog) {
                    Dialog(
                        onDismissRequest = { showAppointmentTypeDialog = false },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true
                        )
                    ) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = HealthRideColors.BackgroundWhite
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .fillMaxHeight(0.7f)
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Select Appointment Type",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = HealthRideColors.TextDark
                                    )

                                    IconButton(
                                        onClick = { showAppointmentTypeDialog = false },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(HealthRideColors.TextLight.copy(alpha = 0.1f))
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Close",
                                            tint = HealthRideColors.TextMedium,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                // Search field
                                var searchQuery by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Search") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = null,
                                            tint = HealthRideColors.TextMedium
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = HealthRideColors.PrimaryPurple,
                                        unfocusedBorderColor = HealthRideColors.TextLight.copy(alpha = 0.5f),
                                        focusedContainerColor = HealthRideColors.BackgroundLight,
                                        unfocusedContainerColor = HealthRideColors.BackgroundLight
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Filtered types list
                                val filteredTypes = appointmentTypes.filter {
                                    it.contains(searchQuery, ignoreCase = true)
                                }

                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 24.dp)
                                ) {
                                    items(filteredTypes.size) { index ->
                                        val type = filteredTypes[index]
                                        val selected = type == selectedAppointmentType

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    if (selected)
                                                        HealthRideColors.PrimaryPurple.copy(alpha = 0.1f)
                                                    else
                                                        Color.Transparent
                                                )
                                                .clickable {
                                                    onAppointmentTypeSelect(type)
                                                    showAppointmentTypeDialog = false
                                                }
                                                .padding(vertical = 12.dp, horizontal = 16.dp)
                                        ) {
                                            // Icon selection logic
                                            val icon = when {
                                                type.contains("Primary") -> Icons.Outlined.HealthAndSafety
                                                type.contains("Specialist") -> Icons.Outlined.MedicalServices
                                                type.contains("Therapy") -> Icons.Outlined.Accessibility
                                                type.contains("Dental") -> Icons.Outlined.CleanHands
                                                type.contains("Mental") -> Icons.Outlined.Psychology
                                                type.contains("Pharmacy") -> Icons.Outlined.Medication
                                                else -> Icons.Outlined.LocalHospital
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        if (selected)
                                                            HealthRideColors.PrimaryPurple.copy(alpha = 0.15f)
                                                        else
                                                            HealthRideColors.PrimaryBlue.copy(alpha = 0.05f)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    icon,
                                                    contentDescription = null,
                                                    tint = if (selected)
                                                        HealthRideColors.PrimaryPurple
                                                    else
                                                        HealthRideColors.PrimaryBlue,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Text(
                                                text = type,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (selected)
                                                        FontWeight.SemiBold
                                                    else
                                                        FontWeight.Normal
                                                ),
                                                color = if (selected)
                                                    HealthRideColors.PrimaryPurple
                                                else
                                                    HealthRideColors.TextDark,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(start = 16.dp)
                                            )

                                            if (selected) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = HealthRideColors.PrimaryPurple,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        if (index < filteredTypes.size - 1) {
                                            Divider(
                                                color = HealthRideColors.TextLight.copy(alpha = 0.1f),
                                                modifier = Modifier.padding(start = 56.dp)
                                            )
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
}

// Ride type option component
@Composable
fun RideTypeOption(
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    // Selection animation
    val animatedSelection = animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "SelectionAnimation"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                HealthRideColors.BackgroundWhite
            else
                HealthRideColors.BackgroundWhite.copy(alpha = 0.7f)
        ),
        border = BorderStroke(
            width = 2.dp,
            brush = if (isSelected)
                Brush.horizontalGradient(
                    colors = listOf(
                        HealthRideColors.PrimaryBlue,
                        HealthRideColors.PrimaryPurple
                    )
                )
            else
                SolidColor(HealthRideColors.TextLight.copy(alpha = 0.2f))
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .animateContentSize()
            .graphicsLayer {
                scaleX = 0.95f + (0.05f * animatedSelection.value)
                scaleY = 0.95f + (0.05f * animatedSelection.value)
            }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Left side: Icon and Title
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon with animated background
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = if (isSelected) {
                                        listOf(
                                            HealthRideColors.PrimaryBlue.copy(alpha = 0.15f),
                                            HealthRideColors.PrimaryPurple.copy(alpha = 0.15f)
                                        )
                                    } else {
                                        listOf(
                                            HealthRideColors.TextLight.copy(alpha = 0.1f),
                                            HealthRideColors.TextLight.copy(alpha = 0.1f)
                                        )
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected)
                                HealthRideColors.PrimaryBlue
                            else
                                HealthRideColors.TextMedium,
                            modifier = Modifier
                                .size(28.dp)
                                .graphicsLayer {
                                    scaleX = 1f + (0.2f * animatedSelection.value)
                                    scaleY = 1f + (0.2f * animatedSelection.value)
                                }
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.3).sp
                            ),
                            color = if (isSelected)
                                HealthRideColors.PrimaryBlue
                            else
                                HealthRideColors.TextDark
                        )

                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected)
                                HealthRideColors.PrimaryPurple.copy(alpha = 0.8f)
                            else
                                HealthRideColors.TextMedium
                        )
                    }
                }

                // Right side: Info button and Selection indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Info button
                    IconButton(
                        onClick = onInfoClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Learn more",
                            tint = if (isSelected)
                                HealthRideColors.PrimaryPurple
                            else
                                HealthRideColors.TextMedium,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Radio button
                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected)
                                    HealthRideColors.PrimaryBlue
                                else
                                    HealthRideColors.TextLight.copy(alpha = 0.2f)
                            )
                            .border(
                                width = 2.dp,
                                color = if (isSelected)
                                    HealthRideColors.PrimaryBlue
                                else
                                    HealthRideColors.TextLight.copy(alpha = 0.4f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Selected indicator
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }

            // Description
            AnimatedVisibility(
                visible = isSelected,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = HealthRideColors.TextMedium,
                        modifier = Modifier.padding(start = 64.dp, end = 8.dp)
                    )

                    // Selected badge
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        HealthRideColors.PrimaryBlue,
                                        HealthRideColors.PrimaryPurple
                                    )
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Selected",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Ride Type Selector component
@Composable
fun RideTypeSelector(
    selectedRideType: RideType,
    onRideTypeChange: (RideType) -> Unit,
    onRideTypeInfoClick: (RideType) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Ambulatory option - Standard ride
        RideTypeOption(
            title = "Standard",
            subtitle = "Ambulatory",
            description = "For patients who can walk and enter/exit the vehicle with minimal assistance",
            icon = Icons.Outlined.DirectionsCar,
            isSelected = selectedRideType == RideType.AMBULATORY,
            onClick = { onRideTypeChange(RideType.AMBULATORY) },
            onInfoClick = { onRideTypeInfoClick(RideType.AMBULATORY) }
        )

        // Wheelchair option - Accessible ride
        RideTypeOption(
            title = "Wheelchair",
            subtitle = "Accessible",
            description = "For patients requiring wheelchair transportation with secure boarding assistance",
            icon = Icons.Outlined.Accessible,
            isSelected = selectedRideType == RideType.WHEELCHAIR,
            onClick = { onRideTypeChange(RideType.WHEELCHAIR) },
            onInfoClick = { onRideTypeInfoClick(RideType.WHEELCHAIR) }
        )
    }
}

// Step 2 - Ride Type selection
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideTypeStep(
    selectedRideType: RideType,
    onRideTypeChange: (RideType) -> Unit,
    onRideTypeInfoClick: (RideType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Enhanced card with better visuals
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = HealthRideColors.BackgroundLight,
                contentColor = HealthRideColors.TextDark
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Card title with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Outlined.AltRoute,
                        contentDescription = null,
                        tint = HealthRideColors.PrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        "Select Ride Type",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = HealthRideColors.TextDark
                    )
                }

                // Enhanced ride type selector
                RideTypeSelector(
                    selectedRideType = selectedRideType,
                    onRideTypeChange = onRideTypeChange,
                    onRideTypeInfoClick = onRideTypeInfoClick
                )
            }
        }

        // Additional information card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = HealthRideColors.PrimaryBlue.copy(alpha = 0.05f)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = null,
                    tint = HealthRideColors.PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    "Select the ride type that best fits your needs. All options are covered by your insurance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = HealthRideColors.TextDark,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    }
}

// Step 3 - Date and Time Selection
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedDateTimeStep(
    selectedDate: Date,
    onDateChange: (Date) -> Unit,
    selectedTime: Date,
    onTimeChange: (Date) -> Unit,
    minimumDateTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    // Format current selections for display
    val selectedLocalDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val selectedLocalTime = selectedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    // State for pickers
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- Date Selection Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HealthRideColors.BackgroundLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = HealthRideColors.PrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Select Date",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = HealthRideColors.TextDark
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Notice about advance time
                Card(
                    colors = CardDefaults.cardColors(containerColor = HealthRideColors.PrimaryBlue.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = null,
                            tint = HealthRideColors.PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Rides must be scheduled at least 7 hours in advance",
                            style = MaterialTheme.typography.bodySmall,
                            color = HealthRideColors.TextDark,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                // Date selection button
                Button(
                    onClick = { showDatePicker = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HealthRideColors.BackgroundWhite,
                        contentColor = HealthRideColors.TextDark
                    ),
                    border = BorderStroke(1.dp, HealthRideColors.PrimaryBlue.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = null,
                                tint = HealthRideColors.PrimaryBlue
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = selectedLocalDate.format(dateFormatter),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = HealthRideColors.TextMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Quick Select:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = HealthRideColors.TextMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Week days horizontal scroll
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val today = LocalDate.now()
                    val minDate = minimumDateTime.toLocalDate()
                    items(14) { dayOffset ->
                        val date = today.plusDays(dayOffset.toLong())
                        if (!date.isBefore(minDate)) {
                            val isSelected = date.isEqual(selectedLocalDate)
                            val isToday = date.isEqual(today)
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        isSelected -> HealthRideColors.PrimaryBlue
                                        isToday -> HealthRideColors.PrimaryBlue.copy(alpha = 0.1f)
                                        else -> HealthRideColors.BackgroundWhite
                                    }
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = when {
                                        isSelected -> HealthRideColors.PrimaryBlue
                                        else -> HealthRideColors.TextLight.copy(alpha = 0.2f)
                                    }
                                ),
                                modifier = Modifier
                                    .width(64.dp)
                                    .clickable {
                                        onDateChange(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                                    }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = date.format(DateTimeFormatter.ofPattern("EE")),
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = if (isSelected) Color.White else HealthRideColors.TextMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = date.dayOfMonth.toString(),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.White else HealthRideColors.TextDark
                                    )
                                    if (isToday) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Today",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) Color.White else HealthRideColors.PrimaryBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // --- Time Selection Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HealthRideColors.BackgroundLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = HealthRideColors.PrimaryPurple,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Select Time",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = HealthRideColors.TextDark
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { showTimePicker = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HealthRideColors.BackgroundWhite,
                        contentColor = HealthRideColors.TextDark
                    ),
                    border = BorderStroke(1.dp, HealthRideColors.PrimaryPurple.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = HealthRideColors.PrimaryPurple
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = selectedLocalTime.format(timeFormatter),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = HealthRideColors.TextMedium
                        )
                    }
                }
            }
        }
    }

    // --- Imperative DatePickerDialog ---
    if (showDatePicker) {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            val calendar = Calendar.getInstance().apply { time = selectedDate }
            DatePickerDialog(
                context,
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val newCalendar = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
                    onDateChange(newCalendar.time)
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    // --- Imperative TimePickerDialog ---
    if (showTimePicker) {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            val calendar = Calendar.getInstance().apply { time = selectedTime }
            TimePickerDialog(
                context,
                { _: TimePicker, hour: Int, minute: Int ->
                    val newCalendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                    }
                    onTimeChange(newCalendar.time)
                    showTimePicker = false
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        }
    }
}

// Step 4 - Special Requirements
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedSpecialRequirementsStep(
    bringCompanion: Boolean,
    onBringCompanionChange: (Boolean) -> Unit,
    specialNotes: String,
    onSpecialNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Passengers Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = HealthRideColors.BackgroundLight
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Card title with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Outlined.People,
                        contentDescription = null,
                        tint = HealthRideColors.PrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        "Passengers",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = HealthRideColors.TextDark
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Companion toggle with modern styling
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(HealthRideColors.BackgroundWhite)
                        .clickable { onBringCompanionChange(!bringCompanion) }
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (bringCompanion)
                                    HealthRideColors.PrimaryBlue.copy(alpha = 0.1f)
                                else
                                    HealthRideColors.TextLight.copy(alpha = 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.SupervisorAccount,
                            contentDescription = null,
                            tint = if (bringCompanion)
                                HealthRideColors.PrimaryBlue
                            else
                                HealthRideColors.TextMedium,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = "Bring a Companion",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = HealthRideColors.TextDark
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Add one additional passenger to your ride",
                            style = MaterialTheme.typography.bodySmall,
                            color = HealthRideColors.TextMedium
                        )
                    }

                    // Modern switch design
                    Switch(
                        checked = bringCompanion,
                        onCheckedChange = onBringCompanionChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = HealthRideColors.BackgroundWhite,
                            checkedTrackColor = HealthRideColors.PrimaryBlue,
                            uncheckedThumbColor = HealthRideColors.BackgroundWhite,
                            uncheckedTrackColor = HealthRideColors.TextLight.copy(alpha = 0.5f)
                        )
                    )
                }

                // Extra instructions for companion
                AnimatedVisibility(
                    visible = bringCompanion,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = HealthRideColors.PrimaryBlue.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Info,
                                contentDescription = null,
                                tint = HealthRideColors.PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )

                            Text(
                                text = "Your companion must be ready at the same pickup location. They cannot be picked up separately.",
                                style = MaterialTheme.typography.bodySmall,
                                color = HealthRideColors.TextDark,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Additional Information Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = HealthRideColors.BackgroundLight
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Card title with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Outlined.Description,
                        contentDescription = null,
                        tint = HealthRideColors.PrimaryPurple,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        "Additional Information",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = HealthRideColors.TextDark
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Special notes text field with modern styling
                OutlinedTextField(
                    value = specialNotes,
                    onValueChange = onSpecialNotesChange,
                    label = {
                        Text("Additional Notes (Optional)")
                    },
                    placeholder = {
                        Text(
                            "E.g., Gate code is #1234, use side entrance...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = HealthRideColors.TextLight
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthRideColors.PrimaryPurple,
                        unfocusedBorderColor = HealthRideColors.TextLight.copy(alpha = 0.5f),
                        focusedLabelColor = HealthRideColors.PrimaryPurple,
                        unfocusedLabelColor = HealthRideColors.TextMedium,
                        cursorColor = HealthRideColors.PrimaryPurple,
                        focusedContainerColor = HealthRideColors.BackgroundWhite,
                        unfocusedContainerColor = HealthRideColors.BackgroundWhite
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                // Character counter below text field
                Text(
                    text = "${specialNotes.length}/200",
                    style = MaterialTheme.typography.labelSmall,
                    color = HealthRideColors.TextMedium,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp, end = 4.dp)
                )
            }
        }
    }
}

// Step 5 - Confirmation
@Composable
fun EnhancedConfirmationStep(
    pickupAddress: String,
    destinationAddress: String,
    selectedDate: Date,
    selectedTime: Date,
    bringCompanion: Boolean,
    selectedRideType: RideType,
    specialNotes: String,
    appointmentType: String,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance().apply { time = selectedDate }
    val timeCalendar = Calendar.getInstance().apply { time = selectedTime }
    calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
    calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
    val dateTimeString = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(calendar.time)
    val timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.time)
    val numberOfPassengers = if (bringCompanion) 2 else 1

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Success Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = HealthRideColors.SuccessGreen.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(HealthRideColors.SuccessGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = HealthRideColors.SuccessGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        "Ready to Book",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = HealthRideColors.SuccessGreen
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Please review your ride details",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HealthRideColors.TextDark
                    )
                }
            }
        }

        // Ride Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = HealthRideColors.BackgroundLight
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Card header
                Text(
                    "Ride Summary",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = HealthRideColors.TextDark
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Date & Time with styled cards
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = HealthRideColors.BackgroundWhite
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = HealthRideColors.PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )

                        Column(
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Text(
                                text = dateTimeString,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = HealthRideColors.TextDark
                            )

                            Text(
                                text = timeString,
                                style = MaterialTheme.typography.bodyMedium,
                                color = HealthRideColors.TextMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Appointment Type
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = HealthRideColors.BackgroundWhite
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            Icons.Outlined.MedicalServices,
                            contentDescription = null,
                            tint = HealthRideColors.PrimaryPurple,
                            modifier = Modifier.size(24.dp)
                        )

                        Column(
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Text(
                                text = "Appointment Type",
                                style = MaterialTheme.typography.labelMedium,
                                color = HealthRideColors.TextMedium
                            )

                            Text(
                                text = appointmentType,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = HealthRideColors.TextDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Passengers & Ride Type combined
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Passengers
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = HealthRideColors.BackgroundWhite
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                Icons.Outlined.People,
                                contentDescription = null,
                                tint = HealthRideColors.PrimaryBlue,
                                modifier = Modifier.size(24.dp)
                            )

                            Column(
                                modifier = Modifier.padding(start = 16.dp)
                            ) {
                                Text(
                                    text = "Passengers",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = HealthRideColors.TextMedium
                                )

                                Text(
                                    text = "$numberOfPassengers ${if(numberOfPassengers > 1) "(+ Companion)" else ""}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = HealthRideColors.TextDark
                                )
                            }
                        }
                    }

                    // Ride Type
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = HealthRideColors.BackgroundWhite
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                if (selectedRideType == RideType.AMBULATORY)
                                    Icons.Outlined.DirectionsCar
                                else
                                    Icons.Outlined.Accessible,
                                contentDescription = null,
                                tint = HealthRideColors.PrimaryPurple,
                                modifier = Modifier.size(24.dp)
                            )

                            Column(
                                modifier = Modifier.padding(start = 16.dp)
                            ) {
                                Text(
                                    text = "Ride Type",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = HealthRideColors.TextMedium
                                )

                                Text(
                                    text = if (selectedRideType == RideType.AMBULATORY)
                                        "Standard"
                                    else
                                        "Wheelchair",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = HealthRideColors.TextDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pickup Location
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = HealthRideColors.BackgroundWhite
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            Icons.Outlined.MyLocation,
                            contentDescription = null,
                            tint = HealthRideColors.PrimaryBlue,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 2.dp)
                        )

                        Column(
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Text(
                                text = "Pickup",
                                style = MaterialTheme.typography.labelMedium,
                                color = HealthRideColors.TextMedium
                            )

                            Text(
                                text = pickupAddress,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = HealthRideColors.TextDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Destination
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = HealthRideColors.BackgroundWhite
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Place,
                            contentDescription = null,
                            tint = HealthRideColors.PrimaryPurple,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 2.dp)
                        )

                        Column(
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Text(
                                text = "Destination",
                                style = MaterialTheme.typography.labelMedium,
                                color = HealthRideColors.TextMedium
                            )

                            Text(
                                text = destinationAddress,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = HealthRideColors.TextDark
                            )
                        }
                    }
                }

                // Special requirements (if any)
                if (specialNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = HealthRideColors.BackgroundWhite
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Notes,
                                contentDescription = null,
                                tint = HealthRideColors.PrimaryBlue,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(top = 2.dp)
                            )

                            Column(
                                modifier = Modifier.padding(start = 16.dp)
                            ) {
                                Text(
                                    text = "Special Requirements",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = HealthRideColors.TextMedium
                                )

                                Text(
                                    text = specialNotes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = HealthRideColors.TextDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Insurance Coverage Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = HealthRideColors.PrimaryBlue.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Shield,
                                contentDescription = null,
                                tint = HealthRideColors.PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                "Covered by your insurance",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = HealthRideColors.PrimaryBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

// Dialog for ride type info
@Composable
fun EnhancedRideTypeInfoDialog(
    rideType: RideType,
    onDismiss: () -> Unit
) {
    val (title, description, icon, features) = when (rideType) {
        RideType.AMBULATORY -> Quadruple(
            "Standard (Ambulatory)",
            "For patients who can walk and transfer without significant assistance. Includes sedan or SUV transportation with a professional driver trained in patient transport.",
            Icons.Outlined.DirectionsCar,
            listOf(
                "Door-to-door service",
                "Professional medical transport driver",
                "Comfortable sedan or SUV vehicle",
                "Driver assistance with boarding"
            )
        )
        RideType.WHEELCHAIR -> Quadruple(
            "Wheelchair Accessible",
            "For patients requiring wheelchair transportation. Includes a wheelchair-accessible vehicle and drivers trained to assist with secure boarding, transport, and exiting.",
            Icons.Outlined.Accessible,
            listOf(
                "Wheelchair-accessible vehicle",
                "Hydraulic lift or ramp",
                "Secure wheelchair anchoring",
                "Specially trained drivers",
                "Full boarding and exiting assistance"
            )
        )
        // Handle other cases if RideType enum has more values
        else -> Quadruple("Unknown", "Description not available", Icons.Outlined.Help, emptyList())
    }

    // Animation states
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        showDialog = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        val slideInAnim = animateFloatAsState(
            targetValue = if (showDialog) 1f else 0f,
            animationSpec = tween(300, easing = FastOutSlowInEasing),
            label = "slide_in"
        )

        val scaleAnim = animateFloatAsState(
            targetValue = if (showDialog) 1f else 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "scale"
        )

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = HealthRideColors.BackgroundWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                    translationY = (1f - slideInAnim.value) * 100f
                    alpha = slideInAnim.value
                }
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Header with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            HealthRideColors.PrimaryBlue.copy(alpha = 0.15f),
                                            HealthRideColors.PrimaryPurple.copy(alpha = 0.15f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = HealthRideColors.PrimaryPurple,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = HealthRideColors.TextDark
                        )
                    }

                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(HealthRideColors.TextLight.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = HealthRideColors.TextMedium,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Description
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = HealthRideColors.TextDark,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Features list card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = HealthRideColors.BackgroundLight
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Features",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthRideColors.PrimaryPurple
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        features.forEachIndexed { index, feature ->
                            val itemAnim = remember { Animatable(0f) }

                            LaunchedEffect(showDialog) {
                                if (showDialog) {
                                    delay(150L + (index * 100L))
                                    itemAnim.animateTo(
                                        targetValue = 1f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer {
                                        alpha = itemAnim.value
                                        translationX = (1f - itemAnim.value) * 20f
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(HealthRideColors.PrimaryPurple.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = HealthRideColors.PrimaryPurple,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = feature,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = HealthRideColors.TextDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HealthRideColors.PrimaryPurple
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        "Got it",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

// Route Preview Overlay
@Composable
fun RoutePreviewOverlay(
    isVisible: Boolean,
    originAddress: String,
    destinationAddress: String,
    onConfirm: () -> Unit,
    onClose: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut() + slideOutVertically(
            targetOffsetY = { it / 3 },
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        // Semi-transparent overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.1f))
                .systemBarsPadding()
                .padding(16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClose
                )
        ) {
            // Top card showing the route
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)  // Make it 90% of screen width
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .clickable(enabled = false) { /* Prevent clicks from propagating */ },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = HealthRideColors.BackgroundWhite.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Header with title and close button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            "Route Preview",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = HealthRideColors.TextDark
                        )

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(HealthRideColors.TextLight.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close Preview",
                                tint = HealthRideColors.TextMedium,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Origin Info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(HealthRideColors.PrimaryBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.MyLocation,
                                contentDescription = null,
                                tint = HealthRideColors.PrimaryBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = "Origin",
                                style = MaterialTheme.typography.labelMedium,
                                color = HealthRideColors.TextMedium
                            )

                            Text(
                                text = originAddress,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = HealthRideColors.TextDark,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Line connector with dots
                    Row(
                        modifier = Modifier.padding(start = 24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.height(32.dp)
                        ) {
                            // Animated dotted line
                            Canvas(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(32.dp)
                            ) {
                                val pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(5f, 5f),
                                    0f
                                )

                                drawLine(
                                    color = HealthRideColors.TextLight,
                                    start = Offset(1f, 0f),
                                    end = Offset(1f, size.height),
                                    strokeWidth = 2f,
                                    pathEffect = pathEffect
                                )
                            }
                        }
                    }

                    // Destination Info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(HealthRideColors.PrimaryPurple.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Place,
                                contentDescription = null,
                                tint = HealthRideColors.PrimaryPurple,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = "Destination",
                                style = MaterialTheme.typography.labelMedium,
                                color = HealthRideColors.TextMedium
                            )

                            Text(
                                text = destinationAddress,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = HealthRideColors.TextDark,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Estimated info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Distance
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Distance",
                                style = MaterialTheme.typography.labelMedium,
                                color = HealthRideColors.TextMedium
                            )

                            Text(
                                text = "5.2 miles",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = HealthRideColors.TextDark
                            )
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .height(24.dp)
                                .width(1.dp)
                                .background(HealthRideColors.TextLight.copy(alpha = 0.2f))
                                .align(Alignment.CenterVertically)
                        )

                        // Time
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Est. Time",
                                style = MaterialTheme.typography.labelMedium,
                                color = HealthRideColors.TextMedium
                            )

                            Text(
                                text = "18 min",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = HealthRideColors.TextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Confirm Button
                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HealthRideColors.PrimaryBlue
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            "Continue to Booking",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingAndAppointmentHeader(
    greeting: String,
    userName: String,
    rideDate: String,
    rideTime: String,
    destination: String
) {
    // Animation for smooth appearance
    val animatedAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .graphicsLayer {
                alpha = animatedAlpha.value
                translationY = (1 - animatedAlpha.value) * 15f
            }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = HealthRideColors.PrimaryBlue.copy(alpha = 0.04f),
                ambientColor = HealthRideColors.PrimaryPurple.copy(alpha = 0.04f)
            ),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = HealthRideColors.BackgroundWhite),
        border = BorderStroke(1.dp, HealthRideColors.SurfaceLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Column (Greeting)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = HealthRideColors.PrimaryBlue
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = HealthRideColors.TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Sleek Divider
            Box(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .width(3.dp)
                    .height(30.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                HealthRideColors.TextLight.copy(alpha = 0.3f),
                                HealthRideColors.TextLight.copy(alpha = 0.6f),
                                HealthRideColors.TextLight.copy(alpha = 0.3f),
                            )
                        )
                    )
            )

            // Right Column (Appointment Details)
            Column(
                modifier = Modifier.weight(1.8f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Event,
                        contentDescription = "Appointment",
                        tint = HealthRideColors.TextMedium,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Next Appointment",
                        style = MaterialTheme.typography.labelMedium,
                        color = HealthRideColors.TextMedium
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "$rideDate • $rideTime",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = HealthRideColors.PrimaryPurple
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = destination.ifBlank { "Appointment Location" },
                    style = MaterialTheme.typography.bodySmall,
                    color = HealthRideColors.TextMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernHomeScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    // Initialize theme and system utilities
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val destinationFocusRequester = remember { FocusRequester() }
    val density = LocalDensity.current

    // -- STATE MANAGEMENT --
    // User and location state
    LaunchedEffect(Unit) { userViewModel.loadCurrentUser() }
    val currentUser by userViewModel.currentUser.observeAsState()
    val upcomingRides = SampleData.upcomingRides
    val nextRide = upcomingRides.minByOrNull { ride -> ride.dateTime }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    var currentUserLocation by remember { mutableStateOf<LatLng?>(null) }
    val defaultLocation = remember { LatLng(40.7128, -74.0060) } // NYC default

    // Search and UI state
    var isSearchActive by remember { mutableStateOf(false) }
    var originText by remember { mutableStateOf("Current Location") }
    var destinationText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var isDestinationFocused by remember { mutableStateOf(false) }
    var showRecentPlaces by remember { mutableStateOf(true) }
    var showAppointmentTypeDialog by remember { mutableStateOf(false) }

    // Route preview state
    var showRoutePreview by remember { mutableStateOf(false) }
    var previewOrigin by remember { mutableStateOf("") }
    var previewDestination by remember { mutableStateOf("") }

    // Booking flow state
    var showBookingModal by remember { mutableStateOf(false) }
    var bookingStep by remember { mutableStateOf(0) }
    val maxBookingSteps = 5

    // Bottom sheet state with enhanced animation
    var sheetDragProgress by remember { mutableStateOf(0f) }
    val minSheetHeight = 0.45f
    val maxSheetHeight = 0.9f
    val sheetAnimatedProgress = animateFloatAsState(
        targetValue = sheetDragProgress,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "SheetAnimProgress"
    )

    // Default heights for each step
    val defaultStepHeights = remember {
        mapOf(
            0 to 0.5f, // Trip Details (now smaller since the route card is gone)
            1 to 0.7f, // Ride Type
            2 to 0.75f, // Date & Time
            3 to 0.6f,  // Requirements
            4 to 0.75f  // Confirmation
        )
    }

    // Current sheet height based on step or user drag with smoother transitions
    val sheetHeightFraction = remember(bookingStep, sheetAnimatedProgress.value) {
        if (sheetAnimatedProgress.value > 0) {
            minSheetHeight + (maxSheetHeight - minSheetHeight) * sheetAnimatedProgress.value
        } else {
            defaultStepHeights[bookingStep] ?: 0.6f
        }
    }

    // Form state for the booking process
    var modalPickupLocation by remember { mutableStateOf("") }
    var modalDestination by remember { mutableStateOf("") }
    val currentDateTime = LocalDateTime.now()
    val minDateTime = currentDateTime.plusHours(7)
    var selectedDate by remember { mutableStateOf(Date.from(minDateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant())) }
    var selectedTime by remember { mutableStateOf(Date.from(minDateTime.toLocalTime().atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant())) }
    var specialNotes by remember { mutableStateOf("") }
    var bringCompanion by remember { mutableStateOf(false) }
    var selectedRideType by remember { mutableStateOf(RideType.AMBULATORY) }
    var selectedAppointmentType by remember { mutableStateOf<String?>(null) }
    var showAppointmentDropdown by remember { mutableStateOf(false) }

    // Recently visited places
    val recentDestinations = remember {
        listOf(
            Triple("Memorial Hospital", "3 days ago", Icons.Outlined.LocalHospital),
            Triple("City Health Clinic", "Last week", Icons.Outlined.Healing),
            Triple("Downtown Medical Center", "2 weeks ago", Icons.Outlined.MedicalServices)
        )
    }

    // Loading and success indicators
    var isBookingLoading by remember { mutableStateOf(false) }
    var showBookingSuccessDialog by remember { mutableStateOf(false) }

    // Info dialog state
    var showRideTypeInfo by remember { mutableStateOf(false) }
    var infoRideType by remember { mutableStateOf(RideType.AMBULATORY) }

    // -- MAP STATE --
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }
    val mapProperties by remember(hasLocationPermission) {
        mutableStateOf(MapProperties(
            isMyLocationEnabled = hasLocationPermission,
            maxZoomPreference = 20f,
            minZoomPreference = 3f,
            mapStyleOptions = null // Can be customized with JSON style
        ))
    }
    val mapUiSettings by remember {
        mutableStateOf(MapUiSettings(
            compassEnabled = false,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false,
            zoomControlsEnabled = false,
            rotationGesturesEnabled = true,
            tiltGesturesEnabled = true
        ))
    }

    // -- PERMISSIONS & LOCATION --
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions.values.any { it }
    }
    val fusedLocationClient: FusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // List of medical appointment types
    val appointmentTypes = remember {
        listOf(
            "Accident Scene to Emergency Room",
            "Airport to Hospital",
            "Airport to Hotel (Medical Stay)",
            "Airport to Rehabilitation Center",
            "Airport to Residence (Post-Treatment)",
            "Ambulance Transfer Between Hospitals",
            "Annual Physical",
            "Assisted Living Facility to Appointment",
            "Assisted Living Facility to Hospital",
            "Blood Transfusion Appointment",
            "Cancer Center to Home",
            "Cardiology",
            "Chemotherapy",
            "Childcare Facility to Doctor's Office",
            "Chiropractor Appointment",
            "Community Event Medical Support Transport",
            "Dental Appointment",
            "Dermatology",
            "Dialysis",
            "Doctor's Office to Home",
            "Doctor's Office to Specialist",
            "Drug Rehabilitation Center Appointment",
            "Emergency Room to Home",
            "Emergency Room to Inpatient Care",
            "Emergency Room to Rehabilitation Center",
            "Endocrinology",
            "ENT (Ear, Nose, and Throat) Appointment",
            "Eye Exam",
            "Fall at Home Requiring Medical Attention",
            "Fitness Center Injury Requiring Medical Attention",
            "Gastroenterology",
            "Group Home to Medical Appointment",
            "Gym Injury Requiring Medical Attention",
            "Health Screening Event Transport",
            "Home to Appointment",
            "Home to Dialysis",
            "Home to Hospital (Emergency)",
            "Home to Hospital (Scheduled Admission)",
            "Home to Lab Work",
            "Home to Mental Health Counseling",
            "Home to Pharmacy",
            "Home to Physical Therapy",
            "Home to Post-Operative Checkup",
            "Home to Pre-Operative Consult",
            "Home to Radiation Therapy",
            "Home to Specialist Visit",
            "Hospice Facility to Appointment",
            "Hospice Facility to Hospital",
            "Hospital Discharge",
            "Hospital to Airport",
            "Hospital to Assisted Living Facility",
            "Hospital to Home",
            "Hospital to Long-Term Care Facility",
            "Hospital to Rehabilitation Center",
            "Imaging (CT Scan)",
            "Imaging (MRI)",
            "Imaging (PET Scan)",
            "Imaging (Ultrasound)",
            "Imaging (X-Ray)",
            "Inpatient Rehabilitation to Home",
            "Lab Work / Blood Draw",
            "Long-Term Care Facility to Appointment",
            "Long-Term Care Facility to Hospital",
            "Medical Equipment Delivery/Pickup",
            "Medical Supply Store Visit",
            "Mental Health / Counseling",
            "Nursing Home to Appointment",
            "Nursing Home to Hospital",
            "Occupational Therapy",
            "Oncology",
            "Ophthalmology",
            "Orthodontist Appointment",
            "Orthopedics",
            "Pain Management Clinic Visit",
            "Pharmacy Pickup",
            "Physical Therapy",
            "Podiatrist Appointment",
            "Post-Operative Checkup",
            "Pre-Operative Consult",
            "Primary Care Visit",
            "Psychiatrist Appointment",
            "Pulmonology",
            "Radiation Therapy",
            "Recreational Area Injury Requiring Medical Attention",
            "Rehabilitation Center to Appointment",
            "Rehabilitation Center to Home",
            "School Nurse to Doctor's Office",
            "School Nurse to Home",
            "School Nurse to Hospital",
            "Skilled Nursing Facility to Appointment",
            "Skilled Nursing Facility to Hospital",
            "Social Event Injury Requiring Medical Attention",
            "Specialist Visit",
            "Speech Therapy",
            "Sports Arena Injury Requiring Medical Attention",
            "Substance Abuse Treatment Center Appointment",
            "Urgent Care Visit",
            "Urology",
            "Vaccination Appointment",
            "Workplace Injury Requiring Medical Attention", "Other Medical Appointment"
        )
    }

    // Request location permissions if not already granted
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    // Get current location when permission is granted
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                @SuppressLint("MissingPermission")
                val locationResult = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).await()

                locationResult?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentUserLocation = latLng
                    val fetchedAddress = "4209 Penelope Pl NE" // TODO: Geocode
                    originText = fetchedAddress
                    modalPickupLocation = fetchedAddress

                    // Smooth camera animation to current location
                    cameraPositionState.animate(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(latLng)
                                .zoom(15f)
                                .build()
                        ),
                        1000
                    )
                }
            } catch (e: Exception) {
                Log.e("ModernHomeScreen", "Error getting location", e)
            }
        }
    }

    // Search logic with debounce
    LaunchedEffect(destinationText) {
        if (destinationText.length >= 2 && isDestinationFocused) {
            delay(300) // Debounce for smoother UX

            // MOCK SEARCH RESULTS - In real app, call API here
            searchResults = listOf(
                AutocompletePrediction(description = "$destinationText Medical Center", place_id = "place1"),
                AutocompletePrediction(description = "$destinationText Hospital", place_id = "place2"),
                AutocompletePrediction(description = "$destinationText Clinic", place_id = "place3"),
                AutocompletePrediction(description = "$destinationText Healthcare", place_id = "place4")
            )
        } else {
            searchResults = emptyList()
        }
    }

    // Reset drag progress when step changes
    LaunchedEffect(bookingStep, showBookingModal) {
        if (!showBookingModal) {
            sheetDragProgress = 0f
        }
    }

    // Control search bar visibility
    var shouldShowSearchBar by remember { mutableStateOf(true) }

    // Effect to hide search bar when booking modal is visible
    LaunchedEffect(showBookingModal, showRoutePreview) {
        shouldShowSearchBar = !showBookingModal && !showRoutePreview

        if (showBookingModal || showRoutePreview) {
            isSearchActive = false
            showRecentPlaces = false
        }
    }

    // Dynamic greeting message
    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        in 18..21 -> "Good evening"
        else -> "Hello"
    }

    // -- MAIN UI LAYOUT --
    Box(modifier = Modifier.fillMaxSize()) {
        // Map base layer
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
            onMapLoaded = {
                // Map loaded callback
                Log.d("HealthRide", "Map loaded successfully")
            }
        )

        // Overlay for gradient scrim at top and bottom for better text visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.1f)
                        ),
                        startY = 0f,
                        endY = 2000f
                    )
                )
        )

        if (showRoutePreview) {
            RoutePreviewOverlay(
                isVisible = showRoutePreview,
                originAddress = previewOrigin,
                destinationAddress = previewDestination,
                onConfirm = {
                    showRoutePreview = false
                    modalPickupLocation = previewOrigin
                    modalDestination = previewDestination
                    bookingStep = 0
                    showBookingModal = true
                },
                onClose = {
                    showRoutePreview = false
                    destinationText = ""
                }
            )
        }
// Main Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 12.dp)
                .imePadding()
        ) {
            AnimatedVisibility(visible = !isSearchActive && !showRoutePreview && !showBookingModal) { // <-- ADD THIS LINE
                GreetingAndAppointmentHeader(
                    greeting = greeting, // your existing greeting text (e.g., "Good morning")
                    userName = currentUser?.firstName ?: "there",
                    rideDate = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(nextRide?.dateTime ?: Date()),
                    rideTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(nextRide?.dateTime ?: Date()),
                    destination = nextRide?.destinationAddress ?: "Medical Appointment"
                )
            } // <-- ADD THIS LINE


            // Search Area
            AnimatedVisibility(
                visible = shouldShowSearchBar,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = if (isSearchActive) 8.dp else 16.dp)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                ) {
                    val searchCardShape = if (!isSearchActive) RoundedCornerShape(75.dp) else RoundedCornerShape(24.dp) // Or 32.dp if you preferred that for expanded
                    Card(
                        shape = searchCardShape, // <-- USE THE DYNAMIC SHAPE HERE
                        colors = CardDefaults.cardColors(
                            containerColor = HealthRideColors.BackgroundWhite.copy(alpha = 0.95f),
                            contentColor = HealthRideColors.TextDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                            .shadow(
                                elevation = if (isSearchActive) 8.dp else 4.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = HealthRideColors.PrimaryBlue.copy(alpha = 0.1f),
                                spotColor = HealthRideColors.PrimaryBlue.copy(alpha = 0.1f)
                            )
                            .animateContentSize()
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                            // Initial Search Bar Look (Collapsed state)
                            AnimatedVisibility(
                                visible = !isSearchActive,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            isSearchActive = true
                                            coroutineScope.launch {
                                                delay(50)
                                                destinationFocusRequester.requestFocus()
                                            }
                                        }
                                        .padding(vertical = 16.dp, horizontal = 8.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.Search,
                                        contentDescription = "Search",
                                        tint = HealthRideColors.PrimaryPurple,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Spacer(Modifier.width(16.dp))

                                    Text(
                                        "Where would you like to go?",
                                        color = HealthRideColors.TextMedium,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }

                            // Expanded Search Fields (Expanded state)
                            AnimatedVisibility(
                                visible = isSearchActive,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column {
                                    // Origin Field
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.linearGradient(
                                                        colors = listOf(
                                                            HealthRideColors.PrimaryBlue.copy(alpha = 0.15f),
                                                            HealthRideColors.PrimaryBlue.copy(alpha = 0.05f)
                                                        )
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Rounded.MyLocation,
                                                contentDescription = null,
                                                tint = HealthRideColors.PrimaryBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        TextField(
                                            value = originText,
                                            onValueChange = { originText = it },
                                            placeholder = { Text("Origin") },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent,
                                                disabledContainerColor = Color.Transparent,
                                                focusedIndicatorColor = Color.Transparent,
                                                unfocusedIndicatorColor = Color.Transparent,
                                                cursorColor = HealthRideColors.PrimaryBlue
                                            ),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Medium,
                                                color = HealthRideColors.TextDark
                                            )
                                        )
                                    }

                                    // Subtle divider
                                    Divider(
                                        modifier = Modifier
                                            .padding(vertical = 4.dp, horizontal = 8.dp)
                                            .alpha(0.5f),
                                        color = HealthRideColors.TextLight.copy(alpha = 0.5f),
                                        thickness = 1.dp
                                    )

                                    // Destination Field
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.linearGradient(
                                                        colors = listOf(
                                                            HealthRideColors.PrimaryPurple.copy(alpha = 0.15f),
                                                            HealthRideColors.PrimaryPurple.copy(alpha = 0.05f)
                                                        )
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Rounded.Place,
                                                contentDescription = null,
                                                tint = HealthRideColors.PrimaryPurple,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        TextField(
                                            value = destinationText,
                                            onValueChange = { destinationText = it },
                                            placeholder = {
                                                Text(
                                                    "Where to?",
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        color = HealthRideColors.TextMedium
                                                    )
                                                )
                                            },
                                            singleLine = true,
                                            modifier = Modifier
                                                .weight(1f)
                                                .focusRequester(destinationFocusRequester)
                                                .onFocusChanged { isDestinationFocused = it.isFocused },
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent,
                                                disabledContainerColor = Color.Transparent,
                                                focusedIndicatorColor = Color.Transparent,
                                                unfocusedIndicatorColor = Color.Transparent,
                                                cursorColor = HealthRideColors.PrimaryPurple
                                            ),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Medium,
                                                color = HealthRideColors.TextDark
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onDone = {
                                                    if (destinationText.isNotBlank()) {
                                                        modalPickupLocation = originText
                                                        modalDestination = destinationText
                                                        bookingStep = 0
                                                        showBookingModal = true
                                                        focusManager.clearFocus()
                                                        isSearchActive = false
                                                        searchResults = emptyList()
                                                    } else {
                                                        focusManager.clearFocus()
                                                        isSearchActive = false
                                                    }
                                                }
                                            ),
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                            trailingIcon = {
                                                if (destinationText.isNotEmpty()) {
                                                    IconButton(
                                                        onClick = {
                                                            destinationText = ""
                                                            searchResults = emptyList()
                                                        }
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Close,
                                                            contentDescription = "Clear",
                                                            tint = HealthRideColors.TextMedium
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Search Results Dropdown
                    AnimatedVisibility(
                        visible = isSearchActive && searchResults.isNotEmpty(),
                        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
                    ) {
                        Surface(
                            color = HealthRideColors.BackgroundWhite,
                            shape = RoundedCornerShape(
                                bottomStart = 28.dp,
                                bottomEnd = 28.dp
                            ),
                            tonalElevation = 2.dp,
                            shadowElevation = 8.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 320.dp)
                            ) {
                                items(
                                    items = searchResults,
                                    key = { it.place_id ?: UUID.randomUUID().toString() }
                                ) { result ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                previewOrigin = originText
                                                previewDestination = result.description ?: ""
                                                showRoutePreview = true
                                                focusManager.clearFocus()
                                                isSearchActive = false
                                                searchResults = emptyList()
                                            }
                                            .padding(horizontal = 20.dp, vertical = 16.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.linearGradient(
                                                        colors = listOf(
                                                            HealthRideColors.PrimaryPurple.copy(alpha = 0.1f),
                                                            HealthRideColors.PrimaryBlue.copy(alpha = 0.05f)
                                                        )
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Outlined.LocationOn,
                                                contentDescription = null,
                                                tint = HealthRideColors.PrimaryPurple,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Text(
                                            text = result.description ?: "",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = HealthRideColors.TextDark,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Icon(
                                            Icons.Default.KeyboardArrowRight,
                                            contentDescription = null,
                                            tint = HealthRideColors.TextLight,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    if (searchResults.indexOf(result) < searchResults.size - 1) {
                                        Divider(
                                            color = HealthRideColors.SurfaceLight,
                                            modifier = Modifier.padding(horizontal = 20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Recent Places Section
            AnimatedVisibility(
                visible = showRecentPlaces && !showBookingModal && !showRoutePreview,
                enter = fadeIn(initialAlpha = 0.3f) + expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                ),
                exit = fadeOut() + shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(durationMillis = 300)
                ),
                modifier = Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = HealthRideColors.BackgroundWhite,
                            contentColor = HealthRideColors.TextDark
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(28.dp),
                                ambientColor = HealthRideColors.PrimaryBlue.copy(alpha = 0.1f),
                                spotColor = HealthRideColors.PrimaryBlue.copy(alpha = 0.1f)
                            )
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Section Header with Icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(
                                    start = 24.dp,
                                    end = 24.dp,
                                    top = 20.dp,
                                    bottom = 8.dp
                                )
                            ) {
                                Icon(
                                    Icons.Outlined.History,
                                    contentDescription = null,
                                    tint = HealthRideColors.PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = "Recent Places",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.3).sp
                                    ),
                                    color = HealthRideColors.TextDark
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                TextButton(
                                    onClick = { /* View all destinations */ },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = HealthRideColors.PrimaryBlue
                                    )
                                ) {
                                    Text(
                                        "View All",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            // Recent Destinations List
                            recentDestinations.forEachIndexed { index, (destination, time, icon) ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            previewOrigin = originText
                                            previewDestination = destination
                                            showRoutePreview = true
                                            focusManager.clearFocus()
                                            isSearchActive = false
                                            searchResults = emptyList()
                                        }
                                        .padding(horizontal = 24.dp, vertical = 16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        HealthRideColors.PrimaryBlue.copy(alpha = 0.1f),
                                                        HealthRideColors.PrimaryPurple.copy(alpha = 0.05f)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = HealthRideColors.PrimaryBlue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 16.dp)
                                    ) {
                                        Text(
                                            text = destination,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = HealthRideColors.TextDark,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = time,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = HealthRideColors.TextMedium
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(HealthRideColors.PrimaryBlue.copy(alpha = 0.05f))
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = HealthRideColors.PrimaryBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                if (index < recentDestinations.size - 1) {
                                    Divider(
                                        modifier = Modifier.padding(horizontal = 24.dp),
                                        color = HealthRideColors.SurfaceLight
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
        }

        // Hide search bar when booking modal is visible
        LaunchedEffect(showBookingModal) {
            if (showBookingModal) {
                isSearchActive = false
            }
        }

        // Booking Modal
        AnimatedVisibility(
            visible = showBookingModal,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(durationMillis = 200)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                color = HealthRideColors.BackgroundWhite,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(sheetHeightFraction)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        spotColor = Color.Black.copy(alpha = 0.1f),
                        ambientColor = Color.Black.copy(alpha = 0.05f)
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Drag Handle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 8.dp)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { },
                                    onDragEnd = { },
                                    onDragCancel = { },
                                    onDrag = { change, dragAmount ->
                                        change.consumeAllChanges()
                                        val dragDelta = -dragAmount.y / 500f
                                        sheetDragProgress = (sheetDragProgress + dragDelta)
                                            .coerceIn(0f, 1f)
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(5.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            HealthRideColors.PrimaryBlue.copy(alpha = 0.2f),
                                            HealthRideColors.PrimaryPurple.copy(alpha = 0.2f)
                                        )
                                    )
                                )
                        )
                    }

                    // Close button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ){
                        IconButton(
                            onClick = {
                                showBookingModal = false
                                bookingStep = 0
                                destinationText = ""
                                selectedAppointmentType = null
                                sheetDragProgress = 0f
                            }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close Booking",
                                tint = HealthRideColors.TextMedium
                            )
                        }
                    }

                    // Step Indicator
                    StepIndicator(
                        currentStep = bookingStep,
                        totalSteps = maxBookingSteps,
                        titles = listOf("Trip Details", "Ride Type", "Date & Time", "Requirements", "Confirm"),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    // Content based on current step
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Crossfade(
                            targetState = bookingStep,
                            animationSpec = tween(300),
                            label = "BookingStepCrossfade"
                        ) { step ->
                            Box(
                                modifier = Modifier.padding(
                                    horizontal = 24.dp,
                                    vertical = 18.dp
                                )
                            ) {
                                when (step) {
                                    0 -> TripDetailsStep(
                                        pickupAddress = modalPickupLocation,
                                        destinationAddress = modalDestination,
                                        selectedAppointmentType = selectedAppointmentType,
                                        onAppointmentTypeSelect = { selectedAppointmentType = it },
                                        appointmentTypes = appointmentTypes,
                                        showAppointmentDropdown = showAppointmentDropdown,
                                        onShowAppointmentDropdown = { showAppointmentDropdown = it }
                                    )
                                    1 -> RideTypeStep(
                                        selectedRideType = selectedRideType,
                                        onRideTypeChange = { rideType: RideType -> selectedRideType = rideType },
                                        onRideTypeInfoClick = { type: RideType ->
                                            infoRideType = type
                                            showRideTypeInfo = true
                                        }
                                    )
                                    2 -> EnhancedDateTimeStep(
                                        selectedDate = selectedDate,
                                        onDateChange = { newDate: Date -> selectedDate = newDate },
                                        selectedTime = selectedTime,
                                        onTimeChange = { newTime: Date -> selectedTime = newTime },
                                        minimumDateTime = minDateTime
                                    )
                                    3 -> EnhancedSpecialRequirementsStep(
                                        bringCompanion = bringCompanion,
                                        onBringCompanionChange = { newValue: Boolean -> bringCompanion = newValue },
                                        specialNotes = specialNotes,
                                        onSpecialNotesChange = { newNotes: String -> specialNotes = newNotes }
                                    )
                                    4 -> EnhancedConfirmationStep(
                                        pickupAddress = modalPickupLocation,
                                        destinationAddress = modalDestination,
                                        selectedDate = selectedDate,
                                        selectedTime = selectedTime,
                                        bringCompanion = bringCompanion,
                                        selectedRideType = selectedRideType,
                                        specialNotes = specialNotes,
                                        appointmentType = selectedAppointmentType ?: "Medical Appointment"
                                    )
                                }
                            }
                        }
                    }

                    // Navigation Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalArrangement = if (bookingStep > 0) Arrangement.SpaceBetween else Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back button (only shown after first step)
                        if (bookingStep > 0) {
                            OutlinedButton(
                                onClick = { bookingStep-- },
                                shape = RoundedCornerShape(30.dp),
                                border = BorderStroke(
                                    width = 1.5.dp,
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            HealthRideColors.PrimaryBlue.copy(alpha = 0.5f),
                                            HealthRideColors.PrimaryPurple.copy(alpha = 0.5f)
                                        )
                                    )
                                ),
                                modifier = Modifier.height(56.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp)
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = null,
                                    tint = HealthRideColors.PrimaryBlue,
                                    modifier = Modifier.size(18.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    "Back",
                                    color = HealthRideColors.PrimaryBlue,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        // Next/Book button with conditional states
                        val isNextEnabled = when (bookingStep) {
                            0 -> modalPickupLocation.isNotBlank() &&
                                    modalDestination.isNotBlank() &&
                                    selectedAppointmentType != null
                            else -> true
                        }

                        Button(
                            onClick = {
                                if (bookingStep < maxBookingSteps - 1) {
                                    bookingStep++
                                } else {
                                    // Handle final booking submission
                                    // Use dummy data for combined date and time instead of combineDateAndTime function
                                    val cal = Calendar.getInstance().apply {
                                        time = selectedDate
                                        val timeCalendar = Calendar.getInstance().apply { time = selectedTime }
                                        set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                                        set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    val rideDateTime = cal.time

                                    val passengerCount = if (bringCompanion) 2 else 1
                                    val rideToBook = Ride(
                                        pickupAddress = modalPickupLocation,
                                        destinationAddress = modalDestination,
                                        dateTime = rideDateTime,
                                        passengerName = currentUser?.fullName ?: "User",
                                        numberOfPassengers = passengerCount,
                                        rideType = selectedRideType.name,
                                        specialInstructions = specialNotes.takeIf { it.isNotBlank() },
                                        appointmentType = selectedAppointmentType ?: "Medical Appointment"
                                    )
                                    Log.d("BookingModal", "Ride object to save: $rideToBook")

                                    isBookingLoading = true
                                    coroutineScope.launch {
                                        delay(1500) // Simulate network request
                                        // TODO: Replace with actual RideViewModel/Repository call
                                        isBookingLoading = false
                                        showBookingSuccessDialog = true
                                        Log.d("BookingModal", "Booking complete.")
                                        delay(2000)
                                        showBookingSuccessDialog = false
                                        showBookingModal = false
                                        bookingStep = 0 // Reset step for next time
                                        destinationText = "" // Clear destination text
                                        selectedAppointmentType = null
                                        // Optional: Navigate to rides screen
                                        // navController.navigate("rides")
                                    }
                                }
                            },
                            enabled = isNextEnabled && !isBookingLoading,
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (bookingStep == maxBookingSteps - 1)
                                    HealthRideColors.PrimaryPurple
                                else
                                    HealthRideColors.PrimaryBlue,
                                contentColor = Color.White,
                                disabledContainerColor = HealthRideColors.TextLight.copy(alpha = 0.2f),
                                disabledContentColor = HealthRideColors.TextLight
                            ),
                            contentPadding = PaddingValues(horizontal = 32.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            if (bookingStep == maxBookingSteps - 1 && isBookingLoading) {
                                // Loading indicator
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                // Button text and icon
                                Text(
                                    if (bookingStep < maxBookingSteps - 1) "Continue" else "Book Ride",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Icon(
                                    imageVector = if (bookingStep < maxBookingSteps - 1)
                                        Icons.Default.ArrowForward
                                    else
                                        Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    } // End Modal Navigation Buttons

                } // End Modal Column
            } // End Modal Surface
        } // End Modal AnimatedVisibility

        if (isBookingLoading) {
            Dialog(
                onDismissRequest = { },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    HealthRideColors.BackgroundWhite.copy(alpha = 0.9f),
                                    HealthRideColors.BackgroundWhite.copy(alpha = 0.95f)
                                )
                            )
                        )
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = Color.Black.copy(alpha = 0.1f),
                            spotColor = Color.Black.copy(alpha = 0.05f)
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Animation values
                        val infiniteTransition = rememberInfiniteTransition(label = "loading")
                        val rotation = infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "rotation"
                        )

                        val pulseAnimation = infiniteTransition.animateFloat(
                            initialValue = 0.8f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulse"
                        )

                        // Animated loader
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .padding(bottom = 16.dp)
                        ) {
                            // Background ring
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawArc(
                                    color = HealthRideColors.TextLight.copy(alpha = 0.1f),
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }

                            // Animated progress ring
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        rotationZ = rotation.value
                                        scaleX = pulseAnimation.value
                                        scaleY = pulseAnimation.value
                                    }
                            ) {
                                drawArc(
                                    brush = Brush.sweepGradient(
                                        listOf(
                                            HealthRideColors.PrimaryBlue.copy(alpha = 0f),
                                            HealthRideColors.PrimaryBlue.copy(alpha = 0.5f),
                                            HealthRideColors.PrimaryPurple
                                        )
                                    ),
                                    startAngle = 0f,
                                    sweepAngle = 240f,
                                    useCenter = false,
                                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }

                            // Center icon
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DirectionsCar,
                                    contentDescription = null,
                                    tint = HealthRideColors.PrimaryBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Text(
                            text = "Booking Your Ride",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.sp
                            ),
                            color = HealthRideColors.TextDark
                        )
                    }
                }
            }
        }

        if (showBookingSuccessDialog) {
            Dialog(
                onDismissRequest = { showBookingSuccessDialog = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                val successAnimation = remember { Animatable(0f) }
                LaunchedEffect(Unit) {
                    successAnimation.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }

                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = HealthRideColors.BackgroundWhite
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .graphicsLayer {
                            scaleX = successAnimation.value
                            scaleY = successAnimation.value
                            alpha = successAnimation.value
                        }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        // Success icon with animation
                        val confettiAnimation = remember { Animatable(0f) }
                        LaunchedEffect(Unit) {
                            delay(400)
                            confettiAnimation.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .padding(bottom = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Success circle background
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            HealthRideColors.SuccessGreen.copy(alpha = 0.1f),
                                            HealthRideColors.SuccessGreen.copy(alpha = 0.05f)
                                        )
                                    ),
                                    radius = size.minDimension / 2
                                )
                            }

                            // Success checkmark
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = HealthRideColors.SuccessGreen,
                                modifier = Modifier
                                    .size(64.dp)
                                    .graphicsLayer {
                                        scaleX = confettiAnimation.value
                                        scaleY = confettiAnimation.value
                                    }
                            )

                            // Confetti-like particles (animated)
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        alpha = confettiAnimation.value
                                    }
                            ) {
                                val radius = size.minDimension / 2
                                val particles = 12
                                val particleRadius = 4.dp.toPx()

                                repeat(particles) { i ->
                                    val angle = (i * 360f / particles) * (Math.PI / 180f).toFloat()
                                    val x = center.x + radius * 0.8f * cos(angle)
                                    val y = center.y + radius * 0.8f * sin(angle)

                                    drawCircle(
                                        color = if (i % 3 == 0)
                                            HealthRideColors.PrimaryBlue
                                        else if (i % 3 == 1)
                                            HealthRideColors.PrimaryPurple
                                        else
                                            HealthRideColors.AccentTeal,
                                        radius = particleRadius,
                                        center = Offset(x, y)
                                    )
                                }
                            }
                        }

                        Text(
                            "Ride Booked!",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = HealthRideColors.TextDark
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Your ride has been scheduled successfully. We'll send you a reminder before your pickup time.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = HealthRideColors.TextMedium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { showBookingSuccessDialog = false },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HealthRideColors.PrimaryBlue
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text(
                                "Great!",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }

        if (showRideTypeInfo) {
            EnhancedRideTypeInfoDialog(
                rideType = infoRideType,
                onDismiss = { showRideTypeInfo = false }
            )
        }
    } // End Root Box
}