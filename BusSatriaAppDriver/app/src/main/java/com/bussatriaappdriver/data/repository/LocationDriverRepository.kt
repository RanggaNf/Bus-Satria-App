package com.bussatriaappdriver.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.bussatriaappdriver.data.markerPositions
import com.bussatriaappdriver.utils.PreferenceUtil
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

class LocationDriverRepository @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val firebaseDatabase: FirebaseDatabase
) {

    private var locationCallbackWrapper: CallbackWrapper? = null

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(context: Context, onLocationUpdate: (LatLng) -> Unit) {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // Update every 5 seconds
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 1000 // Maximum wait time 1 second
        }

        locationCallbackWrapper?.let {
            // Remove existing callback to avoid potential issues
            fusedLocationClient.removeLocationUpdates(it.callback)
        }

        locationCallbackWrapper = CallbackWrapper(object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    onLocationUpdate(latLng)
                }
            }
        })

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallbackWrapper!!.callback, // Ensure non-null callback is passed here
            Looper.getMainLooper()
        )
    }
    fun findNearestHalte(currentLocation: LatLng): Pair<String, LatLng> {
        return markerPositions.minByOrNull { (position, _) ->
            calculateDistance(currentLocation, position)
        }?.let { (position, name) ->
            name to position
        } ?: ("Unknown" to LatLng(0.0, 0.0))
    }

    private fun calculateDistance(loc1: LatLng, loc2: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(loc1.latitude, loc1.longitude, loc2.latitude, loc2.longitude, results)
        return results[0]
    }

    fun stopSendingLocationToFirebase() {
        locationCallbackWrapper?.let {
            fusedLocationClient.removeLocationUpdates(it.callback)
            locationCallbackWrapper = null // Clear callback wrapper after stopping updates
        }
    }

    fun sendLocationToFirebase(driverPasscode: String, latLng: LatLng) {
        val locationRef = firebaseDatabase.getReference("locations").child(driverPasscode)

        locationRef.setValue(latLng)
            .addOnSuccessListener {
                Log.d("LocationRepository", "Location sent to Firebase successfully: $latLng")
            }
            .addOnFailureListener { exception ->
                Log.d("LocationRepository", "Failed to send location to Firebase: ${exception.message}")
            }
    }

    private class CallbackWrapper(val callback: LocationCallback)

}
