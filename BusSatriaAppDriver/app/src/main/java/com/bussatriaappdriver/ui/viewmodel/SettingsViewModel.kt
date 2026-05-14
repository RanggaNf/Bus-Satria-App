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
import com.bussatriaappdriver.data.repository.SettingsRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val isDarkTheme = settingsRepository.isDarkThemeFlow
    val isLocationPermissionGranted = settingsRepository.isLocationPermissionGrantedFlow
    val isNotificationEnabled = settingsRepository.isNotificationEnabledFlow

    fun toggleDarkTheme() {
        viewModelScope.launch {
            settingsRepository.toggleDarkTheme()
        }
    }

    fun toggleLocationPermission() {
        viewModelScope.launch {
            settingsRepository.toggleLocationPermission()
        }
    }

    fun toggleNotificationPermission() {
        viewModelScope.launch {
            settingsRepository.toggleNotificationPermission()
        }
    }
}
