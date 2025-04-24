package com.example.healthride.data.repository

import android.net.Uri // Make sure this import is added
import com.example.healthride.data.model.User // Import User model
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining operations related to user data, authentication, and verification.
 */
interface UserRepository { // Ensure this line is present only ONCE

    // --- Authentication ---
    suspend fun signUp(email: String, password: String, user: User): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signOut()
    suspend fun getCurrentUser(): User?
    suspend fun isUserSignedIn(): Boolean

    // --- Profile ---
    suspend fun updateUserProfile(user: User): Result<User>

    // --- Document Upload ---
    suspend fun uploadInsuranceCard(userId: String, fileUri: Uri): Result<String>
    suspend fun uploadIdentificationCard(userId: String, fileUri: Uri): Result<String>

    // --- Verification ---
    suspend fun submitForVerification(user: User): Result<User>
    suspend fun getVerificationStatus(): Result<User>

    // --- Realtime Updates ---
    fun observeCurrentUser(): Flow<User?>
}