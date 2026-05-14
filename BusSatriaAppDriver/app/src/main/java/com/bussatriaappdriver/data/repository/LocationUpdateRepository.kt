package com.bussatriaappdriver.data.repository

import com.google.type.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationUpdateRepository @Inject constructor() {
    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation

    fun updateLocation(location: LatLng) {
        _currentLocation.value = location
    }
}