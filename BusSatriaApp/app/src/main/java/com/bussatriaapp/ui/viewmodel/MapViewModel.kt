package com.bussatriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MapViewModel : ViewModel() {
    private val _isMapReady = MutableLiveData<Boolean>(false)
    val isMapReady: LiveData<Boolean> = _isMapReady

    fun setMapReady(ready: Boolean) {
        _isMapReady.value = ready
    }
}