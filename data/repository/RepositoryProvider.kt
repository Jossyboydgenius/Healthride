package com.example.healthride.data.repository

import com.example.healthride.data.AppConfig

object RepositoryProvider {
    private val _userRepository: UserRepository by lazy {
        if (AppConfig.USE_MOCK_DATA) { // Checks the flag
            MockUserRepository()
        } else {
            UserRepositoryImpl() // Should use this when flag is false
        }
    }

    private val _rideRepository: RideRepository by lazy {
        if (AppConfig.USE_MOCK_DATA) { // Checks the flag
            MockRideRepository()
        } else {
            RideRepositoryImpl() // Should use this when flag is false
        }
    }

    fun getUserRepository(): UserRepository = _userRepository
    fun getRideRepository(): RideRepository = _rideRepository
}