package com.example.healthride.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.healthride.RequestLocationPermission
import com.example.healthride.LoadingIndicator
import com.example.healthride.data.model.RideType
import com.example.healthride.ui.components.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.tasks.await
import java.time.format.DateTimeFormatter

@SuppressLint("MissingPermission")
@Composable
fun ModernHomeScreen(
    navController: NavController
) {
    // Request location permissions
    RequestLocationPermission()
    
    // Core state
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cancellationTokenSource = remember { CancellationTokenSource() }
    
    // UI state
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var pickupAddress by remember { mutableStateOf("") }
    var destinationAddress by remember { mutableStateOf("") }
    var pickupLocation by remember { mutableStateOf<LatLng?>(null) }
    var dropoffLocation by remember { mutableStateOf<LatLng?>(null) }
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    
    // Booking state
    var showBookingModal by remember { mutableStateOf(false) }
    var showRoutePreview by remember { mutableStateOf(false) }
    var showRecentPlaces by remember { mutableStateOf(true) }
    var showSearchResults by remember { mutableStateOf(false) }
    
    // Booking details
    var selectedRideType by remember { mutableStateOf(RideType.AMBULATORY) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf<String?>("9:00 AM") }
    var additionalNotes by remember { mutableStateOf("") }
    var isRoundTrip by remember { mutableStateOf(false) }
    var hasCompanion by remember { mutableStateOf(false) }
    var returnDate by remember { mutableStateOf(LocalDate.now()) }
    var returnTime by remember { mutableStateOf<String?>("5:00 PM") }
    var bookingStep by remember { mutableStateOf(0) }
    
    // Route information
    val distance = 8.5f // In a real app, these would be calculated based on the route
    val duration = 25
    val estimatedPrice = 32.50
    
    // Function to get current location
    val getCurrentLocation = {
        coroutineScope.launch {
            isLoadingLocation = true
            try {
                val locationResult = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).await()
                
                locationResult?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                    Log.d("HomeScreen", "Got location: ${it.latitude}, ${it.longitude}")
                }
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error getting location", e)
            } finally {
                isLoadingLocation = false
            }
        }
    }
    
    // Handle map loaded
    val onMapLoaded = {
        // Get current location when map is loaded if we have permission
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && currentLocation == null
        ) {
            getCurrentLocation()
        }
    }
    
    // Main UI Structure
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Main Map
        HealthRideMap(
            currentLocation = currentLocation,
            pickupLocation = pickupLocation,
            dropoffLocation = dropoffLocation,
            routePoints = routePoints,
            onMyLocationClick = { getCurrentLocation() },
            onMapClick = { /* Handle map click */ },
            onMapLoaded = onMapLoaded,
            modifier = Modifier.fillMaxSize()
        )
        
        // Loading indicator
        if (isLoadingLocation) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator(size = 60f)
            }
        }
        
        // Top search bar section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            // Search bar for pickup location
            AddressSearchBar(
                value = pickupAddress,
                onValueChange = { pickupAddress = it },
                placeholder = "Where are you starting from?",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Adjust,
                        contentDescription = null,
                        tint = HealthRideColors.PrimaryBlue
                    )
                },
                isActive = false,
                onClick = {
                    showRecentPlaces = false
                    showSearchResults = true
                },
                modifier = Modifier.shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Search bar for destination
            AddressSearchBar(
                value = destinationAddress,
                onValueChange = { destinationAddress = it },
                placeholder = "Where are you going?",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = null,
                        tint = HealthRideColors.PrimaryPurple
                    )
                },
                isActive = false,
                onClick = {
                    showRecentPlaces = false
                    showSearchResults = true
                },
                modifier = Modifier.shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            )
        }
        
        // Book Now button
        AnimatedVisibility(
            visible = !showBookingModal && !showRoutePreview && pickupAddress.isNotEmpty() && destinationAddress.isNotEmpty(),
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            GradientButton(
                text = "Book Now",
                onClick = { 
                    showBookingModal = true
                    showRecentPlaces = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
        }
        
        // Booking modal
        AnimatedVisibility(
            visible = showBookingModal,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Booking steps indicator
                    StepIndicator(
                        currentStep = bookingStep,
                        totalSteps = 4,
                        titles = listOf("Ride Type", "Date & Time", "Additional Info", "Confirm")
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Booking steps content
                    when (bookingStep) {
                        0 -> {
                            // Ride Type Selection
                            RideTypeSelection(
                                selectedType = selectedRideType,
                                onTypeSelected = { selectedRideType = it }
                            )
                        }
                        1 -> {
                            // Date & Time Selection
                            DateSelector(
                                selectedDate = selectedDate,
                                onDateSelected = { selectedDate = it }
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            TimeSelector(
                                availableTimes = listOf("9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", 
                                    "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM"),
                                selectedTime = selectedTime,
                                onTimeSelected = { selectedTime = it }
                            )
                        }
                        2 -> {
                            // Additional Info
                            AdditionalInfoSection(
                                notes = additionalNotes,
                                onNotesChange = { additionalNotes = it },
                                isRoundTrip = isRoundTrip,
                                onRoundTripChange = { isRoundTrip = it },
                                companion = hasCompanion,
                                onCompanionChange = { hasCompanion = it }
                            )
                            
                            ReturnTripSection(
                                isVisible = isRoundTrip,
                                selectedDate = returnDate,
                                selectedTime = returnTime,
                                onDateSelected = { returnDate = it },
                                onTimeSelected = { returnTime = it }
                            )
                        }
                        3 -> {
                            // Confirmation
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Ride Summary
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = HealthRideColors.BackgroundLight
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Ride Summary",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = HealthRideColors.TextDark
                                        )
                                        
                                        Spacer(modifier = Modifier.height(16.dp))
                                        
                                        // From/To
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Adjust,
                                                contentDescription = null,
                                                tint = HealthRideColors.PrimaryBlue
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = pickupAddress.ifEmpty { "Current Location" },
                                                color = HealthRideColors.TextDark
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Place,
                                                contentDescription = null,
                                                tint = HealthRideColors.PrimaryPurple
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = destinationAddress,
                                                color = HealthRideColors.TextDark
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(16.dp))
                                        
                                        // Date & Time
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.CalendarToday,
                                                contentDescription = null,
                                                tint = HealthRideColors.TextMedium
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMM d")) + 
                                                    " • " + (selectedTime ?: ""),
                                                color = HealthRideColors.TextDark
                                            )
                                        }
                                        
                                        if (isRoundTrip && returnTime != null) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Repeat,
                                                    contentDescription = null,
                                                    tint = HealthRideColors.TextMedium
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Return: " + returnDate.format(DateTimeFormatter.ofPattern("EEE, MMM d")) + 
                                                        " • " + returnTime,
                                                    color = HealthRideColors.TextDark
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Route preview map
                                if (pickupLocation != null && dropoffLocation != null) {
                                    RoutePreviewMap(
                                        pickupLocation = pickupLocation!!,
                                        dropoffLocation = dropoffLocation!!,
                                        routePoints = routePoints,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    RouteInfoSummary(
                                        distance = distance,
                                        duration = duration,
                                        estimatedPrice = estimatedPrice
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Back button (except for first step)
                        if (bookingStep > 0) {
                            OutlinedButton(
                                onClick = { bookingStep-- },
                                shape = CircleShape,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Back")
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        
                        // Next/Confirm button
                        GradientButton(
                            text = if (bookingStep < 3) "Next" else "Confirm Booking",
                            onClick = {
                                if (bookingStep < 3) {
                                    bookingStep++
                                } else {
                                    // Handle booking confirmation
                                    showBookingModal = false
                                    showRoutePreview = true
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        
        // Route preview with driver info
        AnimatedVisibility(
            visible = showRoutePreview,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Your Ride is Booked!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = HealthRideColors.TextDark
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "A driver will be assigned shortly",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HealthRideColors.TextMedium
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    RouteInfoSummary(
                        distance = distance,
                        duration = duration,
                        estimatedPrice = estimatedPrice
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { 
                                showRoutePreview = false 
                                showRecentPlaces = true
                            },
                            shape = CircleShape,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Close")
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Button(
                            onClick = { navController.navigate("rides") },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HealthRideColors.PrimaryBlue
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("View Rides")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = HealthRideColors.PrimaryBlue
        ),
        shape = CircleShape,
        modifier = modifier
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold
        )
    }
} 