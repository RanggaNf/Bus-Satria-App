package com.bussatriaapp.ui.screens

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector
import com.bussatriaapp.ui.theme.abuTua
import com.bussatriaapp.ui.viewmodel.SettingsViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()
    val isLocationPermissionGranted by viewModel.isLocationPermissionGranted.collectAsState(initial = false)
    val isNotificationEnabled by viewModel.isNotificationEnabled.collectAsState(initial = false)

    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF8F8F8)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cardColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White

    LaunchedEffect(isDarkTheme) {
        systemUiController.setSystemBarsColor(
            color = backgroundColor,
            darkIcons = !isDarkTheme
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Pengaturan", color = textColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                backgroundColor = backgroundColor,
                elevation = 8.dp,
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SettingOptionItem(
                        title = "Izin Lokasi",
                        description = if (isLocationPermissionGranted) "Diizinkan" else "Tidak Diizinkan",
                        color = cardColor,
                        textColor = textColor,
                        icon = Icons.Default.LocationOn,
                        isChecked = isLocationPermissionGranted,
                        onClick = {
                            viewModel.toggleLocationPermission()
                        }
                    )
                }

                item {
                    SettingOptionItem(
                        title = "Izin Notifikasi",
                        description = if (isNotificationEnabled) "Aktif" else "Nonaktif",
                        color = cardColor,
                        textColor = textColor,
                        icon = Icons.Default.Notifications,
                        isChecked = isNotificationEnabled,
                        onClick = {
                            viewModel.toggleNotificationPermission()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingOptionItem(
    title: String,
    description: String,
    color: Color,
    textColor: Color,
    icon: ImageVector,
    isChecked: Boolean,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    Card(
        backgroundColor = color,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.body1,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.body2,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }
            Switch(
                checked = isChecked,
                onCheckedChange = { newValue ->
                    // Toggle the setting when the switch is changed
                    onClick()
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = if (isDarkTheme) Color.White else Color.Black,
                    uncheckedThumbColor = if (isDarkTheme) abuTua else Color.LightGray,
                    checkedTrackColor = if (isDarkTheme) Color(0xFF03DAC5) else Color(0xFF6200EE),
                    uncheckedTrackColor = if (isDarkTheme) Color(0xFFBDBDBD) else Color(0xFFCFCFCF)
                )
            )
        }
    }
}
