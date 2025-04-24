package com.example.healthride.data.repository

import com.example.healthride.data.model.Ride
import com.example.healthride.data.model.RideStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class RideRepositoryImpl : RideRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val ridesCollection = firestore.collection("rides")

    override suspend fun scheduleRide(ride: Ride): Result<Ride> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("User not signed in"))

            // Create a new document in the rides collection
            val rideRef = ridesCollection.document()
            val newRide = ride.copy(
                id = rideRef.id,
                userId = userId,
                status = RideStatus.SCHEDULED,
                createdAt = Date()
            )

            rideRef.set(newRide).await()
            Result.success(newRide)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun scheduleRoundTrip(
        pickup: Date,
        dropoff: Date,
        addresses: Pair<String, String>,
        notes: String
    ): Result<Ride> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("User not signed in"))

            // Create a ride with round trip details
            val rideRef = ridesCollection.document()
            val newRide = Ride(
                id = rideRef.id,
                userId = userId,
                pickupAddress = addresses.first,
                destinationAddress = addresses.second,
                dateTime = pickup,
                specialInstructions = notes,
                status = RideStatus.SCHEDULED,
                createdAt = Date()
            )

            rideRef.set(newRide).await()
            Result.success(newRide)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRide(rideId: String): Result<Ride> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("User not signed in"))

            val rideDoc = ridesCollection.document(rideId).get().await()
            val ride = rideDoc.toObject(Ride::class.java) ?: return Result.failure(IllegalStateException("Ride not found"))

            // Security check - ensure the ride belongs to the current user
            if (ride.userId != userId) {
                return Result.failure(IllegalStateException("Access denied"))
            }

            Result.success(ride)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelRide(rideId: String): Result<Boolean> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("User not signed in"))

            // Get the ride to verify ownership
            val rideDoc = ridesCollection.document(rideId).get().await()
            val ride = rideDoc.toObject(Ride::class.java) ?: return Result.failure(IllegalStateException("Ride not found"))

            // Security check - ensure the ride belongs to the current user
            if (ride.userId != userId) {
                return Result.failure(IllegalStateException("Access denied"))
            }

            // Only scheduled rides can be cancelled
            if (ride.status != RideStatus.SCHEDULED) {
                return Result.failure(IllegalStateException("Only scheduled rides can be cancelled"))
            }

            ridesCollection.document(rideId)
                .update("status", RideStatus.CANCELLED)
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRideDetails(ride: Ride): Result<Ride> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("User not signed in"))

            // Security check - ensure the ride belongs to the current user
            if (ride.userId != userId) {
                return Result.failure(IllegalStateException("Access denied"))
            }

            // Only allow updates to scheduled rides
            val existingRideDoc = ridesCollection.document(ride.id).get().await()
            val existingRide = existingRideDoc.toObject(Ride::class.java)
                ?: return Result.failure(IllegalStateException("Ride not found"))

            if (existingRide.status != RideStatus.SCHEDULED) {
                return Result.failure(IllegalStateException("Cannot update a ride that is already in progress or completed"))
            }

            // Update the ride
            ridesCollection.document(ride.id).set(ride).await()
            Result.success(ride)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUpcomingRides(): Result<List<Ride>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("User not signed in"))

            val upcomingRides = ridesCollection
                .whereEqualTo("userId", userId)
                .whereIn("status", listOf(RideStatus.SCHEDULED.name, RideStatus.IN_PROGRESS.name))
                .orderBy("dateTime", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(Ride::class.java)

            Result.success(upcomingRides)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPastRides(): Result<List<Ride>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("User not signed in"))

            val pastRides = ridesCollection
                .whereEqualTo("userId", userId)
                .whereIn("status", listOf(RideStatus.COMPLETED.name, RideStatus.CANCELLED.name))
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Ride::class.java)

            Result.success(pastRides)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchRidesByDate(startDate: Date, endDate: Date): Result<List<Ride>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("User not signed in"))

            val rides = ridesCollection
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("dateTime", startDate)
                .whereLessThanOrEqualTo("dateTime", endDate)
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Ride::class.java)

            Result.success(rides)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeUpcomingRides(): Flow<List<Ride>> = callbackFlow {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listenerRegistration = ridesCollection
            .whereEqualTo("userId", userId)
            .whereIn("status", listOf(RideStatus.SCHEDULED.name, RideStatus.IN_PROGRESS.name))
            .orderBy("dateTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val rides = snapshot?.toObjects(Ride::class.java) ?: emptyList()
                trySend(rides)
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun observeRide(rideId: String): Flow<Ride?> = callbackFlow {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listenerRegistration = ridesCollection.document(rideId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val ride = snapshot?.toObject(Ride::class.java)

                // Security check - only send rides that belong to the current user
                if (ride != null && ride.userId == userId) {
                    trySend(ride)
                } else {
                    trySend(null)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }
}