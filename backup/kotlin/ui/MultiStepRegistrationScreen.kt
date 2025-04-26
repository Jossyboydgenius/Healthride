package com.example.healthride.ui

import androidx.compose.animation.*
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthride.data.model.User
import com.example.healthride.ui.components.Blob
import com.example.healthride.ui.theme.*
import com.example.healthride.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun StepIndicator(
    step: Int,
    isActive: Boolean,
    isCompleted: Boolean,
    isLastStep: Boolean,
    currentStep: Int,
    totalSteps: Int,
    titles: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    color = when {
                        isCompleted -> PrimaryBlue
                        isActive -> PrimaryBlueLight
                        else -> Color(0xFFE5E7EB)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, "Step $step Completed", tint = Color.White, modifier = Modifier.size(18.dp))
            } else {
                Text(step.toString(), color = if (isActive) PrimaryBlue else TextMediumGray, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            }
        }
        if (!isLastStep) {
            Spacer(Modifier.width(4.dp))
            Divider(modifier = Modifier.weight(1f).height(2.dp), color = if (isCompleted || isActive) PrimaryBlue else Color(0xFFE5E7EB))
            Spacer(Modifier.width(4.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiStepRegistrationScreen(
    onCompleteRegistration: () -> Unit = {},
    onBackToLogin: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    // State
    var currentStep by rememberSaveable { mutableStateOf(0) }
    val totalSteps = 4
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var state by rememberSaveable { mutableStateOf("") }
    var zipCode by rememberSaveable { mutableStateOf("") }
    var insuranceProvider by rememberSaveable { mutableStateOf("") }
    var policyNumber by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    // Focus & Scroll
    val focusManager = LocalFocusManager.current
    val firstNameFocus = remember { FocusRequester() }
    val lastNameFocus = remember { FocusRequester() }
    val phoneFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    val confirmPasswordFocus = remember { FocusRequester() }
    val addressFocus = remember { FocusRequester() }
    val cityFocus = remember { FocusRequester() }
    val stateFocus = remember { FocusRequester() }
    val zipCodeFocus = remember { FocusRequester() }
    val insuranceFocus = remember { FocusRequester() }
    val policyNumberFocus = remember { FocusRequester() }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // TextField Colors for Modern Look
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = Color(0xFFE5E7EB),
        focusedLabelColor = PrimaryBlue,
        cursorColor = PrimaryBlue,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color(0xFFF9FAFC)
    )

    // Validation
    fun validateCurrentStep(): Boolean {
        when (currentStep) {
            0 -> return firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()
            1 -> return email.isNotBlank() && password.isNotBlank() && password == confirmPassword
            2 -> return address.isNotBlank() && city.isNotBlank() && state.isNotBlank() && zipCode.isNotBlank()
            3 -> return insuranceProvider.isNotBlank() && policyNumber.isNotBlank()
        }
        return true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFF0F4FF), Color(0xFFF5F9FF))))
    ) {
        Blob(color = PrimaryBlue, size = 600, xOffset = -300f, yOffset = -200f, modifier = Modifier.alpha(0.08f))
        Blob(color = PrimaryPurple, size = 500, xOffset = 200f, yOffset = 500f, modifier = Modifier.alpha(0.08f))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Create Your Account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextDarkBlue
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0 until totalSteps) {
                    StepIndicator(
                        step = i + 1,
                        isActive = currentStep == i,
                        isCompleted = currentStep > i,
                        isLastStep = i == totalSteps - 1,
                        currentStep = currentStep,
                        totalSteps = totalSteps,
                        titles = listOf("Personal Information", "Set Up Your Account", "Add Your Address", "Insurance Information"),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Text(
                text = when (currentStep) {
                    0 -> "Personal Information"
                    1 -> "Set Up Your Account"
                    2 -> "Add Your Address"
                    3 -> "Insurance Information"
                    else -> ""
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextDarkBlue,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Step 1: Personal Info
            AnimatedVisibility(visible = currentStep == 0) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name") },
                        placeholder = { Text("Enter your first name") },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = if (firstName.isNotEmpty()) PrimaryBlue else TextMediumGray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .focusRequester(firstNameFocus),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { lastNameFocus.requestFocus() }),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name") },
                        placeholder = { Text("Enter your last name") },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = if (lastName.isNotEmpty()) PrimaryBlue else TextMediumGray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .focusRequester(lastNameFocus),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { phoneFocus.requestFocus() }),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        placeholder = { Text("Enter your phone number") },
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = if (phone.isNotEmpty()) PrimaryBlue else TextMediumGray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .focusRequester(phoneFocus),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            // Step 2: Account Info
            AnimatedVisibility(visible = currentStep == 1) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("Enter your email address") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = if (email.isNotEmpty()) PrimaryBlue else TextMediumGray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .focusRequester(emailFocus),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() }),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        placeholder = { Text("Create a password") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = if (password.isNotEmpty()) PrimaryBlue else TextMediumGray) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = if (passwordVisible) PrimaryBlue else TextMediumGray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .focusRequester(passwordFocus),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { confirmPasswordFocus.requestFocus() }),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        placeholder = { Text("Confirm your password") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = if (confirmPassword.isNotEmpty()) PrimaryBlue else TextMediumGray) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    if (confirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                    tint = if (confirmPasswordVisible) PrimaryBlue else TextMediumGray
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .focusRequester(confirmPasswordFocus),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            // Step 3: Address Info
            AnimatedVisibility(visible = currentStep == 2) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Street Address") },
                        placeholder = { Text("Enter your street address") },
                        leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null, tint = if (address.isNotEmpty()) PrimaryBlue else TextMediumGray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .focusRequester(addressFocus),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { cityFocus.requestFocus() }),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        placeholder = { Text("Enter your city") },
                        leadingIcon = { Icon(Icons.Outlined.LocationCity, contentDescription = null, tint = if (city.isNotEmpty()) PrimaryBlue else TextMediumGray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .focusRequester(cityFocus),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { stateFocus.requestFocus() }),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it },
                            label = { Text("State") },
                            placeholder = { Text("State") },
                            leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null, tint = if (state.isNotEmpty()) PrimaryBlue else TextMediumGray) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 16.dp)
                                .focusRequester(stateFocus),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { zipCodeFocus.requestFocus() }),
                            colors = textFieldColors,
                            shape = RoundedCornerShape(16.dp)
                        )

                        OutlinedTextField(
                            value = zipCode,
                            onValueChange = { zipCode = it },
                            label = { Text("Zip Code") },
                            placeholder = { Text("Zip") },
                            leadingIcon = { Icon(Icons.Outlined.Pin, contentDescription = null, tint = if (zipCode.isNotEmpty()) PrimaryBlue else TextMediumGray) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 16.dp)
                                .focusRequester(zipCodeFocus),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            colors = textFieldColors,
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            // Step 4: Insurance Info
            AnimatedVisibility(visible = currentStep == 3) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = insuranceProvider,
                        onValueChange = { insuranceProvider = it },
                        label = { Text("Insurance Provider") },
                        placeholder = { Text("Enter your insurance provider") },
                        leadingIcon = { Icon(Icons.Outlined.Shield, contentDescription = null, tint = if (insuranceProvider.isNotEmpty()) PrimaryBlue else TextMediumGray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .focusRequester(insuranceFocus),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { policyNumberFocus.requestFocus() }),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = policyNumber,
                        onValueChange = { policyNumber = it },
                        label = { Text("Policy Number") },
                        placeholder = { Text("Enter your policy number") },
                        leadingIcon = { Icon(Icons.Outlined.Numbers, contentDescription = null, tint = if (policyNumber.isNotEmpty()) PrimaryBlue else TextMediumGray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .focusRequester(policyNumberFocus),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Text(
                        "Note: You'll be asked to upload your insurance card and ID in the next step.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMediumGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button (if not on first step)
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.width(120.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(
                                colors = listOf(PrimaryBlue, PrimaryPurple)
                            )
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Back", color = PrimaryBlue)
                    }
                } else {
                    // Back to login button (on first step)
                    TextButton(
                        onClick = onBackToLogin,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Back to Login", color = TextMediumGray)
                    }
                }

                // Continue/Submit button
                Button(
                    onClick = {
                        if (validateCurrentStep()) {
                            if (currentStep < totalSteps - 1) {
                                currentStep++
                                coroutineScope.launch {
                                    scrollState.animateScrollTo(0)
                                }
                            } else {
                                // On last step - submit registration
                                val newUser = User(
                                    email = email,
                                    firstName = firstName,
                                    lastName = lastName,
                                    phone = phone,
                                    address = address,
                                    city = city,
                                    state = state,
                                    zipCode = zipCode,
                                    insuranceProvider = insuranceProvider,
                                    policyNumber = policyNumber
                                )

                                authViewModel.signUp(email, password, newUser)
                                onCompleteRegistration()
                            }
                        }
                    },
                    modifier = Modifier.width(150.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    ),
                    enabled = validateCurrentStep() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = if (currentStep < totalSteps - 1) "Continue" else "Create Account",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}