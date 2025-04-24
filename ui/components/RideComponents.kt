package com.example.healthride.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Import the CORRECT data models
import com.example.healthride.data.model.Ride
import com.example.healthride.data.model.RideStatus
import com.example.healthride.formatToPattern // Import the extension function
import com.example.healthride.ui.theme.*

// --- RideCard Definition ---
@Composable
fun RideCard(
    ride: Ride, // *** Expects data.model.Ride ***
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow( elevation = 6.dp, shape = RoundedCornerShape(20.dp), spotColor = PrimaryPurple.copy(alpha = 0.1f) )
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = Color.White // Use SurfaceWhite from theme if preferred
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // Consistent padding
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.CalendarToday, null, tint = PrimaryPurple, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = ride.dateTime.formatToPattern("EEE, MMM d • h:mm a"),
                        fontWeight = FontWeight.Medium,
                        color = TextDarkBlue,
                        fontSize = 13.sp
                    )
                }
                // Ensure StatusBadge uses data.model.RideStatus
                StatusBadge(status = ride.status)
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Simplified Row layout for addresses
            Row( verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp) ) {
                Icon( Icons.Outlined.Adjust, "Pickup", tint = PrimaryBlue, modifier = Modifier.size(18.dp) )
                Spacer(Modifier.width(8.dp))
                Text( ride.pickupAddress, color = TextDarkBlue, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium )
            }
            Row( verticalAlignment = Alignment.CenterVertically ) {
                Icon( Icons.Outlined.Place, "Destination", tint = PrimaryPurple, modifier = Modifier.size(18.dp) )
                Spacer(Modifier.width(8.dp))
                Text( ride.destinationAddress, color = TextDarkBlue, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium )
            }
        }
    }
}

// --- StatusBadge Definition ---
@Composable
fun StatusBadge(status: RideStatus) { // *** Expects data.model.RideStatus ***
    val (backgroundColor, textColor, text) = when (status) {
        RideStatus.SCHEDULED -> Triple(PrimaryBlue.copy(alpha = 0.1f), PrimaryBlue, "Scheduled")
        RideStatus.IN_PROGRESS -> Triple(WarningYellow.copy(alpha = 0.1f), WarningYellow, "In Progress")
        RideStatus.COMPLETED -> Triple(SuccessGreen.copy(alpha = 0.1f), SuccessGreen, "Completed")
        RideStatus.CANCELLED -> Triple(ErrorRed.copy(alpha = 0.1f), ErrorRed, "Cancelled")
    }
    Surface(color = backgroundColor, shape = RoundedCornerShape(12.dp)) {
        Text(
            text = text, color = textColor, fontWeight = FontWeight.Medium, fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

// --- ActionButton Definition (Keep as is or remove if unused) ---
@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}

// --- RideInfoRow Definition (Keep as is) ---
@Composable
fun RideInfoRow( icon: ImageVector, title: String, value: String, valueColor: Color = TextDarkBlue ) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(icon, null, tint = TextMediumGray, modifier = Modifier.size(20.dp).padding(top = 2.dp))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodySmall, color = TextMediumGray)
            Spacer(Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = valueColor)
        }
    }
}

