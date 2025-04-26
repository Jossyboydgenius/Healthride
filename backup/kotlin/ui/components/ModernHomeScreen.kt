package com.example.healthride.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.healthride.ui.HealthRideColors
import com.example.healthride.ui.components.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

enum class BookingStep {
    LOCATION,
    RIDE_TYPE,
    SCHEDULE,
    ADDITIONAL_INFO,
    CONFIRM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernHomeScreen(
    navigateToRideHistory: () -> Unit,
    navigateToProfile: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    
    var currentStep by remember { mutableStateOf(BookingStep.LOCATION) }
    
    // Location state
    var pickupAddress by remember { mutableStateOf("") }
    var destinationAddress by remember { mutableStateOf("") }
    var pickupLocation by remember { mutableStateOf<LatLng?>(null) }
    var destinationLocation by remember { mutableStateOf<LatLng?>(null) }
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    
    // Ride type state
    var selectedRideType by remember { mutableStateOf<String?>(null) }
    
    // Schedule state
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    
    // Additional info state
    var notes by remember { mutableStateOf("") }
    var needsRoundTrip by remember { mutableStateOf(false) }
    var needsCompanion by remember { mutableStateOf(false) }
    var returnDate by remember { mutableStateOf<String?>(null) }
    var returnTime by remember { mutableStateOf<String?>(null) }
    
    // Mock data for map summary
    val distance = "8.2 mi"
    val duration = "25 min"
    val estimatedPrice = "$45.00"
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = HealthRideColors.BackgroundLight,
                drawerContentColor = HealthRideColors.TextDark
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Health Ride",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                
                Divider()
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = true,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text("Ride History") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            navigateToRideHistory()
                        }
                    }
                )
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            navigateToProfile()
                        }
                    }
                )
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Help, contentDescription = null) },
                    label = { Text("Help & Support") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Health Ride") },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = HealthRideColors.PrimaryBlue,
                        titleContentColor = HealthRideColors.TextLight,
                        navigationIconContentColor = HealthRideColors.TextLight
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Map view
                MapView(
                    pickupLocation = pickupLocation,
                    destinationLocation = destinationLocation,
                    routePoints = routePoints,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Booking Flow Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    // Show route summary if locations are selected
                    AnimatedVisibility(
                        visible = pickupLocation != null && destinationLocation != null && currentStep != BookingStep.LOCATION
                    ) {
                        RideSummary(
                            distance = distance,
                            duration = duration,
                            estimatedPrice = estimatedPrice,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 8.dp)
                        )
                    }
                    
                    // Main booking card
                    Card(
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = HealthRideColors.BackgroundLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            StepIndicator(
                                totalSteps = 5,
                                currentStep = currentStep.ordinal + 1,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Content based on current step
                            when (currentStep) {
                                BookingStep.LOCATION -> {
                                    Text(
                                        text = "Where are you going?",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    
                                    AddressSearchSection(
                                        pickupAddress = pickupAddress,
                                        onPickupAddressChange = { 
                                            pickupAddress = it
                                            // In a real app, this would trigger geocoding to get LatLng
                                            if (it.isNotEmpty()) {
                                                pickupLocation = LatLng(37.7749, -122.4194) // Mock location
                                            }
                                        },
                                        destinationAddress = destinationAddress,
                                        onDestinationAddressChange = { 
                                            destinationAddress = it
                                            // In a real app, this would trigger geocoding to get LatLng
                                            if (it.isNotEmpty()) {
                                                destinationLocation = LatLng(37.7833, -122.4167) // Mock location
                                                // Generate mock route points
                                                routePoints = listOf(
                                                    LatLng(37.7749, -122.4194),
                                                    LatLng(37.7780, -122.4180),
                                                    LatLng(37.7833, -122.4167)
                                                )
                                            }
                                        }
                                    )
                                }
                                
                                BookingStep.RIDE_TYPE -> {
                                    Text(
                                        text = "Select Ride Type",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    
                                    RideTypeSelection(
                                        selectedRideType = selectedRideType,
                                        onRideTypeSelected = { selectedRideType = it }
                                    )
                                }
                                
                                BookingStep.SCHEDULE -> {
                                    Text(
                                        text = "Schedule Your Ride",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    
                                    DateSelector(
                                        selectedDate = selectedDate,
                                        onDateSelected = { selectedDate = it }
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    TimeSelector(
                                        selectedTime = selectedTime,
                                        onTimeSelected = { selectedTime = it }
                                    )
                                }
                                
                                BookingStep.ADDITIONAL_INFO -> {
                                    Text(
                                        text = "Additional Information",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    
                                    AdditionalInfoSection(
                                        notes = notes,
                                        onNotesChange = { notes = it },
                                        needsRoundTrip = needsRoundTrip,
                                        onNeedsRoundTripChange = { needsRoundTrip = it },
                                        needsCompanion = needsCompanion,
                                        onNeedsCompanionChange = { needsCompanion = it }
                                    )
                                    
                                    AnimatedVisibility(visible = needsRoundTrip) {
                                        ReturnTripSection(
                                            selectedDate = returnDate,
                                            onDateSelected = { returnDate = it },
                                            selectedTime = returnTime,
                                            onTimeSelected = { returnTime = it }
                                        )
                                    }
                                }
                                
                                BookingStep.CONFIRM -> {
                                    Text(
                                        text = "Confirm Booking",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    
                                    // Booking summary
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = HealthRideColors.BackgroundMedium
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Ride Details",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = HealthRideColors.TextDark
                                            )
                                            
                                            Spacer(modifier = Modifier.height(8.dp))
                                            
                                            BookingSummaryItem(
                                                label = "From",
                                                value = pickupAddress
                                            )
                                            
                                            BookingSummaryItem(
                                                label = "To",
                                                value = destinationAddress
                                            )
                                            
                                            BookingSummaryItem(
                                                label = "Ride Type",
                                                value = selectedRideType ?: ""
                                            )
                                            
                                            BookingSummaryItem(
                                                label = "Date",
                                                value = selectedDate ?: ""
                                            )
                                            
                                            BookingSummaryItem(
                                                label = "Time",
                                                value = selectedTime ?: ""
                                            )
                                            
                                            if (needsRoundTrip) {
                                                Divider(
                                                    modifier = Modifier.padding(vertical = 8.dp)
                                                )
                                                
                                                Text(
                                                    text = "Return Trip",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = HealthRideColors.TextDark
                                                )
                                                
                                                Spacer(modifier = Modifier.height(8.dp))
                                                
                                                BookingSummaryItem(
                                                    label = "Date",
                                                    value = returnDate ?: ""
                                                )
                                                
                                                BookingSummaryItem(
                                                    label = "Time",
                                                    value = returnTime ?: ""
                                                )
                                            }
                                            
                                            Divider(
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "Total",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = HealthRideColors.TextDark
                                                )
                                                
                                                Text(
                                                    text = if (needsRoundTrip) {
                                                        "$${estimatedPrice.drop(1).toDouble() * 2}"
                                                    } else {
                                                        estimatedPrice
                                                    },
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = HealthRideColors.PrimaryBlue
                                                )
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    Button(
                                        onClick = {
                                            // Handle booking confirmation
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = HealthRideColors.PrimaryBlue
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Confirm Booking",
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Navigation buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        if (currentStep.ordinal > 0) {
                                            currentStep = BookingStep.values()[currentStep.ordinal - 1]
                                        }
                                    },
                                    enabled = currentStep.ordinal > 0,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = HealthRideColors.PrimaryBlue
                                    ),
                                    border = BorderStroke(1.dp, HealthRideColors.PrimaryBlue),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Back")
                                }
                                
                                Button(
                                    onClick = {
                                        if (currentStep.ordinal < BookingStep.values().size - 1) {
                                            currentStep = BookingStep.values()[currentStep.ordinal + 1]
                                        }
                                    },
                                    enabled = when (currentStep) {
                                        BookingStep.LOCATION -> pickupAddress.isNotEmpty() && destinationAddress.isNotEmpty()
                                        BookingStep.RIDE_TYPE -> selectedRideType != null
                                        BookingStep.SCHEDULE -> selectedDate != null && selectedTime != null
                                        BookingStep.ADDITIONAL_INFO -> true
                                        BookingStep.CONFIRM -> false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = HealthRideColors.PrimaryBlue
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (currentStep.ordinal < BookingStep.values().size - 1) "Next" else "Confirm",
                                        modifier = Modifier.padding(horizontal = 24.dp)
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

@Composable
fun BookingSummaryItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = HealthRideColors.TextMedium,
            style = MaterialTheme.typography.bodyMedium
        )
        
        Text(
            text = value,
            color = HealthRideColors.TextDark,
            style = MaterialTheme.typography.bodyMedium
        )
    }
} 