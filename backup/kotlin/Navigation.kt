package com.example.healthride

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.healthride.data.model.VerificationStatus
import com.example.healthride.ui.*
import com.example.healthride.ui.viewmodel.AuthViewModel
import com.example.healthride.ui.viewmodel.UserViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            val authIsLoading by authViewModel.isLoading.observeAsState(true)
            val signedInUser by authViewModel.currentUser.observeAsState()

            LaunchedEffect(Unit) {
                authViewModel.checkCurrentUser()
            }

            ModernSplashScreen(
                onSplashFinished = {
                    val destination = if (!authIsLoading && signedInUser != null) {
                        Log.d("AppNavigation/Splash", "User is signed in (from ViewModel), navigating to main.")
                        userViewModel.loadCurrentUser()
                        "main"
                    } else {
                        Log.d("AppNavigation/Splash", "User is NOT signed in or state unclear, navigating to welcome.")
                        "welcome"
                    }

                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("welcome") {
            WelcomeScreen { navController.navigate("login") }
        }

        composable("login") {
            FirebaseLoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        composable("register") {
            MultiStepRegistrationScreen(
                authViewModel = authViewModel,
                onCompleteRegistration = {
                    navController.navigate("insuranceUpload") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable("insuranceUpload") {
            LaunchedEffect(Unit) { userViewModel.loadCurrentUser() }
            InsuranceUploadScreen(
                userViewModel = userViewModel,
                onBackClick = { navController.popBackStack() },
                onVerificationSubmitted = {
                    navController.navigate("verificationStatus") {
                        popUpTo("insuranceUpload") { inclusive = true }
                    }
                }
            )
        }

        composable("verificationStatus") {
            val currentUserStatus by userViewModel.currentUser.observeAsState()
            val statusString = currentUserStatus?.verificationStatus ?: VerificationStatus.PENDING.name
            val statusEnum = remember(statusString) {
                runCatching { VerificationStatus.valueOf(statusString.uppercase()) }
                    .getOrDefault(VerificationStatus.PENDING)
            }

            VerificationStatusScreen(
                status = statusEnum,
                onBackClick = {
                    if (statusEnum == VerificationStatus.VERIFIED) {
                        navController.navigate("main") { popUpTo(0); launchSingleTop = true }
                    } else {
                        navController.popBackStack()
                    }
                },
                onResubmit = { navController.navigate("insuranceUpload") },
                onContactSupport = { /* Handle contact */ }
            )
        }

        // --- MAIN Route ---
        composable("main") {
            Log.d("AppNavigation_Main", "Entering 'main' composable destination.")

            // --- Sign Out Handler ---
            val signOutCompleted by authViewModel.signOutComplete.observeAsState(false)
            LaunchedEffect(signOutCompleted) {
                if (signOutCompleted == true) {
                    Log.d("AppNavigation", "Sign out complete signal received. Navigating to welcome.")
                    navController.navigate("welcome") { popUpTo(0) }
                    authViewModel.resetSignOutComplete()
                }
            }

            // Observe user data and loading state
            val user by userViewModel.currentUser.observeAsState()
            val isUserLoading by userViewModel.isLoading.observeAsState(true)
            val isAuthLoading by authViewModel.isLoading.observeAsState(true)

            // Trigger user load on entry if needed
            LaunchedEffect(Unit) {
                if (authViewModel.currentUser.value != null && user == null && !isUserLoading) {
                    Log.d("AppNavigation_Main", "Auth user exists but UserViewModel empty. Triggering loadCurrentUser().")
                    userViewModel.loadCurrentUser()
                } else {
                    Log.d("AppNavigation_Main", "User load not triggered (already loading, loaded, or no auth user).")
                }
            }

            // Determine if overall loading is finished
            val isLoadingComplete = !isAuthLoading && !isUserLoading

            Log.d("AppNavigation_Main", "Rendering UI check - isLoadingComplete: $isLoadingComplete, Delegated User: ${user?.id}")

            // --- UI Rendering Logic ---
            if (!isLoadingComplete) {
                Log.d("AppNavigation_Main", "Showing Loading Indicator (waiting for auth/user details).")
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Loading is complete. Assign user to a local variable for safe access and smart casting.
                val currentUser = user // Create a local variable

                if (currentUser == null) {
                    // If loading is complete and there's still no user, they are not logged in. Navigate to welcome.
                    Log.w("AppNavigation_Main", "Load complete but currentUser is null. Navigating to welcome.")
                    LaunchedEffect(Unit) {
                        navController.navigate("welcome") { popUpTo(0) }
                    }
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator() // Show loading during brief navigation state
                    }
                } else {
                    // User object (currentUser) is definitely available here.
                    // Use currentUser for status checks and passing to screens.
                    val verificationStatusString = currentUser.verificationStatus // Safe access via local variable
                    val status = remember(verificationStatusString) {
                        runCatching { VerificationStatus.valueOf(verificationStatusString.uppercase()) }
                            .getOrDefault(VerificationStatus.NOT_SUBMITTED)
                    }

                    Log.d("AppNavigation_Main", "User loaded. User ID: ${currentUser.id}, Determined Status: $status") // Safe access

                    when (status) {
                        VerificationStatus.VERIFIED -> {
                            Log.d("AppNavigation_Main", "Status VERIFIED. Rendering ModernMainScreen.")
                            ModernMainScreen(
                                navController = navController,
                                authViewModel = authViewModel,
                                onSignOutRequest = {
                                    Log.d("AppNavigation", "Sign out request triggered from ModernMainScreen.")
                                    authViewModel.signOut()
                                }
                            )
                        }
                        VerificationStatus.PENDING,
                        VerificationStatus.REJECTED,
                        VerificationStatus.NOT_SUBMITTED -> {
                            val destination = when (status) {
                                VerificationStatus.PENDING, VerificationStatus.REJECTED -> "verificationStatus"
                                VerificationStatus.NOT_SUBMITTED -> "insuranceUpload"
                                else -> { Log.e("AppNavigation_Main", "Unexpected status $status in redirection block!"); "" }
                            }
                            Log.d("AppNavigation_Main", "Status NOT VERIFIED ($status). Preparing redirect to '$destination'.")

                            LaunchedEffect(status, destination) {
                                if (destination.isNotEmpty()) {
                                    navController.navigate(destination) {
                                        popUpTo("main") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    Spacer(Modifier.height(16.dp))
                                    Text(if (destination.isNotEmpty()) "Checking account status..." else "Error determining status.")
                                }
                            }
                        }
                    } // End when (status)
                } // End else (currentUser != null)
            } // End else (isLoadingComplete)
        } // End composable("main")



        // --- Placeholder Screen Routes ---
        composable("personalInfo") {
            PersonalInfoScreen(onBackClick = { navController.popBackStack() })
        }
        composable("insurancePayment") {
            InsurancePaymentScreen(onBackClick = { navController.popBackStack() })
        }
        composable("settingsSupport") {
            SettingsSupportScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

// Helper extension function to safely navigate to bookRide with parameters
fun NavController.navigateToBookRide(
    pickup: String = "",
    destination: String = "",
    rideType: String = "ambulatory"
) {
    try {
        val encodedPickup = URLEncoder.encode(pickup, StandardCharsets.UTF_8.toString())
        val encodedDestination = URLEncoder.encode(destination, StandardCharsets.UTF_8.toString())

        navigate("bookRide?pickup=$encodedPickup&destination=$encodedDestination&rideType=$rideType") {
            launchSingleTop = true
            // Optional: Add transition animations here if desired
        }
    } catch (e: Exception) {
        Log.e("Navigation", "Error navigating to booking screen: ${e.message}", e)
        // Fallback to simple navigation without parameters if encoding fails
        navigate("bookRide")
    }
}