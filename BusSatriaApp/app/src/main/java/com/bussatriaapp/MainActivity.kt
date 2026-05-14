package com.bussatriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.bussatriaapp.ui.theme.BusSatriaAppTheme
import com.bussatriaapp.ui.viewmodel.AuthViewModel
import com.bussatriaapp.ui.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint

import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bussatriaapp.ui.viewmodel.ChatViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val locationViewModel: LocationViewModel by viewModels()
    private val viewModel: AuthViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, you can update the ViewModel if needed
            chatViewModel.onNotificationPermissionGranted()
        } else {
            // Permission denied, you might want to show a message to the user
            // explaining why the permission is important
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.checkAuthStatus()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BusSatriaAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val needsNotificationPermission by chatViewModel.needsNotificationPermission.collectAsState()

                    LaunchedEffect(needsNotificationPermission) {
                        if (needsNotificationPermission) {
                            requestNotificationPermission()
                        }
                    }

                    SatriaNavigation()
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationViewModel.resetTrackingState()
    }
}