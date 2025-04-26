package com.example.healthride.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthride.data.model.User
import com.example.healthride.data.repository.RepositoryProvider
import com.example.healthride.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val userRepository: UserRepository = RepositoryProvider.getUserRepository()
    private val TAG = "UserViewModel"

    // --- LiveData for UI State ---
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _insuranceCardUrl = MutableLiveData<String?>()
    val insuranceCardUrl: LiveData<String?> = _insuranceCardUrl

    private val _idCardUrl = MutableLiveData<String?>()
    val idCardUrl: LiveData<String?> = _idCardUrl

    private val _verificationSubmitted = MutableLiveData<Boolean>()
    val verificationSubmitted: LiveData<Boolean> = _verificationSubmitted

    init {
        loadCurrentUser() // Load user when ViewModel is created
    }

    // --- Core Functions ---

    fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d(TAG, "Attempting to load current user")
                val user = userRepository.getCurrentUser()
                _currentUser.value = user
                // Reset relevant states based on loaded user data
                _insuranceCardUrl.value = user?.insuranceCardImageUrl?.takeIf { it.isNotBlank() }
                _idCardUrl.value = user?.identificationCardImageUrl?.takeIf { it.isNotBlank() }
                _verificationSubmitted.value = user?.verificationStatus == "PENDING" || user?.verificationStatus == "VERIFIED"
                Log.d(TAG, "User loaded: ${user?.id}, Status: ${user?.verificationStatus}")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading current user: ${e.message}", e)
                _error.value = "Failed to load user data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadInsuranceCard(fileUri: Uri) {
        val userId = _currentUser.value?.id ?: return Unit.also { _error.value = "User not logged in" }
        Log.d(TAG, "Starting insurance card upload for user $userId")
        performUpload(userId, fileUri, isInsurance = true)
    }

    fun uploadIdCard(fileUri: Uri) {
        val userId = _currentUser.value?.id ?: return Unit.also { _error.value = "User not logged in" }
        Log.d(TAG, "Starting ID card upload for user $userId")
        performUpload(userId, fileUri, isInsurance = false)
    }

    fun submitForVerification() {
        val user = _currentUser.value ?: return Unit.also { _error.value = "User data not available" }
        Log.d(TAG, "Submit button triggered for user ${user.id}")

        // Ensure the URLs we have locally are in the user object before submitting
        val userToSubmit = user.copy(
            insuranceCardImageUrl = _insuranceCardUrl.value ?: user.insuranceCardImageUrl,
            identificationCardImageUrl = _idCardUrl.value ?: user.identificationCardImageUrl
        )

        // Client-side check before calling repository
        if (userToSubmit.insuranceCardImageUrl.isBlank() || userToSubmit.identificationCardImageUrl.isBlank()) {
            _error.value = "Please upload both Insurance Card and ID Card images."
            Log.w(TAG,"Verification submission aborted: Missing images locally.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            Log.d(TAG, "Calling repository to submit verification for user ${userToSubmit.id}")
            val result = userRepository.submitForVerification(userToSubmit)
            result.fold(
                onSuccess = { updatedUser ->
                    Log.d(TAG, "Verification submission successful. User status: ${updatedUser.verificationStatus}")
                    _currentUser.value = updatedUser // Update local state
                    _verificationSubmitted.value = true // Signal success to UI
                },
                onFailure = { exception ->
                    Log.e(TAG, "Verification submission failed: ${exception.message}", exception)
                    _error.value = "Verification submission failed: ${exception.message}"
                    _verificationSubmitted.value = false
                }
            )
            _isLoading.value = false
        }
    }

    // --- Helper Functions ---

    private fun performUpload(userId: String, fileUri: Uri, isInsurance: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val uploadResult = if (isInsurance) {
                userRepository.uploadInsuranceCard(userId, fileUri)
            } else {
                userRepository.uploadIdentificationCard(userId, fileUri)
            }

            uploadResult.fold(
                onSuccess = { url ->
                    val fieldName = if (isInsurance) "Insurance Card" else "ID Card"
                    Log.d(TAG, "$fieldName upload success. URL: $url")
                    if (isInsurance) _insuranceCardUrl.value = url else _idCardUrl.value = url
                    // Update the profile in Firestore in the background
                    _currentUser.value?.let { user ->
                        val updatedUser = if (isInsurance) user.copy(insuranceCardImageUrl = url)
                        else user.copy(identificationCardImageUrl = url)
                        updateUserProfileInBackground(updatedUser)
                    }
                },
                onFailure = { exception ->
                    val fieldName = if (isInsurance) "Insurance Card" else "ID Card"
                    Log.e(TAG, "$fieldName upload failed: ${exception.message}", exception)
                    _error.value = "$fieldName upload failed: ${exception.message}"
                }
            )
            _isLoading.value = false // Ensure loading is false even after background update starts
        }
    }

    // Updates Firestore profile without blocking the UI thread or showing main loading state
    private fun updateUserProfileInBackground(user: User) {
        viewModelScope.launch {
            Log.d(TAG, "Starting background profile update in Firestore for ${user.id}")
            val result = userRepository.updateUserProfile(user)
            result.fold(
                onSuccess = { updatedUser ->
                    Log.d(TAG, "Background Firestore profile update successful for ${updatedUser.id}")
                    // Optionally update _currentUser if needed, but rely on observer mainly
                    // _currentUser.postValue(updatedUser) // Use postValue if needed from background
                },
                onFailure = { exception ->
                    Log.e(TAG, "Background Firestore profile update failed: ${exception.message}", exception)
                    // Decide if this background failure needs to show an error
                    // _error.postValue("Failed to save image URL: ${exception.message}")
                }
            )
        }
    }
}