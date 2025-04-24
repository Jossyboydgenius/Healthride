package com.example.healthride.network // Or your chosen package

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header // <-- !!! IMPORT THIS !!!
import retrofit2.http.Query

interface FirebaseFunctionsApiService {
    @GET(".") // Or your function path
    suspend fun getPlacePredictions(
        @Header("Authorization") authHeader: String, // <-- !!! MAKE SURE THIS LINE IS HERE !!!
        @Query("query") query: String
    ): Response<PlacesAutocompleteResponse>
}