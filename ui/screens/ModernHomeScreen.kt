package com.example.healthride.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthride.ui.HealthRideColors
import com.example.healthride.ui.components.*
import java.time.LocalDate

enum class BookingStep {
    RideType,
    Schedule,
    Details,
    Confirm
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernHomeScreen(
    navigateToRideDetails: (String) -> Unit,
    navigateToProfile: () -> Unit
) {
    var currentBookingStep by remember { mutableStateOf(BookingStep.RideType) }
    var isBookingMode by remember { mutableStateOf(false) }
    
    // Booking state variables
    var selectedRideType by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf("10:00 AM") }
    var selectedReturnDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedReturnTime by remember { mutableStateOf("4:00 PM") }
    var specialInstructions by remember { mutableStateOf("") }
    var isRoundTrip by remember { mutableStateOf(false) }
    var needsCompanion by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBookingMode) "Book a Ride" else "Health Ride",
                        color = HealthRideColors.TextDark,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (isBookingMode) {
                        IconButton(onClick = { isBookingMode = false }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = HealthRideColors.TextDark
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navigateToProfile() }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = HealthRideColors.TextDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HealthRideColors.SurfaceColor
                )
            )
        },
        bottomBar = {
            if (isBookingMode) {
                BottomAppBar(
                    containerColor = HealthRideColors.SurfaceColor
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                if (currentBookingStep != BookingStep.RideType) {
                                    currentBookingStep = BookingStep.values()[currentBookingStep.ordinal - 1]
                                } else {
                                    isBookingMode = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HealthRideColors.BackgroundLight
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (currentBookingStep == BookingStep.RideType) "Cancel" else "Previous",
                                color = HealthRideColors.TextDark
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Button(
                            onClick = {
                                if (currentBookingStep != BookingStep.Confirm) {
                                    currentBookingStep = BookingStep.values()[currentBookingStep.ordinal + 1]
                                } else {
                                    // Submit booking and return to home
                                    isBookingMode = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HealthRideColors.PrimaryBlue
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (currentBookingStep == BookingStep.Confirm) "Submit" else "Next"
                            )
                        }
                    }
                }
            } else {
                BottomAppBar(
                    containerColor = HealthRideColors.SurfaceColor
                ) {
                    NavigationBar(
                        containerColor = HealthRideColors.SurfaceColor
                    ) {
                        NavigationBarItem(
                            selected = true,
                            onClick = { },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home") }
                        )
                        
                        NavigationBarItem(
                            selected = false,
                            onClick = { },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "My Rides"
                                )
                            },
                            label = { Text("My Rides") }
                        )
                        
                        NavigationBarItem(
                            selected = false,
                            onClick = { },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications"
                                )
                            },
                            label = { Text("Notifications") }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (!isBookingMode) {
                FloatingActionButton(
                    onClick = { isBookingMode = true },
                    containerColor = HealthRideColors.PrimaryBlue,
                    contentColor = HealthRideColors.SurfaceColor
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Book a Ride"
                    )
                }
            }
        }
    ) { paddingValues ->
        if (isBookingMode) {
            BookingContent(
                modifier = Modifier.padding(paddingValues),
                currentStep = currentBookingStep.ordinal + 1,
                selectedRideType = selectedRideType,
                onRideTypeSelected = { selectedRideType = it },
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
                selectedTime = selectedTime,
                onTimeSelected = { selectedTime = it },
                selectedReturnDate = selectedReturnDate,
                onReturnDateSelected = { selectedReturnDate = it },
                selectedReturnTime = selectedReturnTime,
                onReturnTimeSelected = { selectedReturnTime = it },
                specialInstructions = specialInstructions,
                onSpecialInstructionsChange = { specialInstructions = it },
                isRoundTrip = isRoundTrip,
                onRoundTripChange = { isRoundTrip = it },
                needsCompanion = needsCompanion, 
                onNeedsCompanionChange = { needsCompanion = it },
                currentBookingStep = currentBookingStep
            )
        } else {
            HomeContent(
                modifier = Modifier.padding(paddingValues),
                navigateToRideDetails = navigateToRideDetails
            )
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    navigateToRideDetails: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HealthRideColors.BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        HomeHeader()
        
        UpcomingRidesSection(navigateToRideDetails = navigateToRideDetails)
        
        ServicesSection()
        
        InsuranceVerificationSection()
    }
}

