package com.example.healthride.data.repository

import com.example.healthride.data.model.Ride
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface RideRepository {
    // Ride scheduling operations
    suspend fun scheduleRide(ride: Ride): Result<Ride>
    suspend fun scheduleRoundTrip(pickup: Date, dropoff: Date, addresses: Pair<String, String>, notes: String): Result<Ride>

    // Ride management
    suspend fun getRide(rideId: String): Result<Ride>
    suspend fun cancelRide(rideId: String): Result<Boolean>
    suspend fun updateRideDetails(ride: Ride): Result<Ride>

    // Ride retrieval
    suspend fun getUpcomingRides(): Result<List<Ride>>
    suspend fun getPastRides(): Result<List<Ride>>
    suspend fun searchRidesByDate(startDate: Date, endDate: Date): Result<List<Ride>>

    // Observing data changes
    fun observeUpcomingRides(): Flow<List<Ride>>
    fun observeRide(rideId: String): Flow<Ride?>
}