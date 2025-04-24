package com.example.healthride.data.model

import java.util.Date

// Ride Type Options
enum class RideType {
    AMBULATORY,
    WHEELCHAIR,
    STRETCHER  // Add this line
}

// Stores all info about a single ride request
data class Ride(
    val id: String = "",
    val userId: String = "", // Who booked it

    // Location & Time
    val pickupAddress: String = "",
    val destinationAddress: String = "",
    val dateTime: Date = Date(), // Pickup time

    // Ride Status
    val status: RideStatus = RideStatus.SCHEDULED,

    // Passenger & Ride Details
    val passengerName: String = "", // Name of person riding (defaults to user)
    val numberOfPassengers: Int = 1, // 1 (patient) or 2 (patient + companion)
    val rideType: String = RideType.AMBULATORY.name, // "AMBULATORY" or "WHEELCHAIR"
    val specialInstructions: String? = null, // For the "more info" box
    val appointmentType: String = "", // Optional: Type of appointment

    // Driver & Vehicle Details (Filled in later by backend/driver)
    val driverId: String? = null,
    val driverName: String? = null,
    val driverPhone: String? = null,
    val driverPhotoUrl: String? = null,
    val vehicleInfo: String? = null,

    // Pricing & Metadata
    val estimatedPrice: Double? = null, // Optional: Show estimated cost
    val actualPrice: Double? = null, // Optional: Final cost after ride
    val medicallyCovered: Boolean = true, // Default assumption
    val insuranceVerified: Boolean = false, // Verification status
    val createdAt: Date = Date() // When the ride was booked
)

// Ride Status Options
enum class RideStatus {
    SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
}