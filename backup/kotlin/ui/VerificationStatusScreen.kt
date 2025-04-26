package com.example.healthride.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.healthride.data.model.VerificationStatus // Ensure this import is correct
import com.example.healthride.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.BorderStroke // Ensure this import is present
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationStatusScreen(
    status: VerificationStatus = VerificationStatus.PENDING,
    submissionDate: Date = Date(),
    estimatedCompletionDate: Date = Date(System.currentTimeMillis() + 48 * 60 * 60 * 1000),
    onBackClick: () -> Unit = {},
    onResubmit: () -> Unit = {},
    onContactSupport: () -> Unit = {} // Callback is better handled in ViewModel/Navigation if complex actions needed
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val progress = when (status) {
        VerificationStatus.PENDING, VerificationStatus.REJECTED -> 0.5f
        VerificationStatus.VERIFIED -> 1f
        VerificationStatus.NOT_SUBMITTED -> 0f
    }

    val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) // Corrected pattern
    val formattedSubmissionDate = dateFormatter.format(submissionDate)
    val formattedCompletionDate = dateFormatter.format(estimatedCompletionDate)

    Scaffold(
        topBar = {
            if (status != VerificationStatus.PENDING) {
                TopAppBar(
                    title = { Text("Verification Status") },
                    navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Back") } },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SurfaceWhite,
                        titleContentColor = TextDarkBlue
                    )
                )
            }
        }
    ) { paddingValues ->
        val topPadding = paddingValues.calculateTopPadding()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(top = topPadding, bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp), // Use consistent vertical padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            StatusIndicator(status)
            Spacer(Modifier.height(32.dp))
            VerificationProgressTracker(progress, status)
            Spacer(Modifier.height(32.dp))

            if (status != VerificationStatus.NOT_SUBMITTED) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        DetailRow(icon = Icons.Outlined.CalendarToday, title = "Submission Date", value = formattedSubmissionDate)
                        if (status == VerificationStatus.PENDING) {
                            Spacer(Modifier.height(12.dp)); Divider(color = BackgroundGray.copy(alpha=0.7f), thickness = 1.dp); Spacer(Modifier.height(12.dp))
                            DetailRow(icon = Icons.Outlined.Timer, title = "Estimated Completion", value = formattedCompletionDate)
                        }
                        if (status == VerificationStatus.REJECTED) {
                            Spacer(Modifier.height(12.dp)); Divider(color = BackgroundGray.copy(alpha=0.7f), thickness = 1.dp); Spacer(Modifier.height(12.dp))
                            DetailRow(
                                icon = Icons.Outlined.ErrorOutline,
                                title = "Reason",
                                value = "Documents couldn't be verified. Please resubmit or contact support.", // Simpler message
                                valueColor = ErrorRed
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (status) {
                        VerificationStatus.PENDING -> InfoBlue.copy(alpha = 0.05f)
                        VerificationStatus.VERIFIED -> SuccessGreen.copy(alpha = 0.05f)
                        VerificationStatus.REJECTED -> ErrorRed.copy(alpha = 0.05f)
                        else -> SurfaceWhite
                    }
                ),
                border = BorderStroke(1.dp, when (status) {
                    VerificationStatus.PENDING -> InfoBlue.copy(alpha = 0.2f)
                    VerificationStatus.VERIFIED -> SuccessGreen.copy(alpha = 0.2f)
                    VerificationStatus.REJECTED -> ErrorRed.copy(alpha = 0.2f)
                    else -> TextLightGray.copy(alpha = 0.2f)
                })
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = when (status) {
                            VerificationStatus.PENDING -> Icons.Outlined.HourglassTop
                            VerificationStatus.VERIFIED -> Icons.Outlined.CheckCircle
                            VerificationStatus.REJECTED -> Icons.Outlined.HighlightOff
                            else -> Icons.Outlined.Info
                        },
                        contentDescription = null,
                        tint = when (status) {
                            VerificationStatus.PENDING -> InfoBlue
                            VerificationStatus.VERIFIED -> SuccessGreen
                            VerificationStatus.REJECTED -> ErrorRed
                            else -> TextMediumGray
                        },
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = when (status) {
                            VerificationStatus.PENDING -> "Almost There..."
                            VerificationStatus.VERIFIED -> "You're All Set!"
                            VerificationStatus.REJECTED -> "Action Required"
                            else -> "Verification Needed"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = when (status) {
                            VerificationStatus.PENDING -> InfoBlue
                            VerificationStatus.VERIFIED -> SuccessGreen
                            VerificationStatus.REJECTED -> ErrorRed
                            else -> TextDarkBlue
                        },
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = when (status) {
                            VerificationStatus.PENDING -> "We're reviewing your documents. Sit tight! This usually takes 24-48 hours."
                            VerificationStatus.VERIFIED -> "Your account is verified and ready for booking rides!" // Simplified
                            VerificationStatus.REJECTED -> "Looks like there was an issue. Please resubmit your documents or contact support for help."
                            else -> "Please upload your insurance card and ID to book rides covered by insurance."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextDarkBlue,
                        textAlign = TextAlign.Center
                    )
                }
            } // End info card

            Spacer(Modifier.height(24.dp))

            // Action button area
            when (status) {
                VerificationStatus.PENDING, VerificationStatus.VERIFIED -> {
                    Spacer(Modifier.height(56.dp)) // Reserve space even if no button
                }
                VerificationStatus.REJECTED -> {
                    Button( onClick = onResubmit, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp), colors = ButtonDefaults.buttonColors( containerColor = PrimaryBlue ) ) { Text("Resubmit Documents", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:")
                                putExtra(Intent.EXTRA_EMAIL, arrayOf("support@healthride.app")) // Use correct email
                                putExtra(Intent.EXTRA_SUBJECT, "Verification Assistance Needed")
                            }
                            try { context.startActivity(emailIntent); onContactSupport() } catch (e: Exception) { Log.e("VerificationStatus", "Email app launch failed", e) /* Handle error */ }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp)
                    ) { Text("Contact Support", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue) }
                }
                VerificationStatus.NOT_SUBMITTED -> {
                    Button( onClick = onResubmit, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp), colors = ButtonDefaults.buttonColors( containerColor = PrimaryBlue ) ) { Text("Start Verification", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

// Simple Detail Row Composable (Helper)
@Composable
private fun DetailRow(icon: ImageVector, title: String, value: String, valueColor: Color = TextDarkBlue) {
    Row( modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top ) {
        Icon(icon, null, tint = TextMediumGray, modifier = Modifier.size(20.dp).padding(top = 2.dp))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodySmall, color = TextMediumGray)
            Spacer(Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = valueColor)
        }
    }
}

