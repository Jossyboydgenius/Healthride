package com.example.healthride.network // Or your chosen package

// Data classes to understand the JSON from your function
data class PlacesAutocompleteResponse(
    val predictions: List<AutocompletePrediction>?,
    val status: String?,
    val error_message: String? // Optional error from Google
)

data class AutocompletePrediction(
    val description: String?,
    val place_id: String?
    // You can add 'structured_formatting', 'terms', etc., here later if needed
)