package com.example.healthride.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthride.data.model.User
import com.example.healthride.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    onBackClick: () -> Unit = {},
    // Sample user data for preview
    user: User? = User(
        id = "user_123",
        email = "user@example.com",
        firstName = "John",
        lastName = "Doe",
        phone = "(555) 123-4567",
        address = "123 Health Street",
        city = "Medical City",
        state = "MC",
        zipCode = "12345"
    )
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var showSuccessMessage by remember { mutableStateOf(false) }

    // Animation states
    var showContent by remember { mutableStateOf(false) }

    // Form states (initialized with user data if available)
    var firstName by remember { mutableStateOf(user?.firstName ?: "") }
    var lastName by remember { mutableStateOf(user?.lastName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var address by remember { mutableStateOf(user?.address ?: "") }
    var city by remember { mutableStateOf(user?.city ?: "") }
    var state by remember { mutableStateOf(user?.state ?: "") }
    var zipCode by remember { mutableStateOf(user?.zipCode ?: "") }

    // Track if form has changes
    val hasChanges = remember(firstName, lastName, email, phone, address, city, state, zipCode) {
        firstName != user?.firstName || lastName != user?.lastName ||
                email != user?.email || phone != user?.phone ||
                address != user?.address || city != user?.city ||
                state != user?.state || zipCode != user?.zipCode
    }

    // Form validation
    val isFormValid = remember(firstName, lastName, email, phone) {
        firstName.isNotBlank() && lastName.isNotBlank() &&
                email.isNotBlank() && email.contains("@") && phone.isNotBlank()
    }

    // Start animations
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    // Hide success message after delay
    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            delay(3000)
            showSuccessMessage = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Personal Information",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDarkBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = TextDarkBlue
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8FAFC),
                            Color(0xFFF5F7FD)
                        )
                    )
                )
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Personal Information Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceWhite
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Edit Your Information",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkBlue
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Name fields (side by side)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // First Name
                                Column(modifier = Modifier.weight(1f)) {
                                    FormField(
                                        value = firstName,
                                        onValueChange = { firstName = it },
                                        label = "First Name",
                                        icon = Icons.Outlined.Person,
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Words,
                                            imeAction = ImeAction.Next
                                        )
                                    )
                                }

                                // Last Name
                                Column(modifier = Modifier.weight(1f)) {
                                    FormField(
                                        value = lastName,
                                        onValueChange = { lastName = it },
                                        label = "Last Name",
                                        icon = Icons.Outlined.Person,
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.Words,
                                            imeAction = ImeAction.Next
                                        )
                                    )
                                }
                            }

                            // Contact Information
                            FormSection(title = "Contact Information") {
                                FormField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = "Email Address",
                                    icon = Icons.Outlined.Email,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    )
                                )

                                FormField(
                                    value = phone,
                                    onValueChange = { phone = it },
                                    label = "Phone Number",
                                    icon = Icons.Outlined.Phone,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Phone,
                                        imeAction = ImeAction.Next
                                    )
                                )
                            }

                            // Address Information
                            FormSection(title = "Address Information") {
                                FormField(
                                    value = address,
                                    onValueChange = { address = it },
                                    label = "Street Address",
                                    icon = Icons.Outlined.Home,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    )
                                )

                                FormField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = "City",
                                    icon = Icons.Outlined.LocationCity,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Words,
                                        imeAction = ImeAction.Next
                                    )
                                )

                                // State and Zip code (side by side)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // State
                                    Column(modifier = Modifier.weight(1f)) {
                                        FormField(
                                            value = state,
                                            onValueChange = { state = it },
                                            label = "State",
                                            icon = Icons.Outlined.Map,
                                            keyboardOptions = KeyboardOptions(
                                                capitalization = KeyboardCapitalization.Characters,
                                                imeAction = ImeAction.Next
                                            )
                                        )
                                    }

                                    // Zip Code
                                    Column(modifier = Modifier.weight(1f)) {
                                        FormField(
                                            value = zipCode,
                                            onValueChange = { zipCode = it },
                                            label = "Zip Code",
                                            icon = Icons.Outlined.Pin,
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Done
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button
                    Button(
                        onClick = {
                            // Simulate saving
                            coroutineScope.launch {
                                showSuccessMessage = true
                                // In a real app, you would save to database here
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        ),
                        enabled = hasChanges && isFormValid
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Save Changes",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // If no changes, show a helper text
                    if (!hasChanges) {
                        Text(
                            text = "Make changes to your information, then tap Save.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMediumGray
                        )
                    }
                    else if (!isFormValid) {
                        Text(
                            text = "Please fill in all required fields.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ErrorRed
                        )
                    }
                }
            }

            // Success Message Snackbar
            AnimatedVisibility(
                visible = showSuccessMessage,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Surface(
                    color = SuccessGreen,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color.White
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Changes saved successfully!",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FormSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(0.2f),
                color = PrimaryBlue.copy(alpha = 0.3f),
                thickness = 2.dp
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDarkBlue,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Divider(
                modifier = Modifier.weight(1f),
                color = PrimaryBlue.copy(alpha = 0.2f),
                thickness = 1.dp
            )
        }

        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = TextLightGray,
                focusedLabelColor = PrimaryBlue,
                cursorColor = PrimaryBlue,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorBorderColor = ErrorRed,
                errorLabelColor = ErrorRed,
                errorCursorColor = ErrorRed
            ),
            keyboardOptions = keyboardOptions,
            isError = isError,
            singleLine = true
        )

        // Error message
        if (isError && errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = ErrorRed,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}