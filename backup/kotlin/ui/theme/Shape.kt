package com.healthride.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Main shapes object for Material 3 theme
val HealthRideShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)

// Additional custom corner shapes
object HealthRideCorners {
    // Button shapes
    val buttonSmall = RoundedCornerShape(4.dp)
    val buttonMedium = RoundedCornerShape(8.dp)
    val buttonLarge = RoundedCornerShape(12.dp)
    val buttonRounded = RoundedCornerShape(50)
    
    // Card shapes
    val cardSmall = RoundedCornerShape(8.dp)
    val cardMedium = RoundedCornerShape(12.dp)
    val cardLarge = RoundedCornerShape(16.dp)
    
    // Input field shapes
    val inputField = RoundedCornerShape(8.dp)
    
    // Bottom sheet shapes
    val bottomSheetTop = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    
    // Dialog shapes
    val dialog = RoundedCornerShape(16.dp)
    
    // Chip shapes
    val chip = RoundedCornerShape(8.dp)
    val chipRounded = RoundedCornerShape(50)
    
    // Image shapes
    val imageSmall = RoundedCornerShape(4.dp)
    val imageMedium = RoundedCornerShape(8.dp)
    val imageLarge = RoundedCornerShape(12.dp)
    val imageRounded = RoundedCornerShape(50)
} 