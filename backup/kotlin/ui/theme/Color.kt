// In ui/theme/Color.kt
package com.healthride.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Health Ride App color definitions
 * Organized into categories for maintainability
 */
object HealthRideColors {
    
    /**
     * Primary brand colors
     */
    object Primary {
        val primary = Color(0xFF4F6BED)
        val onPrimary = Color(0xFFFFFFFF)
        val primaryContainer = Color(0xFFE6ECFD)
        val onPrimaryContainer = Color(0xFF0A1976)
    }
    
    /**
     * Secondary brand colors
     */
    object Secondary {
        val secondary = Color(0xFF8776D7)
        val onSecondary = Color(0xFFFFFFFF)
        val secondaryContainer = Color(0xFFEFEBFF)
        val onSecondaryContainer = Color(0xFF24135B)
    }
    
    /**
     * Tertiary/accent colors
     */
    object Tertiary {
        val tertiary = Color(0xFF38C1BA)
        val onTertiary = Color(0xFFFFFFFF)
        val tertiaryContainer = Color(0xFFCCF2EF)
        val onTertiaryContainer = Color(0xFF00403B)
    }
    
    /**
     * Error and status colors
     */
    object Error {
        val error = Color(0xFFEF4444)
        val onError = Color(0xFFFFFFFF)
        val errorContainer = Color(0xFFFFEDEA)
        val onErrorContainer = Color(0xFF5F1412)
        
        // Additional status colors
        val success = Color(0xFF22C55E)
        val warning = Color(0xFFF59E0B)
        val info = Color(0xFF3B82F6)
    }
    
    /**
     * Neutral colors (backgrounds, surfaces, text)
     */
    object Neutral {
        // Background
        val background = Color(0xFFF8FAFC)
        val onBackground = Color(0xFF1E293B)
        
        // Surface
        val surface = Color(0xFFFFFFFF)
        val onSurface = Color(0xFF1E293B)
        val surfaceVariant = Color(0xFFF0F4FF)
        val onSurfaceVariant = Color(0xFF64748B)
        
        // Text
        val textDark = Color(0xFF1E293B)
        val textMedium = Color(0xFF64748B)
        val textLight = Color(0xFFABB3C4)
        
        // Outline
        val outline = Color(0xFFBDC4DE)
        val outlineVariant = Color(0xFFE2E8F0)
    }
    
    /**
     * Dark theme specific colors
     */
    object DarkTheme {
        // Primary
        val primary = Color(0xFF8EA5FF)
        val onPrimary = Color(0xFF001452)
        val primaryContainer = Color(0xFF0B2991)
        val onPrimaryContainer = Color(0xFFDBE1FF)
        
        // Secondary
        val secondary = Color(0xFFC8BFFF)
        val onSecondary = Color(0xFF352383)
        val secondaryContainer = Color(0xFF4C3D9C)
        val onSecondaryContainer = Color(0xFFE9DFFF)
        
        // Tertiary
        val tertiary = Color(0xFF5CDCD3)
        val onTertiary = Color(0xFF00312E)
        val tertiaryContainer = Color(0xFF17504D)
        val onTertiaryContainer = Color(0xFFBDEDEA)
        
        // Background
        val background = Color(0xFF1E293B)
        val onBackground = Color(0xFFECF0F5)
        
        // Surface
        val surface = Color(0xFF121A2A)
        val onSurface = Color(0xFFECF0F5)
        val surfaceVariant = Color(0xFF2A3447)
        val onSurfaceVariant = Color(0xFFBDCBE6)
        
        // Text
        val textDark = Color(0xFFECF0F5)
        val textMedium = Color(0xFFBDCBE6)
        val textLight = Color(0xFF84919F)
        
        // Outline
        val outline = Color(0xFF6F7E8C)
        val outlineVariant = Color(0xFF3A4256)
    }
}