package com.bussatriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussatriaapp.data.repository.BusLocation
import com.bussatriaapp.data.repository.BusLocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BusLocationViewModel @Inject constructor(private val repository: BusLocationRepository) : ViewModel() {
    val busLocations: StateFlow<List<BusLocation>> = repository.getBusLocations()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}