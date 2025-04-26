package com.example.healthride.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthride.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsurancePaymentScreen(
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Animation states
    var showContent by remember { mutableStateOf(false) }
    var selectedSection by remember { mutableStateOf(0) }

    // Trigger animations
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    // Insurance info - sample data
    val insuranceInfo = remember {
        InsuranceInfo(
            provider = "BlueCross BlueShield",
            planType = "PPO Gold",
            memberID = "XYZ123456789",
            groupNumber = "G-5555555",
            expirationDate = "12/31/2025",
            status = "Active"
        )
    }

    // Payment methods - sample data
    val paymentMethods = remember {
        listOf(
            PaymentMethod(
                id = "pm_1",
                type = PaymentType.CREDIT_CARD,
                name = "Visa ending in 4242",
                details = "Expires 12/25",
                isDefault = true
            ),
            PaymentMethod(
                id = "pm_2",
                type = PaymentType.INSURANCE,
                name = "BlueCross BlueShield",
                details = "Billing through insurance",
                isDefault = false
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedSection == 0) "Insurance Information" else "Payment Methods",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = TextDarkBlue
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundGray)
        ) {
            // Section tabs
            TabRow(
                selectedTabIndex = selectedSection,
                containerColor = SurfaceWhite,
                contentColor = PrimaryBlue,
                divider = {
                    Divider(
                        thickness = 2.dp,
                        color = Color(0xFFEEEEEE)
                    )
                },
                indicator = { tabPositions ->
                    // Custom tab indicator
                    if (selectedSection < tabPositions.size) {
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedSection])
                                .height(3.dp)
                                .padding(horizontal = 40.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            PrimaryBlue,
                                            PrimaryPurple
                                        )
                                    ),
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
                        )
                    }
                }
            ) {
                // Insurance tab
                Tab(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    text = {
                        Text(
                            text = "Insurance",
                            fontWeight = if (selectedSection == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.HealthAndSafety,
                            contentDescription = null,
                            tint = if (selectedSection == 0) PrimaryBlue else TextMediumGray
                        )
                    }
                )

                // Payment Methods tab
                Tab(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    text = {
                        Text(
                            text = "Payment",
                            fontWeight = if (selectedSection == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Payment,
                            contentDescription = null,
                            tint = if (selectedSection == 1) PrimaryBlue else TextMediumGray
                        )
                    }
                )
            }

            // Content
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
            ) {
                when (selectedSection) {
                    0 -> InsuranceSection(insuranceInfo)
                    1 -> PaymentMethodsSection(paymentMethods)
                }
            }
        }
    }
}

@Composable
fun InsuranceSection(insuranceInfo: InsuranceInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Insurance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceWhite
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Card header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = insuranceInfo.provider,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkBlue
                    )

                    Surface(
                        color = SuccessGreen.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = insuranceInfo.status,
                                style = MaterialTheme.typography.labelMedium,
                                color = SuccessGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Main card content
                InsuranceDetailItem("Plan Type", insuranceInfo.planType, Icons.Outlined.Policy)
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                InsuranceDetailItem("Member ID", insuranceInfo.memberID, Icons.Outlined.Badge)
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                InsuranceDetailItem("Group Number", insuranceInfo.groupNumber, Icons.Outlined.Group)
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                InsuranceDetailItem("Expiration", insuranceInfo.expirationDate, Icons.Outlined.CalendarToday)
            }
        }

        // Verification Status
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = PrimaryBlueLight.copy(alpha = 0.3f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Verified,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Verified Account",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = TextDarkBlue
                    )

                    Text(
                        text = "Your insurance has been verified and is active for rides",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMediumGray
                    )
                }
            }
        }

        // Add/Edit Button
        Button(
            onClick = { /* Navigate to edit insurance */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Update Insurance Information",
                fontWeight = FontWeight.Bold
            )
        }

        // Coverage Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceWhite
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Coverage Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Coverage details
                CoverageItem(
                    title = "Medical Transport",
                    covered = true,
                    details = "Covered at 100% with prior authorization"
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                CoverageItem(
                    title = "Non-Emergency Transport",
                    covered = true,
                    details = "Covered at 80%, $25 copay may apply"
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                CoverageItem(
                    title = "Out of Network",
                    covered = false,
                    details = "Not covered"
                )
            }
        }
    }
}

