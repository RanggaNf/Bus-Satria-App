package com.bussatriaapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _authState = MutableStateFlow<Result<Unit>?>(null)
    val authState: StateFlow<Result<Unit>?> = _authState

    private val _driverAuthState = MutableStateFlow<Result<Unit>?>(null)
    val driverAuthState: StateFlow<Result<Unit>?> = _driverAuthState

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = repository.register(email, password)
        }
    }

    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _authState.value = repository.login(email, password, context)
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            repository.logout(context)
            // Set authState to null after logout
            _authState.value = null
        }
    }
}