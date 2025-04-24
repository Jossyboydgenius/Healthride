package com.example.healthride.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthride.R
import com.example.healthride.data.model.RideType
import com.example.healthride.ui.HealthRideColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

// Booking Section Header
@Composable
fun BookingSectionHeader(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = HealthRideColors.PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = HealthRideColors.TextDark
            )
        }
        
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = HealthRideColors.TextMedium
            )
        }
    }
}

// Step Indicator
@Composable
fun StepIndicator(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 1..totalSteps) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (i <= currentStep) HealthRideColors.PrimaryBlue
                        else HealthRideColors.BackgroundMedium
                    )
            )
            
            if (i < totalSteps) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

// Ride Type Selection Grid
@Composable
fun RideTypeSelection(
    selectedRideType: String?,
    onRideTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rideTypes = listOf(
        Triple("Standard", "Regular medical transportation", Icons.Default.DirectionsCar),
        Triple("Wheelchair", "Wheelchair accessible vehicle", Icons.Default.Accessible),
        Triple("Stretcher", "For patients who need to lie down", Icons.Default.AirlineSeatFlatAngled),
        Triple("Medical", "With basic medical equipment", Icons.Default.LocalHospital)
    )
    
    Column(modifier = modifier.fillMaxWidth()) {
        rideTypes.forEach { (type, description, icon) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .selectable(
                        selected = type == selectedRideType,
                        onClick = { onRideTypeSelected(type) }
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (type == selectedRideType) 
                        HealthRideColors.PrimaryLight 
                    else 
                        HealthRideColors.BackgroundMedium
                ),
                border = if (type == selectedRideType) 
                    BorderStroke(2.dp, HealthRideColors.PrimaryBlue) 
                else 
                    null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (type == selectedRideType) 
                            HealthRideColors.PrimaryBlue 
                        else 
                            HealthRideColors.TextMedium,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (type == selectedRideType) 
                                HealthRideColors.PrimaryBlue 
                            else 
                                HealthRideColors.TextDark
                        )
                        
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = HealthRideColors.TextMedium
                        )
                    }
                    
                    if (type == selectedRideType) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = HealthRideColors.PrimaryBlue
                        )
                    }
                }
            }
        }
    }
}

// Date Selection Calendar
@Composable
fun DateSelector(
    selectedDate: String?,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    val calendar = Calendar.getInstance()
    
    val dates = List(7) { index ->
        calendar.add(Calendar.DAY_OF_YEAR, if (index == 0) 0 else 1)
        dateFormat.format(calendar.time)
    }
    
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Select Date",
            style = MaterialTheme.typography.titleSmall,
            color = HealthRideColors.TextMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dates.take(3).forEach { date ->
                DateOption(
                    date = date,
                    isSelected = date == selectedDate,
                    onClick = { onDateSelected(date) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dates.drop(3).forEach { date ->
                DateOption(
                    date = date,
                    isSelected = date == selectedDate,
                    onClick = { onDateSelected(date) },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Empty space for the last spot
            Box(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun DateOption(
    date: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) HealthRideColors.PrimaryBlue else HealthRideColors.BackgroundMedium,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                if (isSelected) HealthRideColors.PrimaryLight else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        val parts = date.split(" ")
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = parts[0], // Month
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) HealthRideColors.PrimaryBlue else HealthRideColors.TextMedium
            )
            
            Text(
                text = parts[1].replace(",", ""), // Day
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) HealthRideColors.PrimaryBlue else HealthRideColors.TextDark
            )
        }
    }
}

// Time Selection
@Composable
fun TimeSelector(
    selectedTime: String?,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeSlots = listOf(
        "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM",
        "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM",
        "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM"
    )
    
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Select Time",
            style = MaterialTheme.typography.titleSmall,
            color = HealthRideColors.TextMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            timeSlots.take(4).forEach { time ->
                TimeOption(
                    time = time,
                    isSelected = time == selectedTime,
                    onClick = { onTimeSelected(time) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            timeSlots.subList(4, 8).forEach { time ->
                TimeOption(
                    time = time,
                    isSelected = time == selectedTime,
                    onClick = { onTimeSelected(time) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            timeSlots.subList(8, 12).forEach { time ->
                TimeOption(
                    time = time,
                    isSelected = time == selectedTime,
                    onClick = { onTimeSelected(time) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TimeOption(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) HealthRideColors.PrimaryBlue else HealthRideColors.BackgroundMedium,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                if (isSelected) HealthRideColors.PrimaryLight else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) HealthRideColors.PrimaryBlue else HealthRideColors.TextDark,
            textAlign = TextAlign.Center
        )
    }
}

// Information Entry Section
@Composable
fun AdditionalInfoSection(
    notes: String,
    onNotesChange: (String) -> Unit,
    needsRoundTrip: Boolean,
    onNeedsRoundTripChange: (Boolean) -> Unit,
    needsCompanion: Boolean,
    onNeedsCompanionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Notes field
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Special Instructions (Optional)") },
            placeholder = { Text("E.g., I need help getting in/out of the vehicle") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = HealthRideColors.PrimaryBlue,
                unfocusedBorderColor = HealthRideColors.BackgroundMedium
            ),
            minLines = 3
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Round trip option
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNeedsRoundTripChange(!needsRoundTrip) }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = needsRoundTrip,
                onCheckedChange = { onNeedsRoundTripChange(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = HealthRideColors.PrimaryBlue,
                    uncheckedColor = HealthRideColors.TextMedium
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = "I need a round trip",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HealthRideColors.TextDark
                )
                
                Text(
                    text = "Schedule a return ride for later",
                    style = MaterialTheme.typography.bodySmall,
                    color = HealthRideColors.TextMedium
                )
            }
        }
        
        // Companion option
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNeedsCompanionChange(!needsCompanion) }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = needsCompanion,
                onCheckedChange = { onNeedsCompanionChange(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = HealthRideColors.PrimaryBlue,
                    uncheckedColor = HealthRideColors.TextMedium
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = "I need a companion",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HealthRideColors.TextDark
                )
                
                Text(
                    text = "A healthcare assistant will accompany you",
                    style = MaterialTheme.typography.bodySmall,
                    color = HealthRideColors.TextMedium
                )
            }
        }
    }
}

// Return Trip Selection (conditionally visible)
@Composable
fun ReturnTripSection(
    selectedDate: String?,
    onDateSelected: (String) -> Unit,
    selectedTime: String?,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = HealthRideColors.BackgroundMedium
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Return Trip Details",
                style = MaterialTheme.typography.titleMedium,
                color = HealthRideColors.TextDark
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DateSelector(
                selectedDate = selectedDate,
                onDateSelected = onDateSelected
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TimeSelector(
                selectedTime = selectedTime,
                onTimeSelected = onTimeSelected
            )
        }
    }
} 