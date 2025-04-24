package com.example.healthride.data.repository

import android.net.Uri
import android.util.Log
import com.example.healthride.data.model.User // Import User
import com.example.healthride.data.model.VerificationStatus // Import Enum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

class MockUserRepository : UserRepository { // Implement the corrected interface
    private val TAG = "MockUserRepo"

    companion object {
        // Use a mutable state for the mock user to simulate updates
        private var mockUserInternal: User? = User( // Initialize with default mock data
            id = "mock_user_123",
            email = "mock@example.com",
            firstName = "Yosef", lastName = "Schmukler", phone = "+1 (555) 000-0000",
            address = "123 Mock St", city = "Mockville", state = "MK", zipCode = "00000",
            insuranceProvider = "Mock Insurance Co", policyNumber = "MOCK12345",
            insuranceCardImageUrl = "", identificationCardImageUrl = "",
            isVerified = false, verificationStatus = VerificationStatus.VERIFIED.name // Use Enum name
        )
        private var loggedIn = false // Separate flag for login status
    }

    // --- Authentication ---
    override suspend fun signUp(email: String, password: String, user: User): Result<User> {
        Log.d(TAG, "Mock signUp: $email")
        delay(300)
        val newUser = user.copy( // Use copy() on the passed 'user' object
            id = "mock_${System.currentTimeMillis()}",
            email = email,
            verificationStatus = VerificationStatus.NOT_SUBMITTED.name // Initial state
        )
        mockUserInternal = newUser // Update the stored user
        loggedIn = true
        return Result.success(newUser)
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        Log.d(TAG, "Mock signIn: $email")
        delay(300)
        return if (email == "mock@example.com" && password == "password") {
            loggedIn = true
            val userToReturn = mockUserInternal ?: User(id="mock_fallback", email=email)
            mockUserInternal = userToReturn
            Log.d(TAG, "Mock signIn success for ${userToReturn.id}")
            Result.success(userToReturn)
        } else {
            Log.w(TAG, "Mock signIn failed")
            Result.failure(Exception("Invalid mock credentials"))
        }
    }

    override suspend fun signOut() {
        Log.d(TAG, "Mock signOut")
        loggedIn = false
        mockUserInternal = null
        // Don't nullify mockUserInternal, just change login state
    }

    override suspend fun getCurrentUser(): User? {
        Log.d(TAG, "Mock getCurrentUser. LoggedIn: $loggedIn")
        // Return the stored user only if logged in
        return if (loggedIn) mockUserInternal else null
    }

    override suspend fun isUserSignedIn(): Boolean {
        Log.d(TAG, "Mock isUserSignedIn: $loggedIn")
        return loggedIn
    }

    // --- Profile ---
    override suspend fun updateUserProfile(user: User): Result<User> {
        Log.d(TAG, "Mock updateUserProfile for ${user.id}")
        val currentUserId = if (loggedIn) mockUserInternal?.id else null
        if (currentUserId != user.id) { // Check if logged in and IDs match
            Log.e(TAG, "Mock updateUserProfile failed: Not logged in or wrong user.")
            return Result.failure(IllegalStateException("User not signed in or invalid user ID"))
        }
        delay(200)
        mockUserInternal = user // Update the stored user
        Log.d(TAG, "Mock updateUserProfile success.")
        return Result.success(user)
    }

    // --- Uploads ---
    override suspend fun uploadInsuranceCard(userId: String, fileUri: Uri): Result<String> {
        Log.d(TAG, "Mock uploadInsuranceCard for $userId, URI: $fileUri")
        val currentUserId = if (loggedIn) mockUserInternal?.id else null
        if (currentUserId != userId) {
            Log.e(TAG, "Mock uploadInsuranceCard failed: Not logged in or wrong user.")
            return Result.failure(IllegalStateException("User not signed in or invalid user ID"))
        }
        delay(800)
        val fakeUrl = "https://mockstorage.example.com/insurance/${userId}.jpg"
        // Update the stored user using copy() safely
        mockUserInternal = mockUserInternal?.copy(insuranceCardImageUrl = fakeUrl)
        Log.d(TAG, "Mock uploadInsuranceCard success. URL: $fakeUrl")
        return Result.success(fakeUrl)
    }

    override suspend fun uploadIdentificationCard(userId: String, fileUri: Uri): Result<String> {
        Log.d(TAG, "Mock uploadIdentificationCard for $userId, URI: $fileUri")
        val currentUserId = if (loggedIn) mockUserInternal?.id else null
        if (currentUserId != userId) {
            Log.e(TAG, "Mock uploadIdentificationCard failed: Not logged in or wrong user.")
            return Result.failure(IllegalStateException("User not signed in or invalid user ID"))
        }
        delay(800)
        val fakeUrl = "https://mockstorage.example.com/id/${userId}.jpg"
        // Update the stored user using copy() safely
        mockUserInternal = mockUserInternal?.copy(identificationCardImageUrl = fakeUrl)
        Log.d(TAG, "Mock uploadIdentificationCard success. URL: $fakeUrl")
        return Result.success(fakeUrl)
    }

    // --- Verification ---
    override suspend fun submitForVerification(user: User): Result<User> {
        Log.d(TAG, "Mock submitForVerification for ${user.id}")
        val currentUserId = if (loggedIn) mockUserInternal?.id else null
        if (currentUserId != user.id) {
            Log.e(TAG, "Mock submitForVerification failed: Not logged in or wrong user.")
            return Result.failure(IllegalStateException("User not signed in or invalid user ID"))
        }

        // Simulate check for required info using the passed 'user' object
        if (user.insuranceCardImageUrl.isBlank() || user.identificationCardImageUrl.isBlank() ||
            user.insuranceProvider.isBlank() || user.policyNumber.isBlank()) {
            Log.w(TAG, "Mock submitForVerification failed: Missing required info/images.")
            return Result.failure(IllegalStateException("Missing required mock verification information or images"))
        }

        delay(400)
        // Update the stored user using copy()
        val updatedUser = user.copy(
            verificationStatus = VerificationStatus.PENDING.name, // Use Enum name
            isVerified = false
        )
        mockUserInternal = updatedUser
        Log.d(TAG, "Mock submitForVerification success. Status set to PENDING.")
        return Result.success(updatedUser)
    }

    override suspend fun getVerificationStatus(): Result<User> {
        Log.d(TAG, "Mock getVerificationStatus")
        val user = if (loggedIn) mockUserInternal else null
        return if (user != null) {
            Log.d(TAG, "Mock getVerificationStatus returning user ${user.id} with status ${user.verificationStatus}")
            Result.success(user) // Return the current state
        } else {
            Log.w(TAG, "Mock getVerificationStatus failed: User not logged in or null.")
            Result.failure(IllegalStateException("Mock user not available"))
        }
    }

    // --- Observing ---
    override fun observeCurrentUser(): Flow<User?> = flow {
        Log.d(TAG, "Mock observeCurrentUser flow started. Emitting: ${if (loggedIn) mockUserInternal?.id else "null"}")
        // Emit the current state immediately based on login status
        emit(if (loggedIn) mockUserInternal else null)
        // Keep the flow open, but Mock doesn't automatically push updates like Firestore listener
        // To simulate updates, you could add delays and re-emit here for testing.
    }
}