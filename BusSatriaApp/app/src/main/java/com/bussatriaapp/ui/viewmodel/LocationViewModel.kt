package com.bussatriaapp.ui.viewmodel

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussatriaapp.LocationService
import com.bussatriaapp.data.HalteStop
import com.bussatriaapp.data.repository.AuthRepository
import com.bussatriaapp.data.repository.LocationRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class LocationViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore,
) : ViewModel() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentLocationDocumentId: String? = null
    private var locationSent = false
    private val _isTracking = MutableStateFlow(getTrackingState())
    private val sentLocationIds = mutableListOf<String>()
    val isTracking: StateFlow<Boolean> = _isTracking
    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats
    private val _userActivities = MutableStateFlow<List<UserActivity>>(emptyList())
    val userActivities: StateFlow<List<UserActivity>> = _userActivities
    private var lastSelectedHalte: String? = null

    init {
        fetchUserStats()
        fetchUserActivities()
    }

    fun sendLocationToDriver(selectedHalteStop: HalteStop) {
        if (!locationSent) {
            viewModelScope.launch {
                sendLocationToFirestore(selectedHalteStop.latLng, selectedHalteStop.name)
                sendWaitingNotification(selectedHalteStop.name)
                updateStatsHalte(selectedHalteStop.name, true)
                setTrackingState(true)
                locationSent = true
                lastSelectedHalte = selectedHalteStop.name
                Log.d("LocationViewModel", "Lokasi dikirim ke Firestore, notifikasi dikirim, dan stats_halte diperbarui: ${selectedHalteStop.name}")
            }
        } else {
            Log.d("LocationViewModel", "Lokasi sudah dikirim sebelumnya, melewati")
        }
    }
    private fun updateStatsHalte(halteName: String, isWaiting: Boolean) {
        val statsHalteRef = firestore.collection("stats_halte").document(halteName)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(statsHalteRef)
            val currentWaitingCount = snapshot.getLong("waiting_count") ?: 0
            val newWaitingCount = if (isWaiting) currentWaitingCount + 1 else maxOf(currentWaitingCount - 1, 0)

            transaction.set(
                statsHalteRef,
                mapOf("waiting_count" to newWaitingCount),
                SetOptions.merge()
            )
        }.addOnSuccessListener {
            Log.d("LocationViewModel", "Stats halte berhasil diperbarui untuk $halteName: waiting_count = ${if (isWaiting) "ditambah 1" else "dikurangi 1"}")
        }.addOnFailureListener { e ->
            Log.e("LocationViewModel", "Gagal memperbarui stats halte", e)
        }
    }

    private fun sendLocationToFirestore(latLng: LatLng, halteName: String) {
        val userId = auth.currentUser?.uid ?: return

        val locationData = hashMapOf(
            "latitude" to latLng.latitude,
            "longitude" to latLng.longitude,
            "halteName" to halteName,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("passenger_locations").document(userId)
            .set(locationData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("LocationViewModel", "Location sent to Firestore successfully for user: $userId")
            }
            .addOnFailureListener { e ->
                Log.e("LocationViewModel", "Error sending location to Firestore", e)
            }
    }

    private fun sendWaitingNotification(halteName: String) {
        viewModelScope.launch {
            val userDataResult = authRepository.getUserData()
            userDataResult.onSuccess { userData ->
                val userName = userData["name"] as? String ?: "Seseorang"
                val userId = auth.currentUser?.uid ?: return@onSuccess
                val userRole = userData["role"] as? String ?: "Unknown Role"

                val notificationData = hashMapOf(
                    "message" to "$userName ($userRole) sedang menunggu di $halteName",
                    "timestamp" to ServerValue.TIMESTAMP
                )

                val notificationRef = database.child("notifications").child(userId)
                notificationRef.setValue(notificationData)
                    .addOnSuccessListener {
                        Log.d("LocationViewModel", "Notification sent successfully for user: $userId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("LocationViewModel", "Error sending notification", e)
                    }
            }.onFailure { error ->
                Log.e("LocationViewModel", "Error getting user data", error)
            }
        }
    }

    fun passengerOnBus() {
        viewModelScope.launch {
            incrementPassengerCount()
            updateLocationToZero()
            resetTrackingState()
            updateUserStats()
            addUserActivity()
            deleteNotification()
            updateStatsHalteOnBoarding()
            updateUserStats()
        }
    }

    private fun updateStatsHalteOnBoarding() {
        lastSelectedHalte?.let { halteName ->
            updateStatsHalte(halteName, false)
            lastSelectedHalte = null
        } ?: run {
            Log.e("LocationViewModel", "Tidak ada halte yang tercatat saat penumpang naik bus")
        }
    }

    private fun updateLocationToZero() {
        val userId = auth.currentUser?.uid ?: return

        val zeroLocationData = hashMapOf(
            "latitude" to 0.0,
            "longitude" to 0.0,
            "halteName" to "",
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("passenger_locations").document(userId)
            .set(zeroLocationData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("LocationViewModel", "Location updated to zero for user: $userId")
            }
            .addOnFailureListener { e ->
                Log.e("LocationViewModel", "Error updating location to zero", e)
            }
    }

    private fun deleteNotification() {
        val userId = auth.currentUser?.uid ?: return

        val notificationRef = database.child("notifications").child(userId)
        notificationRef.removeValue()
            .addOnSuccessListener {
                Log.d("LocationViewModel", "Notification deleted successfully for user: $userId")
            }
            .addOnFailureListener { e ->
                Log.e("LocationViewModel", "Error deleting notification for user: $userId", e)
            }
    }

    private fun incrementPassengerCount() {
        viewModelScope.launch {
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val dailyStatsRef = firestore.collection("daily_stats").document(currentDate)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(dailyStatsRef)
                val currentPassengers = snapshot.getLong("passengers") ?: 0
                transaction.set(dailyStatsRef, mapOf("passengers" to currentPassengers + 1), SetOptions.merge())
            }.addOnSuccessListener {
                Log.d("LocationViewModel", "Passenger count incremented successfully")
            }.addOnFailureListener { e ->
                Log.e("LocationViewModel", "Error incrementing passenger count", e)
            }
        }
    }

    private fun updateUserStats() {
        val userId = auth.currentUser?.uid ?: return
        val userStatsRef = firestore.collection("user_stats").document(userId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userStatsRef)
            val currentTrips = snapshot.getLong("total_trips") ?: 0
            val currentDistance = snapshot.getDouble("total_distance") ?: 0.0
            val currentPoints = snapshot.getLong("total_points") ?: 0

            val newTrips = currentTrips + 1
            val newDistance = currentDistance + 3.32 // Adding 3.32 km for each trip
            val newPoints = currentPoints + 3 // Adding 3 points for each trip

            val updatedStats = hashMapOf(
                "total_trips" to newTrips,
                "total_distance" to newDistance,
                "total_points" to newPoints
            )

            transaction.set(userStatsRef, updatedStats, SetOptions.merge())

            _userStats.value = UserStats(
                trips = newTrips.toInt(),
                distance = newDistance,
                point = newPoints.toInt()
            )
        }.addOnSuccessListener {
            Log.d("LocationViewModel", "User stats updated successfully")
        }.addOnFailureListener { e ->
            Log.e("LocationViewModel", "Error updating user stats", e)
        }
    }

    private fun addUserActivity() {
        val userId = auth.currentUser?.uid ?: return
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val newActivity = UserActivity(
            title = "Perjalanan ${_userStats.value.trips + 1} kali",
            description = "Completed successfully",
            time = currentTime
        )

        firestore.collection("user_activities").document(userId)
            .collection("activities")
            .add(newActivity)
            .addOnSuccessListener {
                Log.d("LocationViewModel", "User activity added successfully")
                fetchUserActivities()
            }
            .addOnFailureListener { e ->
                Log.e("LocationViewModel", "Error adding user activity", e)
            }
    }

    private fun fetchUserActivities() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("user_activities").document(userId)
            .collection("activities")
            .orderBy("time", Query.Direction.DESCENDING)
            .limit(3)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("LocationViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val activities = snapshot.toObjects(UserActivity::class.java)
                    _userActivities.value = activities
                }
            }
    }

    private fun fetchUserStats() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("user_stats").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("LocationViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val totalTrips = snapshot.getLong("total_trips") ?: 0
                    val totalDistance = snapshot.getDouble("total_distance") ?: 0.0
                    val totalPoints = snapshot.getLong("total_points") ?: 0

                    _userStats.value = UserStats(
                        trips = totalTrips.toInt(),
                        distance = totalDistance,
                        point = totalPoints.toInt()
                    )
                }
            }
    }

    private fun getTrackingState(): Boolean {
        return sharedPreferences.getBoolean("is_tracking", false)
    }

    private fun setTrackingState(isTracking: Boolean) {
        sharedPreferences.edit().putBoolean("is_tracking", isTracking).apply()
        _isTracking.value = isTracking
    }

    fun resetTrackingState() {
        setTrackingState(false)
        locationSent = false
    }

    fun checkAndUpdateTrackingState() {
        _isTracking.value = getTrackingState()
    }
}

data class UserStats(
    val trips: Int = 0,
    val distance: Double = 0.0,
    val point: Int = 0
) {
    val formattedDistance: String
        get() = String.format("%.1f", distance)
}

data class UserActivity(
    val title: String = "",
    val description: String = "",
    val time: String = ""
)