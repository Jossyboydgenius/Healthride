package com.example.healthride.ui

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.* // Keep outlined icons
import androidx.compose.material.icons.rounded.* // Keep rounded icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthride.ui.theme.*
import com.example.healthride.ui.viewmodel.AuthViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.livedata.observeAsState // Ensure correct import
import com.example.healthride.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernMainScreen(
    navController: NavController,
    onSignOutRequest: () -> Unit = {},
    onSignOut: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {

    val userViewModel: UserViewModel = viewModel()
    val currentUser by userViewModel.currentUser.observeAsState()
    // Sign out logic
    val isLoadingAuth by authViewModel.isLoading.observeAsState(false) // Observe loading state

    LaunchedEffect(currentUser, isLoadingAuth) { // Depend on both user and loading state
        if (currentUser == null && !isLoadingAuth) { // Only trigger if not loading AND user is null
            Log.d("ModernMainScreen", "User is null and not loading, signing out.")
            onSignOut()
        }
    }

    val handleSignOut = {
        Log.d("ModernMainScreen", "handleSignOut called.")
        authViewModel.signOut() // Use ViewModel to sign out first
        // The LaunchedEffect above should handle the navigation via onSignOut callback
    }

    var currentRoute by remember { mutableStateOf("home") }


    Scaffold(
        bottomBar = {
            ModernBottomNavigationRedesigned(
                currentRoute = currentRoute,
                onNavigate = { route -> currentRoute = route }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(bottom = innerPadding.calculateBottomPadding()) // Apply bottom padding ONLY
        ) {
            // Content switching based on currentRoute
            when (currentRoute) {
                "home" -> ModernHomeScreen(navController = navController)
                "rides" -> RidesScreen( onBackClick = { currentRoute = "home" }, onRideClick = { /* TODO */ } )
                "notifications" -> NotificationsScreen( onBackClick = { currentRoute = "home" } )
                // This is the CORRECT line:
                "profile" -> ProfileScreen(
                    navController = navController,
                    user = currentUser, // <--- ADD THIS PART
                    onBackClick = { currentRoute = "home" },
                    onConfirmSignOut = onSignOutRequest
                )
                // ^^^^^^^^^^^^^^^^^^^^^^^^^^ Add this part
            }
        }
    }
}

// --- Redesigned Bottom Navigation ---
@Composable
fun ModernBottomNavigationRedesigned(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavItemRedesigned("home", "Book", Icons.Outlined.DirectionsCar, Icons.Rounded.DirectionsCar),
        NavItemRedesigned("rides", "Rides", Icons.Outlined.History, Icons.Rounded.History), // History icon
        NavItemRedesigned("notifications", "Alerts", Icons.Outlined.Notifications, Icons.Rounded.Notifications),
        NavItemRedesigned("profile", "Profile", Icons.Outlined.Person, Icons.Rounded.Person)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)), // Clip the surface itself
        color = SurfaceWhite,
        contentColor = TextMediumGray // Default icon/text color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // Consistent height
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                ModernNavItemRedesigned(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onItemClick = { onNavigate(item.route) }
                )
            }
        }
    }
}

// --- Redesigned Nav Item Composable ---
@Composable
fun RowScope.ModernNavItemRedesigned( // Added RowScope receiver
    item: NavItemRedesigned,
    isSelected: Boolean,
    onItemClick: () -> Unit
) {
    val targetColor = if (isSelected) PrimaryBlue else TextMediumGray
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 200),
        label = "nav_item_color_anim"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                onClick = onItemClick,
                indication = null, // No default ripple, custom indication below if needed
                interactionSource = interactionSource
            )
            .padding(vertical = 8.dp) // Add vertical padding for touch area
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.icon,
            contentDescription = item.title,
            tint = animatedColor,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.title,
            color = animatedColor,
            style = MaterialTheme.typography.labelSmall, // Consistent label style
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

// --- Data class for Redesigned Nav Item ---
data class NavItemRedesigned(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)