@Composable
fun PaymentMethodsSection(paymentMethods: List<PaymentMethod>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Payment methods
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceWhite
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Payment Methods",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                // List of payment methods
                paymentMethods.forEachIndexed { index, method ->
                    PaymentMethodItem(
                        paymentMethod = method,
                        onClick = { /* Handle click */ }
                    )

                    if (index < paymentMethods.size - 1) {
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
                    }
                }
            }
        }

        // Add Payment Method Button
        Button(
            onClick = { /* Navigate to add payment method */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Add Payment Method",
                fontWeight = FontWeight.Bold
            )
        }

        // Billing History Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceWhite
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Billing History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkBlue
                    )

                    TextButton(onClick = { /* View all */ }) {
                        Text(
                            text = "View All",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Recent transactions
                TransactionItem(
                    date = "Mar 25, 2025",
                    description = "Ride to Medical Center",
                    amount = "$28.50",
                    status = "Covered by Insurance"
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                TransactionItem(
                    date = "Mar 10, 2025",
                    description = "Ride to City Hospital",
                    amount = "$32.75",
                    status = "Covered by Insurance"
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                TransactionItem(
                    date = "Feb 28, 2025",
                    description = "Ride to Therapy Center",
                    amount = "$15.00",
                    status = "Credit Card Payment"
                )
            }
        }
    }
}

@Composable
fun InsuranceDetailItem(label: String, value: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PrimaryBlueLight.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextMediumGray
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = TextDarkBlue
            )
        }
    }
}

@Composable
fun CoverageItem(title: String, covered: Boolean, details: String) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (covered) SuccessGreen.copy(alpha =.1f) else ErrorRed.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (covered) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = null,
                tint = if (covered) SuccessGreen else ErrorRed,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = TextDarkBlue
            )

            Text(
                text = details,
                style = MaterialTheme.typography.bodySmall,
                color = TextMediumGray
            )
        }
    }
}

@Composable
fun PaymentMethodItem(paymentMethod: PaymentMethod, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when (paymentMethod.type) {
                        PaymentType.CREDIT_CARD -> PrimaryBlueLight.copy(alpha = 0.5f)
                        PaymentType.INSURANCE -> PrimaryPurpleLight.copy(alpha = 0.5f)
                        PaymentType.PAYPAL -> Color(0xFFE7F0FF)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (paymentMethod.type) {
                    PaymentType.CREDIT_CARD -> Icons.Outlined.CreditCard
                    PaymentType.INSURANCE -> Icons.Outlined.HealthAndSafety
                    PaymentType.PAYPAL -> Icons.Outlined.AccountBalance
                },
                contentDescription = null,
                tint = when (paymentMethod.type) {
                    PaymentType.CREDIT_CARD -> PrimaryBlue
                    PaymentType.INSURANCE -> PrimaryPurple
                    PaymentType.PAYPAL -> Color(0xFF0070BA) // PayPal blue
                },
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Payment info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = paymentMethod.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = TextDarkBlue
                )

                if (paymentMethod.isDefault) {
                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = PrimaryBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            text = "DEFAULT",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Text(
                text = paymentMethod.details,
                style = MaterialTheme.typography.bodySmall,
                color = TextMediumGray
            )
        }

        // Edit button
        IconButton(onClick = { /* Edit payment method */ }) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit payment method",
                tint = TextMediumGray
            )
        }
    }
}

@Composable
fun TransactionItem(date: String, description: String, amount: String, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date column
        Column(
            modifier = Modifier.width(70.dp)
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = TextMediumGray,
                fontWeight = FontWeight.Medium
            )
        }

        // Details column
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TextDarkBlue,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall,
                color = TextMediumGray
            )
        }

        // Amount
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextDarkBlue
        )
    }
}

// Data classes

data class InsuranceInfo(
    val provider: String,
    val planType: String,
    val memberID: String,
    val groupNumber: String,
    val expirationDate: String,
    val status: String
)

data class PaymentMethod(
    val id: String,
    val type: PaymentType,
    val name: String,
    val details: String,
    val isDefault: Boolean
)

enum class PaymentType {
    CREDIT_CARD, INSURANCE, PAYPAL
}