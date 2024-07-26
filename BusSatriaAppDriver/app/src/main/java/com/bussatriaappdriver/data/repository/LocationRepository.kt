package com.bussatriaappdriver.data.repository

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

class LocationRepository @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val firebaseDatabase: FirebaseDatabase
) {
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(context: Context, onLocationUpdate: (LatLng) -> Unit) {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // Update setiap 5 detik
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 1000 // Maksimal menunggu 1 detik
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    onLocationUpdate(latLng)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun sendLocationToFirebase(latLng: LatLng) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
        val locationRef = firebaseDatabase.getReference("locations").child(userId)

        locationRef.setValue(latLng)
            .addOnSuccessListener {
                Log.d("LocationRepository", "Location sent to Firebase successfully: $latLng")
            }
            .addOnFailureListener { exception ->
                Log.d("LocationRepository", "Failed to send location to Firebase: ${exception.message}")
            }
    }
}