// StatusIndicator, VerificationProgressTracker, StepItem composables
@Composable fun StatusIndicator(status: VerificationStatus) {
    val colors = when (status) {
        VerificationStatus.PENDING -> InfoBlue
        VerificationStatus.VERIFIED -> SuccessGreen
        VerificationStatus.REJECTED -> ErrorRed
        VerificationStatus.NOT_SUBMITTED -> TextMediumGray
    }
    val text = when (status) {
        VerificationStatus.PENDING -> "PENDING"
        VerificationStatus.VERIFIED -> "VERIFIED"
        VerificationStatus.REJECTED -> "ACTION REQUIRED"
        VerificationStatus.NOT_SUBMITTED -> "NOT SUBMITTED"
    }
    val icon = when (status) {
        VerificationStatus.PENDING -> Icons.Filled.HourglassEmpty
        VerificationStatus.VERIFIED -> Icons.Filled.CheckCircle
        VerificationStatus.REJECTED -> Icons.Filled.Error
        VerificationStatus.NOT_SUBMITTED -> Icons.Filled.Info
    }
    Surface( shape = RoundedCornerShape(50.dp), color = colors.copy(alpha = 0.1f) ) {
        Row( Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically ) {
            Icon( icon, null, tint = colors, modifier = Modifier.size(16.dp) )
            Spacer(Modifier.width(8.dp))
            Text( text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = colors ) // Use LabelMedium
        }
    }
}
@Composable fun VerificationProgressTracker(progress: Float, status: VerificationStatus) {
    val steps = listOf("Submitted", "Reviewing", "Verified")
    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = when (status) {
                VerificationStatus.PENDING -> InfoBlue
                VerificationStatus.VERIFIED -> SuccessGreen
                VerificationStatus.REJECTED -> ErrorRed
                else -> TextLightGray
            },
            trackColor = BackgroundGray.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(16.dp))
        Row( Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween ) {
            steps.forEachIndexed { index, step ->
                val isCompleted = when { /* ... logic as before ... */
                    status == VerificationStatus.VERIFIED -> true
                    status == VerificationStatus.REJECTED && index < 2 -> true
                    status == VerificationStatus.PENDING && index == 0 -> true
                    else -> false
                }
                val isActive = when { /* ... logic as before ... */
                    status == VerificationStatus.PENDING && index == 1 -> true
                    status == VerificationStatus.NOT_SUBMITTED && index == 0 -> true
                    else -> false
                }
                StepItem( step = step, isCompleted = isCompleted, isActive = isActive, status = status )
            }
        }
    }
}
@Composable fun StepItem( step: String, isCompleted: Boolean, isActive: Boolean, status: VerificationStatus ) {
    Column( horizontalAlignment = Alignment.CenterHorizontally ) {
        Box(
            modifier = Modifier.size(24.dp).clip(CircleShape)
                .background( when { /* ... logic as before ... */
                    isCompleted -> when (status) {
                        VerificationStatus.VERIFIED -> SuccessGreen
                        VerificationStatus.REJECTED -> if (step == "Verified") ErrorRed else InfoBlue
                        else -> InfoBlue
                    }
                    isActive -> if(status == VerificationStatus.NOT_SUBMITTED) TextLightGray.copy(alpha = 0.3f) else InfoBlue.copy(alpha = 0.1f)
                    else -> BackgroundGray
                }
                ), contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon( if (status == VerificationStatus.REJECTED && step == "Verified") Icons.Filled.Close else Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(16.dp) )
            } else if (isActive && status == VerificationStatus.PENDING) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(InfoBlue))
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = step, style = MaterialTheme.typography.bodySmall,
            color = when { /* ... logic as before ... */
                isCompleted && status == VerificationStatus.REJECTED && step == "Verified" -> ErrorRed
                isCompleted || isActive -> TextDarkBlue
                else -> TextMediumGray
            },
            fontWeight = if (isCompleted || isActive) FontWeight.Medium else FontWeight.Normal
        )
    }
}