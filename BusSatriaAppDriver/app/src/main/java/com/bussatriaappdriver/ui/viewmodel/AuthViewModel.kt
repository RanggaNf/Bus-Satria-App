package com.bussatriaappdriver.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussatriaappdriver.data.repository.DriverAuthRepository
import com.bussatriaappdriver.utils.PreferenceUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val driverRepository: DriverAuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<Result<Unit>?>(null)
    val authState: StateFlow<Result<Unit>?> = _authState

    private val _driverAuthState = MutableStateFlow<Result<Unit>?>(null)
    val driverAuthState: StateFlow<Result<Unit>?> = _driverAuthState


    fun loginDriver(passcode: String, context: Context) {
        viewModelScope.launch {
            val result = driverRepository.verifyDriverPasscode(passcode, context)
            _driverAuthState.value = result
            if (result.isSuccess) {
                PreferenceUtil.setDriver(context, true)
                PreferenceUtil.setDriverPasscode(context, passcode)
            }
        }
    }

    fun logoutDriver(context: Context) {
        viewModelScope.launch {
            val result = driverRepository.logoutDriver(context)
            _driverAuthState.value = result
            if (result.isSuccess) {
                PreferenceUtil.setDriver(context, false)
            }
        }
    }
}
