package com.example.healthride

import UserSessionManager
import android.app.Application
import com.example.healthride.data.AppConfig
import com.google.firebase.FirebaseApp



// In HealthRideApplication.kt
class HealthRideApplication : Application() {
    lateinit var userSessionManager: UserSessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        userSessionManager = UserSessionManager(this)
    }
}