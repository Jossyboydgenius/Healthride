package com.example.healthride.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthride.data.model.User
import com.example.healthride.data.repository.RepositoryProvider
import com.example.healthride.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val userRepository: UserRepository = RepositoryProvider.getUserRepository()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _signOutComplete = MutableLiveData<Boolean>(false)
        val signOutComplete: LiveData<Boolean> = _signOutComplete

    init {
        checkCurrentUser()
    }

    fun checkCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userRepository.getCurrentUser()
                _currentUser.value = user
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // In AuthViewModel.kt, modify the signIn function:

    // In your AuthViewModel, modify the signIn method
    fun signIn(email: String, password: String) {
        _signOutComplete.value = false
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                Log.d("AuthViewModel", "Starting sign in process")
                val result = userRepository.signIn(email, password)

                result.fold(
                    onSuccess = { user ->
                        Log.d("AuthViewModel", "Sign in successful, user ID: ${user.id}")
                        // Even if we just have a basic user with ID and email, that's enough to proceed
                        _currentUser.value = user
                        _loginSuccess.value = true
                    },
                    onFailure = { exception ->
                        Log.e("AuthViewModel", "Sign in failed: ${exception.message}", exception)
                        _error.value = exception.message ?: "Unknown error"
                    }
                )
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Unexpected error during sign in: ${e.message}", e)
                _error.value = e.message ?: "Unexpected error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(email: String, password: String, user: User) {
        _signOutComplete.value = false
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = userRepository.signUp(email, password, user)
                if (result.isSuccess) {
                    _currentUser.value = result.getOrNull()
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Unknown error"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                userRepository.signOut()
                _currentUser.value = null
                _loginSuccess.value = false
                _signOutComplete.postValue(true)
                // Make sure all other state is cleared
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

        fun resetSignOutComplete() {
            _signOutComplete.value = false
        }
    }