@Composable
fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Welcome back, John!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = HealthRideColors.TextDark
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Where would you like to go today?",
                style = MaterialTheme.typography.bodyMedium,
                color = HealthRideColors.TextMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SearchBar()
        }
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = { },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        placeholder = { Text("Search for destinations") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = HealthRideColors.TextMedium
            )
        },
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = HealthRideColors.SurfaceColor,
            unfocusedBorderColor = HealthRideColors.DividerColor,
            focusedBorderColor = HealthRideColors.PrimaryBlue
        )
    )
}

@Composable
fun UpcomingRidesSection(
    navigateToRideDetails: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Upcoming Rides",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = HealthRideColors.TextDark
            )
            
            TextButton(onClick = { }) {
                Text(
                    text = "View All",
                    color = HealthRideColors.PrimaryBlue
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navigateToRideDetails("ride123") },
            colors = CardDefaults.cardColors(
                containerColor = HealthRideColors.SurfaceColor
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Today, 2:30 PM",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = HealthRideColors.TextDark
                    )
                    
                    StatusChip(status = "Confirmed")
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        LocationItem(
                            icon = Icons.Default.Circle,
                            title = "Pick up",
                            address = "123 Main St, City"
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        LocationItem(
                            icon = Icons.Default.LocationOn,
                            title = "Drop off",
                            address = "456 Medical Center, City"
                        )
                    }
                    
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(70.dp),
                        color = HealthRideColors.DividerColor
                    )
                    
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .width(100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Ride Type",
                            tint = HealthRideColors.TextMedium,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Standard",
                            style = MaterialTheme.typography.bodyMedium,
                            color = HealthRideColors.TextDark
                        )
                        
                        Text(
                            text = "One Way",
                            style = MaterialTheme.typography.bodySmall,
                            color = HealthRideColors.TextMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor) = when (status) {
        "Confirmed" -> Pair(HealthRideColors.SuccessLight, HealthRideColors.Success)
        "Pending" -> Pair(HealthRideColors.WarningLight, HealthRideColors.Warning)
        "Cancelled" -> Pair(HealthRideColors.ErrorLight, HealthRideColors.Error)
        else -> Pair(HealthRideColors.BackgroundMedium, HealthRideColors.TextMedium)
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
    }
}

@Composable
fun LocationItem(
    icon: ImageVector,
    title: String,
    address: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (title == "Pick up") HealthRideColors.Success else HealthRideColors.Error,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = HealthRideColors.TextMedium
            )
            
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium,
                color = HealthRideColors.TextDark
            )
        }
    }
}

@Composable
fun ServicesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Our Services",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = HealthRideColors.TextDark
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ServiceItem(
                icon = Icons.Default.DirectionsCar,
                title = "Standard",
                modifier = Modifier.weight(1f)
            )
            
            ServiceItem(
                icon = Icons.Default.AccessibleForward,
                title = "Wheelchair",
                modifier = Modifier.weight(1f)
            )
            
            ServiceItem(
                icon = Icons.Default.AirlineSeatFlatAngled,
                title = "Stretcher",
                modifier = Modifier.weight(1f)
            )
            
            ServiceItem(
                icon = Icons.Default.LocalHospital,
                title = "Medical",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ServiceItem(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(HealthRideColors.PrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = HealthRideColors.PrimaryBlue,
                modifier = Modifier.size(30.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = HealthRideColors.TextDark
        )
    }
}

@Composable
fun InsuranceVerificationSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = HealthRideColors.PrimaryLight
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = "Insurance Verification",
                tint = HealthRideColors.PrimaryBlue,
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Insurance Verification",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = HealthRideColors.TextDark
                )
                
                Text(
                    text = "Verify your insurance to get covered rides",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HealthRideColors.TextMedium
                )
            }
            
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = HealthRideColors.PrimaryBlue
                )
            ) {
                Text("Verify")
            }
        }
    }
}

