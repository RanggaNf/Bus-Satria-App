package com.bussatriaapp.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussatriaapp.data.AuthState
import com.bussatriaapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _userData = MutableStateFlow<Map<String, Any>?>(null)
    val userData: StateFlow<Map<String, Any>?> = _userData


    fun register(email: String, password: String, name: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.register(email, password, name, role)
            _authState.value = if (result.isSuccess) {
                AuthState.Authenticated
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, password, context)
            _authState.value = if (result.isSuccess) {
                fetchUserData()
                AuthState.Authenticated
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            repository.logout(context)
            _authState.value = AuthState.Unauthenticated
            _userData.value = null
        }
    }

    fun fetchUserData() {
        viewModelScope.launch {
            _userData.value = repository.getUserData().getOrNull()
        }
    }

    fun updateProfile(name: String, email: String, role: String, newPassword: String) {
        viewModelScope.launch {
            val updatedData = mapOf(
                "name" to name,
                "email" to email,
                "role" to role
            )
            val result = repository.updateUserData(updatedData)
            if (result.isSuccess) {
                if (newPassword.isNotEmpty()) {
                    val passwordResult = repository.updatePassword(newPassword)
                    if (passwordResult.isFailure) {
                        // Handle password update error if needed
                    }
                }
                _userData.value = updatedData
            } else {
                // Handle update error if needed
            }
        }
    }
    fun checkAuthStatus() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val isLoggedIn = repository.isUserLoggedIn()
            Log.d("AuthViewModel", "isUserLoggedIn: $isLoggedIn")
            _authState.value = if (isLoggedIn) {
                AuthState.Authenticated
            } else {
                AuthState.Unauthenticated
            }
            Log.d("AuthViewModel", "AuthState after check: ${_authState.value}")
        }
    }
}
