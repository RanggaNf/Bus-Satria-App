package com.bussatriaapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
class LocationRepository @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val firestore: FirebaseFirestore
) {
    @SuppressLint("MissingPermission")
    suspend fun getAndSendCurrentLocation(): Result<LatLng> {
        return try {
            val location = fusedLocationClient.lastLocation.await()
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                sendLocationToFirestore(latLng)
                Result.success(latLng)
            } ?: Result.failure(Exception("Location not available"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun sendLocationToFirestore(latLng: LatLng) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
        val locationData = hashMapOf(
            "latitude" to latLng.latitude,
            "longitude" to latLng.longitude,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        try {
            firestore.collection("passenger_locations").document(userId)
                .set(locationData)
                .await()
            Log.d("LocationRepository", "Location sent to Firestore successfully: $latLng")
        } catch (e: Exception) {
            Log.e("LocationRepository", "Failed to send location to Firestore", e)
        }
    }
}