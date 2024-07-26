package com.bussatriaappdriver.ui.viewmodel


import android.app.Application
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussatriaappdriver.LocationService
import com.bussatriaappdriver.data.repository.LocationDriverRepository
import com.bussatriaappdriver.data.repository.LocationRepository
import com.bussatriaappdriver.utils.PreferenceUtil
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LocationDriverViewModel @Inject constructor(
    private val application: Application,
    private val locationRepository: LocationDriverRepository
) : ViewModel() {

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking

    fun startLocationUpdates() {
        if (_isTracking.value) return

        _isTracking.value = true
        val serviceIntent = Intent(application, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            application.startForegroundService(serviceIntent)
        } else {
            application.startService(serviceIntent)
        }
        Log.d("LocationViewModel", "Location updates started")
    }

    fun stopLocationUpdates() {
        _isTracking.value = false
        val serviceIntent = Intent(application, LocationService::class.java)
        application.stopService(serviceIntent)
    }
}