@Composable
fun BookingContent(
    modifier: Modifier = Modifier,
    currentStep: Int,
    selectedRideType: String,
    onRideTypeSelected: (String) -> Unit,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    selectedReturnDate: LocalDate,
    onReturnDateSelected: (LocalDate) -> Unit,
    selectedReturnTime: String,
    onReturnTimeSelected: (String) -> Unit,
    specialInstructions: String,
    onSpecialInstructionsChange: (String) -> Unit,
    isRoundTrip: Boolean,
    onRoundTripChange: (Boolean) -> Unit,
    needsCompanion: Boolean,
    onNeedsCompanionChange: (Boolean) -> Unit,
    currentBookingStep: BookingStep
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HealthRideColors.BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        StepIndicator(currentStep = currentStep, totalSteps = 4)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = HealthRideColors.SurfaceColor
            )
        ) {
            when (currentBookingStep) {
                BookingStep.RideType -> {
                    RideTypeSelection(
                        selectedRideType = selectedRideType,
                        onRideTypeSelected = onRideTypeSelected
                    )
                }
                BookingStep.Schedule -> {
                    Column {
                        DateSelector(
                            selectedDate = selectedDate,
                            onDateSelected = onDateSelected
                        )
                        
                        TimeSelector(
                            selectedTime = selectedTime,
                            onTimeSelected = onTimeSelected
                        )
                    }
                }
                BookingStep.Details -> {
                    Column {
                        LocationEntrySection()
                        
                        AdditionalInfoSection(
                            specialInstructions = specialInstructions,
                            onSpecialInstructionsChange = onSpecialInstructionsChange,
                            isRoundTrip = isRoundTrip,
                            onRoundTripChange = onRoundTripChange,
                            needsCompanion = needsCompanion,
                            onNeedsCompanionChange = onNeedsCompanionChange
                        )
                        
                        if (isRoundTrip) {
                            ReturnTripSection(
                                selectedDate = selectedReturnDate,
                                onDateSelected = onReturnDateSelected,
                                selectedTime = selectedReturnTime,
                                onTimeSelected = onReturnTimeSelected
                            )
                        }
                    }
                }
                BookingStep.Confirm -> {
                    BookingSummary(
                        rideType = selectedRideType,
                        date = selectedDate,
                        time = selectedTime,
                        isRoundTrip = isRoundTrip,
                        returnDate = selectedReturnDate,
                        returnTime = selectedReturnTime,
                        specialInstructions = specialInstructions,
                        needsCompanion = needsCompanion
                    )
                }
            }
        }
    }
}

@Composable
fun LocationEntrySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Locations",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = HealthRideColors.TextDark
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = "",
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Pickup Location") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Pickup",
                    tint = HealthRideColors.Success
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = HealthRideColors.PrimaryBlue,
                unfocusedBorderColor = HealthRideColors.DividerColor
            )
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = "",
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Dropoff Location") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Dropoff",
                    tint = HealthRideColors.Error
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = HealthRideColors.PrimaryBlue,
                unfocusedBorderColor = HealthRideColors.DividerColor
            )
        )
    }
}

@Composable
fun BookingSummary(
    rideType: String,
    date: LocalDate,
    time: String,
    isRoundTrip: Boolean,
    returnDate: LocalDate,
    returnTime: String,
    specialInstructions: String,
    needsCompanion: Boolean
) {
    val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Booking Summary",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = HealthRideColors.TextDark
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SummaryItem(title = "Ride Type", value = rideType.ifEmpty { "Not selected" })
        SummaryItem(title = "Date", value = dateFormatter.format(date))
        SummaryItem(title = "Time", value = time)
        SummaryItem(title = "Trip Type", value = if (isRoundTrip) "Round Trip" else "One Way")
        
        if (isRoundTrip) {
            SummaryItem(title = "Return Date", value = dateFormatter.format(returnDate))
            SummaryItem(title = "Return Time", value = returnTime)
        }
        
        SummaryItem(title = "Companion", value = if (needsCompanion) "Yes" else "No")
        
        if (specialInstructions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Special Instructions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = HealthRideColors.TextDark
            )
            Text(
                text = specialInstructions,
                style = MaterialTheme.typography.bodyMedium,
                color = HealthRideColors.TextMedium
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = HealthRideColors.BackgroundMedium
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Estimated Cost",
                        style = MaterialTheme.typography.titleSmall,
                        color = HealthRideColors.TextDark
                    )
                    
                    Text(
                        text = "$25.00",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HealthRideColors.TextDark
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "This may be covered by your insurance",
                    style = MaterialTheme.typography.bodySmall,
                    color = HealthRideColors.TextMedium
                )
            }
        }
    }
}

@Composable
fun SummaryItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = HealthRideColors.TextMedium
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = HealthRideColors.TextDark,
            fontWeight = FontWeight.SemiBold
        )
    }
} 