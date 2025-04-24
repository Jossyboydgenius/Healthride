package com.example.healthride.data.repository

import android.net.Uri
import android.util.Log
import com.example.healthride.data.model.User // Correct import
import com.example.healthride.data.model.VerificationStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage // Keep import, though code is commented out
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlinx.coroutines.delay // Added for simulating delay

class UserRepositoryImpl : UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    // Keep storage instance even if not used now, for easier uncommenting later
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val usersCollection = firestore.collection("users")
    private val TAG = "UserRepoImpl"

    // --- Authentication (Keep as corrected in previous step) ---
    override suspend fun signUp(email: String, password: String, user: User): Result<User> {
        return try {
            Log.d(TAG, "Attempting sign up for email: $email")
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: run {
                return Result.failure(IllegalStateException("Failed to get user ID"))
            }
            val newUser = user.copy(
                id = userId,
                email = email,
                verificationStatus = VerificationStatus.NOT_SUBMITTED.name
            )
            usersCollection.document(userId).set(newUser).await()
            Result.success(newUser)
        } catch (e: Exception) {
            Log.e(TAG, "Sign up failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            Log.d(TAG, "Attempting sign in for email: $email")
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: run {
                return Result.failure(IllegalStateException("Failed to get user ID"))
            }
            Log.d(TAG, "Firebase Auth successful. User ID: $userId")
            Log.d(TAG, "Fetching Firestore profile for user ID: $userId")
            val userDoc = usersCollection.document(userId).get().await()

            if (userDoc.exists()) {
                val userProfile = userDoc.toObject(User::class.java)
                if (userProfile != null) {
                    Result.success(userProfile)
                } else {
                    Result.success(User(id = userId, email = email)) // Basic fallback
                }
            } else {
                Result.success(User(id = userId, email = email)) // Basic fallback
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sign in failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        try { auth.signOut() } catch (e: Exception) { Log.e(TAG, "Sign out error", e) }
    }

    override suspend fun getCurrentUser(): User? {
        val userId = auth.currentUser?.uid ?: return null
        return try { usersCollection.document(userId).get().await().toObject(User::class.java) }
        catch (e: Exception) { Log.e(TAG, "Error fetch profile", e); null }
    }

    override suspend fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    // --- Profile Update (Keep as corrected) ---
    override suspend fun updateUserProfile(user: User): Result<User> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("User not signed in"))
            if (userId != user.id) return Result.failure(IllegalStateException("Cannot update another user's profile"))
            usersCollection.document(userId).set(user).await()
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating profile for user ${user.id}: ${e.message}", e)
            Result.failure(e)
        }
    }

    // --- Document Upload (Modified to bypass Storage) ---

    override suspend fun uploadInsuranceCard(userId: String, fileUri: Uri): Result<String> {
        Log.d(TAG, "[BYPASS] Request to upload insurance card for user: $userId (URI: $fileUri)")
        return uploadFile(userId, fileUri, "insurance_cards") // Still call helper
    }

    override suspend fun uploadIdentificationCard(userId: String, fileUri: Uri): Result<String> {
        Log.d(TAG, "[BYPASS] Request to upload identification card for user: $userId (URI: $fileUri)")
        return uploadFile(userId, fileUri, "identification_cards") // Still call helper
    }

    /**
     * Private helper function - MODIFIED TO BYPASS ACTUAL UPLOAD.
     * Returns a placeholder URL immediately.
     */
    private suspend fun uploadFile(userId: String, fileUri: Uri, folder: String): Result<String> {
        Log.d(TAG, "[BYPASS] Simulating upload for $folder, user $userId.")
        // Simulate network delay
        delay(500) // Short delay to mimic processing

        // --- Firebase Storage Code (Commented Out) ---
        /*
        return try {
            val storageRef = storage.reference.child("$folder/$userId.jpg")
            Log.d(TAG, "Uploading file to Storage path: ${storageRef.path}")
            storageRef.putFile(fileUri).await()
            Log.d(TAG, "File upload successful for path: ${storageRef.path}")
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Log.d(TAG, "Successfully obtained download URL: $downloadUrl")
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading file to $folder for user $userId: ${e.message}", e)
            Result.failure(e)
        }
        */
        // --- End Commented Out Code ---

        // --- Bypass Code ---
        // Return a hardcoded placeholder URL instead of uploading
        val placeholderUrl = "https://storage.placeholder.com/$folder/$userId.jpg" // Fake URL
        Log.d(TAG, "[BYPASS] Returning placeholder URL: $placeholderUrl")
        return Result.success(placeholderUrl)
        // --- End Bypass Code ---
    }

    // --- Verification (Keep as corrected) ---
    override suspend fun submitForVerification(user: User): Result<User> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("User not signed in"))
            if (userId != user.id) return Result.failure(IllegalStateException("Cannot update another user's profile"))

            // Check for non-blank URLs (will be placeholders now)
            if (user.insuranceCardImageUrl.isBlank() || user.identificationCardImageUrl.isBlank()) {
                return Result.failure(IllegalStateException("Missing required verification images. Please upload both documents."))
            }
            if (user.insuranceProvider.isBlank() || user.policyNumber.isBlank()) {
                return Result.failure(IllegalStateException("Missing required insurance information."))
            }

            val userToSave = user.copy(
                verificationStatus = VerificationStatus.PENDING.name,
                isVerified = false
            )
            Log.d(TAG, "Submitting user $userId for verification (with placeholder URLs). Status: PENDING.")
            usersCollection.document(userId).set(userToSave).await()
            Result.success(userToSave)

        } catch (e: Exception) {
            Log.e(TAG, "Error submitting for verification for user ${user.id}: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getVerificationStatus(): Result<User> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("User not signed in"))
            val userDoc = usersCollection.document(userId).get().await()
            val user = userDoc.toObject(User::class.java) ?: return Result.failure(IllegalStateException("User profile not found"))
            Log.d(TAG, "Verification status fetched: ${user.verificationStatus}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting verification status: ${e.message}", e)
            Result.failure(e)
        }
    }

    // --- Realtime Updates (Keep as corrected) ---
    override fun observeCurrentUser(): Flow<User?> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) { trySend(null); close(); return@callbackFlow }

        val listenerRegistration = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.toObject(User::class.java))
            }
        awaitClose { listenerRegistration.remove() }
    }
}