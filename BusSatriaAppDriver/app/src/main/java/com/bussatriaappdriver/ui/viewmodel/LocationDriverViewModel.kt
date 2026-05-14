package com.bussatriaappdriver.ui.viewmodel


import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussatriaappdriver.LocationService
import com.bussatriaappdriver.data.markerPositions
import com.bussatriaappdriver.data.repository.LocationDriverRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject


@HiltViewModel
class LocationDriverViewModel @Inject constructor(
    private val application: Application,
    private val sharedPreferences: SharedPreferences,
    private val firestore: FirebaseFirestore,
    private val locationRepository: LocationDriverRepository
) : ViewModel() {

    private val _isTracking = MutableStateFlow(getTrackingState())
    val isTracking: StateFlow<Boolean> = _isTracking

    private val _todayStats = MutableStateFlow(TodayStats(0, 0))
    val todayStats: StateFlow<TodayStats> = _todayStats

    private val _currentTime = MutableStateFlow("")
    val currentTime: StateFlow<String> = _currentTime

    private val _currentHalte = MutableStateFlow<HalteInfo?>(null)
    val currentHalte: StateFlow<HalteInfo?> = _currentHalte

    private val _halteList = MutableStateFlow<List<String>>(emptyList())
    val halteList: StateFlow<List<String>> = _halteList

    private val _nearestHalte = MutableStateFlow<HalteInfo?>(null)
    val nearestHalte: StateFlow<HalteInfo?> = _nearestHalte

    private var locationUpdateJob: Job? = null


    init {
        loadTodayStats()
        startTimeUpdates()
        listenToTodayStats()
    }
    private fun startTimeUpdates() {
        viewModelScope.launch {
            while (true) {
                updateCurrentTime()
                delay(60000) // Update setiap detik
            }
        }
    }
    private fun updateCurrentTime() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        _currentTime.value = timeFormat.format(Date())
    }
    private fun getTrackingState(): Boolean {
        return sharedPreferences.getBoolean("is_tracking", false)
    }

    private fun setTrackingState(isTracking: Boolean) {
        sharedPreferences.edit().putBoolean("is_tracking", isTracking).apply()
        _isTracking.value = isTracking
    }

    fun startLocationUpdates() {
        if (_isTracking.value) return

        setTrackingState(true)
        val serviceIntent = Intent(application, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            application.startForegroundService(serviceIntent)
        } else {
            application.startService(serviceIntent)
        }
        incrementTrips()

        // Mulai pembaruan lokasi
        locationUpdateJob = viewModelScope.launch {
            locationRepository.startLocationUpdates(application) { latLng ->
                updateNearestHalte(latLng)
            }
        }
    }
    private fun incrementTrips() {
        viewModelScope.launch {
            val currentStats = _todayStats.value
            val newStats = currentStats.copy(trips = currentStats.trips + 1)
            updateFirestoreStats(newStats)

            // Tambahkan aktivitas baru
            addActivity(Activity(
                title = "Perjalanan Dimulai",
                description = "Jumlah perjalanan meningkat menjadi ${newStats.trips}",
                time = _currentTime.value
            ))
        }
    }


    fun incrementPassengers(count: Int = 1) {
        viewModelScope.launch {
            val currentStats = _todayStats.value
            val newStats = currentStats.copy(passengers = currentStats.passengers + count)
            updateFirestoreStats(newStats)

            // Tambahkan aktivitas baru
            addActivity(Activity(
                title = "Penumpang Ditambahkan",
                description = "$count penumpang ditambahkan, total sekarang ${newStats.passengers}",
                time = _currentTime.value
            ))
        }
    }


    private suspend fun updateFirestoreStats(newStats: TodayStats) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        firestore.collection("daily_stats")
            .document(currentDate)
            .set(newStats)
            .await()
        _todayStats.value = newStats

        // Tambahkan aktivitas terbaru
        addActivity(Activity(
            title = "Update Status",
            description = "${newStats.trips} Keberangkatan dengan ${newStats.passengers} Penumpang",
            time = _currentTime.value
        ))
    }
    fun stopLocationUpdates() {
        if (!_isTracking.value) return

        setTrackingState(false)
        val serviceIntent = Intent(application, LocationService::class.java)
        application.stopService(serviceIntent)

        // Hentikan pembaruan lokasi
        locationUpdateJob?.cancel()
        locationUpdateJob = null
    }

    private fun updateNearestHalte(currentLocation: LatLng) {
        val (halteName, haltePosition) = locationRepository.findNearestHalte(currentLocation)
        viewModelScope.launch {
            firestore.collection("stats_halte").document(halteName)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val waitingCount = document.getLong("waiting_count")?.toInt() ?: 0
                        _nearestHalte.value = HalteInfo(halteName, waitingCount, haltePosition)
                    }
                }
        }
    }
    private fun loadTodayStats() {
        viewModelScope.launch {
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val statsDoc = firestore.collection("daily_stats")
                .document(currentDate)
                .get()
                .await()

            val stats = statsDoc.toObject(TodayStats::class.java) ?: TodayStats()

            // Optionally, you could load activities here
            val activitiesCollection = firestore.collection("daily_stats")
                .document(currentDate)
                .collection("activities")
                .get()
                .await()
                .toObjects(Activity::class.java)

            _todayStats.value = stats.copy(activities = activitiesCollection)
        }
    }


    fun checkServiceRunning() {
        val serviceRunning = isServiceRunning(LocationService::class.java)
        if (serviceRunning != _isTracking.value) {
            setTrackingState(serviceRunning)
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
    private fun listenToTodayStats() {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        firestore.collection("daily_stats")
            .document(currentDate)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("LocationDriverViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val stats = snapshot.toObject(TodayStats::class.java)
                    stats?.let {
                        _todayStats.value = it
                    }
                }
            }
    }
    private fun addActivity(activity: Activity) {
        viewModelScope.launch {
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val activityRef = firestore.collection("daily_stats")
                .document(currentDate)
                .collection("activities")
                .document()

            activityRef.set(activity).await()
        }
    }
    fun loadHalteList() {
        viewModelScope.launch {
            val halteSnapshot = firestore.collection("stats_halte").get().await()
            _halteList.value = halteSnapshot.documents.map { it.id }
            if (_halteList.value.isNotEmpty()) {
                loadCurrentHalteInfo(_halteList.value.first())
            }
        }
    }

    fun loadCurrentHalteInfo(halteName: String) {
        viewModelScope.launch {
            firestore.collection("stats_halte").document(halteName)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("LocationDriverViewModel", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val waitingCount = snapshot.getLong("waiting_count")?.toInt() ?: 0
                        _currentHalte.value = HalteInfo(halteName, waitingCount, LatLng(0.00,0.00))
                    }
                }
        }
    }

    fun loadNextHalte() {
        val currentIndex = _halteList.value.indexOf(_currentHalte.value?.name)
        if (currentIndex != -1 && currentIndex < _halteList.value.size - 1) {
            loadCurrentHalteInfo(_halteList.value[currentIndex + 1])
        } else if (_halteList.value.isNotEmpty()) {
            loadCurrentHalteInfo(_halteList.value.first())
        }
    }
    fun loadPreviousHalte() {
        val currentIndex = _halteList.value.indexOf(_currentHalte.value?.name)
        if (currentIndex > 0) {
            loadCurrentHalteInfo(_halteList.value[currentIndex - 1])
        } else if (_halteList.value.isNotEmpty()) {
            loadCurrentHalteInfo(_halteList.value.last())
        }
    }
    private var currentHalteListener: ListenerRegistration? = null

    override fun onCleared() {
        super.onCleared()
        currentHalteListener?.remove()
    }
}


data class TodayStats(
    val trips: Int = 0,
    val passengers: Int = 0,
    val activities: List<Activity> = emptyList()
)

data class Activity(
    val title: String = "",
    val description: String = "",
    val time: String = ""
)
data class HalteInfo(
    val name: String,
    val waitingCount: Int,
    val position: LatLng
)



