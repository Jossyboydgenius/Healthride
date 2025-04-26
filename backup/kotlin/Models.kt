package com.example.healthride

// Import the CORRECT Ride and RideStatus models
import com.example.healthride.data.model.Ride
import com.example.healthride.data.model.RideStatus
import com.example.healthride.data.model.RideType // Keep RideType if used by SampleData
import java.text.SimpleDateFormat
import java.util.*

// REMOVE the local Ride data class and RideStatus enum definitions from this file if they exist.

// Helper function to format date (Keep this)
fun Date.formatToPattern(pattern: String): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

// Sample data for the app using the CORRECT models
object SampleData {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

    // Create upcoming rides using data.model.Ride
    val upcomingRides: List<Ride> = listOf(
        Ride(
            id = "R1001",
            userId = "sample_user", // Add userId if needed by the model
            pickupAddress = "123 Home St, Anytown",
            destinationAddress = "456 Hospital Ave, Medville",
            dateTime = dateFormat.parse("2025-03-25 09:30")!!,
            status = RideStatus.SCHEDULED, // Uses data.model.RideStatus
            driverName = "Michael Johnson",
            vehicleInfo = "Toyota Camry - White (ABC123)",
            passengerName = "Yosef Schmukler", // Add required fields from data.model.Ride
            rideType = RideType.AMBULATORY.name
        ),
        Ride(
            id = "R1002",
            userId = "sample_user",
            pickupAddress = "123 Home St, Anytown",
            destinationAddress = "789 Clinic Rd, Healtown",
            dateTime = dateFormat.parse("2025-03-30 14:15")!!,
            status = RideStatus.SCHEDULED, // Uses data.model.RideStatus
            passengerName = "Yosef Schmukler",
            rideType = RideType.AMBULATORY.name
        )
    )

    // Create past rides using data.model.Ride
    val pastRides: List<Ride> = listOf(
        Ride(
            id = "R1000",
            userId = "sample_user",
            pickupAddress = "123 Home St, Anytown",
            destinationAddress = "456 Hospital Ave, Medville",
            dateTime = dateFormat.parse("2025-03-10 10:00")!!,
            status = RideStatus.COMPLETED, // Uses data.model.RideStatus
            driverName = "Sarah Williams",
            vehicleInfo = "Honda Accord - Silver (XYZ789)",
            actualPrice = 28.50, // Use actualPrice if that's the field in data.model.Ride
            passengerName = "Yosef Schmukler",
            rideType = RideType.AMBULATORY.name
        ),
        Ride(
            id = "R999",
            userId = "sample_user",
            pickupAddress = "123 Home St, Anytown",
            destinationAddress = "321 Therapy Center, Welltown",
            dateTime = dateFormat.parse("2025-03-05 13:45")!!,
            status = RideStatus.COMPLETED, // Uses data.model.RideStatus
            driverName = "Robert Davis",
            vehicleInfo = "Hyundai Sonata - Black (DEF456)",
            actualPrice = 32.75,
            passengerName = "Yosef Schmukler",
            rideType = RideType.AMBULATORY.name
        ),
        Ride(
            id = "R998",
            userId = "sample_user",
            pickupAddress = "123 Home St, Anytown",
            destinationAddress = "987 Medical Building, Careville",
            dateTime = dateFormat.parse("2025-02-28 16:30")!!,
            status = RideStatus.CANCELLED, // Uses data.model.RideStatus
            passengerName = "Yosef Schmukler",
            rideType = RideType.AMBULATORY.name
        )
    )
}