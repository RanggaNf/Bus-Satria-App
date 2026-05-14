package com.bussatriaappdriver.ui.screens


import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bussatriaappdriver.data.markerPositions
import com.bussatriaappdriver.ui.viewmodel.LocationDriverViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.bussatriaappdriver.ui.theme.gray
import com.bussatriaappdriver.ui.theme.green
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlin.random.Random

@Composable
fun DepartScreenDriver(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF8F8F8)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cardColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val accentColor = if (isDarkTheme) Color(0xFF6200EE) else Color(0xFF3700B3)

    val locationDriverViewModel: LocationDriverViewModel = hiltViewModel()
    val isTracking by locationDriverViewModel.isTracking.collectAsState()
    val currentHalte by locationDriverViewModel.currentHalte.collectAsState()
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val nearestHalte by locationDriverViewModel.nearestHalte.collectAsState()


    LaunchedEffect(Unit) {
        locationDriverViewModel.checkServiceRunning()
        locationDriverViewModel.loadHalteList()
    }

    LaunchedEffect(key1 = true) {
        systemUiController.setSystemBarsColor(
            color = backgroundColor,
            darkIcons = !isDarkTheme
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TutorialSlider(indicatorColor = textColor)
            }

            item {
                Card(
                    backgroundColor = cardColor,
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Informasi Halte",
                            style = MaterialTheme.typography.h5,
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )

                        currentHalte?.let { halte ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = halte.name.drop(3), // Menghilangkan 4 karakter pertama
                                        style = MaterialTheme.typography.h6,
                                        color = textColor,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PeopleAlt,
                                            contentDescription = "Waiting passengers",
                                            tint = accentColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${halte.waitingCount} penumpang menunggu",
                                            style = MaterialTheme.typography.body1,
                                            color = textColor.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { locationDriverViewModel.loadPreviousHalte() },
                                colors = ButtonDefaults.buttonColors(backgroundColor = accentColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Previous stop",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Previous", color = Color.White)
                            }

                            Button(
                                onClick = { locationDriverViewModel.loadNextHalte() },
                                colors = ButtonDefaults.buttonColors(backgroundColor = accentColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Next", color = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Next stop",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
            item {
                Card(
                    backgroundColor = if (isTracking) green else Color.Red,
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isTracking) "Sedang Beroperasi" else "Tidak Beroperasi",
                                style = MaterialTheme.typography.h6,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTracking) "Lokasi sedang dibagikan" else "Lokasi tidak dibagikan",
                                style = MaterialTheme.typography.body1,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Switch(
                            checked = isTracking,
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    when {
                                        !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                                            Toast.makeText(context, "Mohon nyalakan lokasi terlebih dahulu", Toast.LENGTH_LONG).show()
                                        }
                                        !checkLocationPermission(context) -> {
                                            requestLocationPermission(activity)
                                        }
                                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                                                ActivityCompat.checkSelfPermission(
                                                    context,
                                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                                ) != PackageManager.PERMISSION_GRANTED -> {
                                            requestBackgroundLocationPermission(context)
                                        }
                                        else -> {
                                            locationDriverViewModel.startLocationUpdates()
                                        }
                                    }
                                } else {
                                    locationDriverViewModel.stopLocationUpdates()
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color.White.copy(alpha = 0.5f),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

fun findNearestStop(currentLocation: LatLng): Pair<LatLng, String>? {
    var nearest: Pair<LatLng, String>? = null
    var nearestDistance = Double.MAX_VALUE

    for ((position, title) in markerPositions) {
        val distance = calculateDistance(currentLocation, position)
        if (distance < nearestDistance) {
            nearestDistance = distance
            nearest = Pair(position, title)
        }
    }

    return nearest
}

fun calculateDistance(start: LatLng, end: LatLng): Double {
    val results = FloatArray(1)
    Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, results)
    return results[0].toDouble()
}


@Composable
fun TutorialSlider(indicatorColor: Color) {
    val pagerState = rememberPagerState()
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFF0F0F0)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val accentColor = if (isDarkTheme) Color(0xFF6200EE) else Color(0xFF3700B3)

    val slides = listOf(
        Triple("Mulai Perjalanan", "Aktifkan switch untuk mulai membagikan lokasi Anda.", Icons.Default.PlayArrow),
        Triple("Informasi Halte", "Lihat informasi halte saat ini, termasuk jumlah penumpang yang menunggu.", Icons.Default.Info),
        Triple("Navigasi Halte", "Gunakan tombol 'Sebelumnya' dan 'Selanjutnya' untuk melihat informasi halte lainnya.", Icons.Default.Navigation),
        Triple("Pantau Status", "Perhatikan status 'Sedang Beroperasi' atau 'Tidak Beroperasi' di bagian bawah layar.", Icons.Default.Visibility),
        Triple("Akhiri Perjalanan", "Non-aktifkan switch untuk berhenti membagikan lokasi dan mengakhiri perjalanan.", Icons.Default.Stop)
    )

    Column {
        HorizontalPager(
            count = slides.size,
            state = pagerState,
            modifier = Modifier
                .height(220.dp)
                .fillMaxWidth()
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                elevation = 4.dp,
                backgroundColor = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = slides[page].third,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(bottom = 8.dp)
                    )
                    Text(
                        text = slides[page].first,
                        style = MaterialTheme.typography.h6,
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = slides[page].second,
                        style = MaterialTheme.typography.body2,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(12.dp),
            activeColor = accentColor,
            inactiveColor = accentColor.copy(alpha = 0.3f),
            indicatorWidth = 6.dp,
            indicatorHeight = 6.dp,
            spacing = 8.dp
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White)
            Text(text = text, color = Color.White)
        }
    }
}



fun checkLocationPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun requestLocationPermission(activity: ComponentActivity?) {
    activity?.let {
        ActivityCompat.requestPermissions(
            it,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
}

fun requestBackgroundLocationPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            BACKGROUND_LOCATION_PERMISSION_CODE
        )
    }
}



private const val LOCATION_PERMISSION_REQUEST_CODE = 1
private const val BACKGROUND_LOCATION_PERMISSION_CODE = 2