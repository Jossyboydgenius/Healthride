package com.example.healthride.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.healthride.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.verticalScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthride.ui.viewmodel.UserViewModel
import androidx.compose.runtime.livedata.observeAsState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceUploadScreen(
    userViewModel: UserViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onVerificationSubmitted: () -> Unit = {}
) {
    // --- State Observation ---
    val insuranceCardUrl by userViewModel.insuranceCardUrl.observeAsState()
    val idCardUrl by userViewModel.idCardUrl.observeAsState()
    val isLoading by userViewModel.isLoading.observeAsState(false)
    val error by userViewModel.error.observeAsState()
    val verificationSubmitted by userViewModel.verificationSubmitted.observeAsState(false)

    // --- Local UI State ---
    var uriForCamera by remember { mutableStateOf<Uri?>(null) }
    var currentUploadTarget by remember { mutableStateOf<String?>(null) } // "insurance" or "id"

    val context = LocalContext.current
    val packageName = context.packageName

    // --- Permissions ---
    var hasCameraPermission by remember {
        mutableStateOf( ContextCompat.checkSelfPermission( context, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED )
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            hasCameraPermission = true
            uriForCamera?.let { uri ->
                Log.d("InsuranceUpload", "Permission granted, launching camera for $currentUploadTarget")
                // NOTE: cameraLauncher must be defined *before* this point for this to work immediately
                // If cameraLauncher is defined below, this immediate launch might fail.
                // Consider moving launcher definitions up if immediate launch after permission is needed.
                // For now, just set hasCameraPermission = true is sufficient.
            }
        } else {
            Log.w("InsuranceUpload", "Camera permission denied.")
        }
    }

    // --- File Creation ---
    fun createImageFileUri(): Uri? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val imageFileName = "JPEG_${timeStamp}_"
            val storageDir: File? = context.externalCacheDir ?: context.cacheDir
            if (storageDir == null) {
                Log.e("InsuranceUpload", "Failed to get cache directory.")
                return null
            }
            val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
            FileProvider.getUriForFile(context, "$packageName.fileprovider", imageFile)
        } catch (ex: Exception) {
            Log.e("InsuranceUpload", "Error creating image file URI: ${ex.message}", ex)
            null
        }
    }

    // --- Activity Result Launchers (Defined BEFORE they are used in permission callback if needed) ---
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        val target = currentUploadTarget
        val uri = uriForCamera // Use the stored URI
        currentUploadTarget = null
        uriForCamera = null // Reset after use

        if (success && uri != null && target != null) {
            Log.d("InsuranceUpload", "$target camera success. URI: $uri")
            if (target == "insurance") userViewModel.uploadInsuranceCard(uri)
            else if (target == "id") userViewModel.uploadIdCard(uri)
        } else {
            Log.w("InsuranceUpload", "$target camera failed or URI/target was null. Success: $success")
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val target = currentUploadTarget
        currentUploadTarget = null

        if (uri != null && target != null) {
            Log.d("InsuranceUpload", "$target gallery selected. URI: $uri")
            if (target == "insurance") userViewModel.uploadInsuranceCard(uri)
            else if (target == "id") userViewModel.uploadIdCard(uri)
        } else {
            Log.w("InsuranceUpload", "$target gallery selection cancelled or URI/target was null.")
        }
    }

    // --- Side Effects ---
    LaunchedEffect(verificationSubmitted) {
        if (verificationSubmitted == true) {
            onVerificationSubmitted()
        }
    }
    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

    // --- UI ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insurance Verification") },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon( Icons.Default.ArrowBack, "Back" ) } },
                colors = TopAppBarDefaults.topAppBarColors( containerColor = SurfaceWhite, titleContentColor = TextDarkBlue )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text( "Upload Your Documents", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextDarkBlue )
            Spacer(Modifier.height(8.dp))
            Text( "We need clear photos of your Insurance Card and a valid ID.", style = MaterialTheme.typography.bodyMedium, color = TextMediumGray, textAlign = TextAlign.Center )
            Spacer(Modifier.height(24.dp))

            // Error Display
            if (error != null) {
                Text(
                    text = "Error: $error", // Display the error message from ViewModel
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            // Insurance Card Section - Calling DocumentUploadCard
            DocumentUploadCard(
                title = "Insurance Card",
                imageUrl = insuranceCardUrl,
                // Show loading if general loading is true AND target was insurance
                isLoading = isLoading == true && currentUploadTarget == "insurance",
                onTakePhotoClick = {
                    currentUploadTarget = "insurance" // Set target
                    if (hasCameraPermission) {
                        createImageFileUri()?.let { uri ->
                            uriForCamera = uri // Store URI
                            Log.d("InsuranceUpload", "Launching camera for insurance. Target URI: $uri")
                            cameraLauncher.launch(uri) // Launch camera
                        } ?: Log.e("InsuranceUpload", "Failed to create URI for insurance photo")
                    } else {
                        Log.d("InsuranceUpload", "Requesting camera permission for insurance.")
                        uriForCamera = createImageFileUri() // Prepare URI in case permission granted
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA) // Request permission
                    }
                },
                onUploadClick = {
                    currentUploadTarget = "insurance"; // Set target
                    Log.d("InsuranceUpload", "Launching gallery for insurance.")
                    galleryLauncher.launch("image/*") // Launch gallery
                }
            )

            Spacer(Modifier.height(16.dp))

            // Identification Card Section - Calling DocumentUploadCard
            DocumentUploadCard(
                title = "Identification Card",
                description = "(Driver's License or Government ID)",
                imageUrl = idCardUrl,
                // Show loading if general loading is true AND target was id
                isLoading = isLoading == true && currentUploadTarget == "id",
                onTakePhotoClick = {
                    currentUploadTarget = "id" // Set target
                    if (hasCameraPermission) {
                        createImageFileUri()?.let { uri ->
                            uriForCamera = uri // Store URI
                            Log.d("InsuranceUpload", "Launching camera for ID. Target URI: $uri")
                            cameraLauncher.launch(uri) // Launch camera
                        } ?: Log.e("InsuranceUpload", "Failed to create URI for ID photo")
                    } else {
                        Log.d("InsuranceUpload", "Requesting camera permission for ID.")
                        uriForCamera = createImageFileUri() // Prepare URI
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA) // Request permission
                    }
                },
                onUploadClick = {
                    currentUploadTarget = "id"; // Set target
                    Log.d("InsuranceUpload", "Launching gallery for ID.")
                    galleryLauncher.launch("image/*") // Launch gallery
                }
            )

            Spacer(Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    Log.d("InsuranceUpload", "Submit button clicked.")
                    userViewModel.submitForVerification()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                ),
                // Enable only if both URLs present AND not currently loading generally
                enabled = !insuranceCardUrl.isNullOrBlank() && !idCardUrl.isNullOrBlank() && isLoading != true
            ) {
                // Show loading if general loading is true AND no specific target (i.e., submitting)
                if (isLoading == true && currentUploadTarget == null) {
                    CircularProgressIndicator( color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(24.dp) )
                } else {
                    Text( "Submit for Verification", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}


/**
 * Reusable Composable for the document upload card UI.
 * This function MUST be defined within the same file or accessible scope
 * where it's called by InsuranceUploadScreen.
 */
@Composable
private fun DocumentUploadCard(
    title: String,
    description: String? = null,
    imageUrl: String?, // URL of the uploaded image (placeholder or real)
    isLoading: Boolean, // Is an upload specifically for this card in progress?
    onTakePhotoClick: () -> Unit,
    onUploadClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title and optional Description
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextDarkBlue)
            if (description != null) {
                Spacer(Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodySmall, color = TextMediumGray, textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(16.dp))

            // Image Display Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BackgroundGray)
                    .border(1.dp, TextLightGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Show loading indicator if this specific card's upload is happening
                if (isLoading) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
                // Show image if URL is present AND not currently loading this card
                else if (!imageUrl.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl), // Use Coil to load image from URL
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit // Fit ensures the whole image is visible
                    )
                }
                // Show placeholder if no image and not loading
                else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, null, Modifier.size(48.dp), tint = TextLightGray)
                        Spacer(Modifier.height(8.dp))
                        Text("Take photo or upload", color = TextMediumGray)
                    }
                }
            } // End Box

            Spacer(Modifier.height(16.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val isImagePresent = !imageUrl.isNullOrBlank()

                // Take Photo / Retake Button
                Button(
                    onClick = onTakePhotoClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = if (isImagePresent) ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                    else ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    border = if (isImagePresent) BorderStroke(1.dp, PrimaryBlue) else null,
                    enabled = !isLoading // Disable only if this card is loading
                ) {
                    Icon(
                        imageVector = if (isImagePresent) Icons.Outlined.CameraAlt else Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (isImagePresent) "Retake" else "Take Photo")
                }

                Spacer(Modifier.width(8.dp))

                // Upload / Upload New Button
                OutlinedButton(
                    onClick = onUploadClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    enabled = !isLoading // Disable only if this card is loading
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (isImagePresent) "Upload New" else "Upload")
                }
            } // End Row
        } // End Column
    } // End Card
} // End DocumentUploadCard