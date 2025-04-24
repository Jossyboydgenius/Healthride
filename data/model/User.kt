package com.example.healthride.data.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.IgnoreExtraProperties

// Define the Enum here
enum class VerificationStatus {
    PENDING, VERIFIED, REJECTED, NOT_SUBMITTED // Added NOT_SUBMITTED as a potential initial state
}

@IgnoreExtraProperties
data class User(
    val id: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val insuranceProvider: String = "",
    val policyNumber: String = "",
    val insuranceCardImageUrl: String = "",
    val identificationCardImageUrl: String = "",

    // Use @PropertyName for Firebase mapping of boolean fields starting with 'is'
    @get:PropertyName("isVerified") // Correct getter naming
    @set:PropertyName("isVerified") // Correct setter naming
    var isVerified: Boolean = false, // Keep as Boolean

    // Store status as a String, matching the Enum values (or use NOT_SUBMITTED)
    var verificationStatus: String = VerificationStatus.NOT_SUBMITTED.name, // Use Enum value name

    val createdAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor required by Firestore
    constructor() : this(
        id = "", email = "", firstName = "", lastName = "", phone = "",
        address = "", city = "", state = "", zipCode = "",
        insuranceProvider = "", policyNumber = "",
        insuranceCardImageUrl = "", identificationCardImageUrl = "",
        isVerified = false, verificationStatus = VerificationStatus.NOT_SUBMITTED.name, // Consistent default
        createdAt = 0L // Use Long literal
    )

    // Computed property for full name
    val fullName: String get() = "$firstName $lastName".trim() // trim() handles empty names
}