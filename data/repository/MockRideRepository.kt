package com.example.healthride.data.repository

import com.example.healthride.data.model.Ride
import com.example.healthride.data.model.RideStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import java.util.UUID

class MockRideRepository : RideRepository {
    // Mock data for testing
    private val upcomingRides = mutableListOf(
        Ride(
            id = "ride1",
            userId = "test_id",
            pickupAddress = "123 Home St, Anytown",
            destinationAddress = "456 Hospital Ave, Medville",
            dateTime = Date(System.currentTimeMillis() + 86400000), // tomorrow
            status = RideStatus.SCHEDULED,
            driverName = "Michael Johnson",
            vehicleInfo = "Toyota Camry - White (ABC123)",
            estimatedPrice = 28.50
        ),
        Ride(
            id = "ride2",
            userId = "test_id",
            pickupAddress = "123 Home St, Anytown",
            destinationAddress = "789 Clinic Rd, Healtown",
            dateTime = Date(System.currentTimeMillis() + 432000000), // 5 days from now
            status = RideStatus.SCHEDULED
        )
    )

    private val pastRides = mutableListOf(
        Ride(
            id = "ride3",
            userId = "test_id",
            pickupAddress = "123 Home St, Anytown",
            destinationAddress = "456 Hospital Ave, Medville",
            dateTime = Date(System.currentTimeMillis() - 86400000), // yesterday
            status = RideStatus.COMPLETED,
            driverName = "Sarah Williams",
            vehicleInfo = "Honda Accord - Silver (XYZ789)",
            estimatedPrice = 32.75
        ),
        Ride(
            id = "ride4",
            userId = "test_id",
            pickupAddress = "123 Home St, Anytown",
            destinationAddress = "321 Therapy Center, Welltown",
            dateTime = Date(System.currentTimeMillis() - 432000000), // 5 days ago
            status = RideStatus.CANCELLED
        )
    )

    override suspend fun scheduleRide(ride: Ride): Result<Ride> {
        val newRide = ride.copy(
            id = UUID.randomUUID().toString(),
            status = RideStatus.SCHEDULED,
            createdAt = Date()
        )
        upcomingRides.add(newRide)
        return Result.success(newRide)
    }

    override suspend fun scheduleRoundTrip(
        pickup: Date,
        dropoff: Date,
        addresses: Pair<String, String>,
        notes: String
    ): Result<Ride> {
        val newRide = Ride(
            id = UUID.randomUUID().toString(),
            userId = "test_id",
            pickupAddress = addresses.first,
            destinationAddress = addresses.second,
            dateTime = pickup,
            status = RideStatus.SCHEDULED,
            specialInstructions = notes,
            createdAt = Date()
        )
        upcomingRides.add(newRide)
        return Result.success(newRide)
    }

    override suspend fun getRide(rideId: String): Result<Ride> {
        val ride = upcomingRides.firstOrNull { it.id == rideId } ?: pastRides.firstOrNull { it.id == rideId }
        return if (ride != null) {
            Result.success(ride)
        } else {
            Result.failure(Exception("Ride not found"))
        }
    }

    override suspend fun cancelRide(rideId: String): Result<Boolean> {
        val rideIndex = upcomingRides.indexOfFirst { it.id == rideId }
        if (rideIndex >= 0) {
            val ride = upcomingRides[rideIndex].copy(status = RideStatus.CANCELLED)
            upcomingRides.removeAt(rideIndex)
            pastRides.add(ride)
            return Result.success(true)
        }
        return Result.failure(Exception("Ride not found or cannot be cancelled"))
    }

    override suspend fun updateRideDetails(ride: Ride): Result<Ride> {
        val rideIndex = upcomingRides.indexOfFirst { it.id == ride.id }
        if (rideIndex >= 0) {
            upcomingRides[rideIndex] = ride
            return Result.success(ride)
        }
        return Result.failure(Exception("Ride not found or cannot be updated"))
    }

    override suspend fun getUpcomingRides(): Result<List<Ride>> {
        return Result.success(upcomingRides)
    }

    override suspend fun getPastRides(): Result<List<Ride>> {
        return Result.success(pastRides)
    }

    override suspend fun searchRidesByDate(startDate: Date, endDate: Date): Result<List<Ride>> {
        val filteredRides = (upcomingRides + pastRides).filter {
            it.dateTime.after(startDate) && it.dateTime.before(endDate)
        }
        return Result.success(filteredRides)
    }

    override fun observeUpcomingRides(): Flow<List<Ride>> = flow {
        emit(upcomingRides)
    }

    override fun observeRide(rideId: String): Flow<Ride?> = flow {
        val ride = upcomingRides.firstOrNull { it.id == rideId } ?: pastRides.firstOrNull { it.id == rideId }
        emit(ride)
    }
}