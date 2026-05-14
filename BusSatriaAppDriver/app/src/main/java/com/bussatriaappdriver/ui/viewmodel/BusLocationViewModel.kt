package com.bussatriaappdriver.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussatriaappdriver.data.repository.BusLocation
import com.bussatriaappdriver.data.repository.BusLocationRepository
import com.bussatriaappdriver.data.repository.PassengerLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BusLocationViewModel @Inject constructor(private val repository: BusLocationRepository) : ViewModel() {
    val busLocations: StateFlow<List<BusLocation>> = repository.getBusLocations()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val halteInfo: StateFlow<Map<String, Int>> = repository.getHalteInfo()
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())
}