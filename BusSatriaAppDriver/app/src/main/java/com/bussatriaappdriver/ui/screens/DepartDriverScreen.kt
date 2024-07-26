package com.bussatriaappdriver.ui.screens


import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var nearestStop by remember { mutableStateOf<Pair<LatLng, String>?>(null) }
    var nextStop by remember { mutableStateOf(Triple("Halte 1 Campurejo", 12, 0.5)) }

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val activity = context as? ComponentActivity

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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                TutorialSlider(indicatorColor = textColor)
            }

            item {
                Card(
                    backgroundColor = cardColor,
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionButton(
                            text = if (isTracking) "Stop Sending Location" else "Start Sending Location",
                            icon = if (isTracking) Icons.Default.Stop else Icons.Default.PlayArrow,
                            color = if (isTracking) Color.Red else green
                        ) {
                            if (!checkLocationPermission(context)) {
                                requestLocationPermission(activity)
                            } else if (!isTracking) {
                                locationDriverViewModel.startLocationUpdates()
                            } else {
                                locationDriverViewModel.stopLocationUpdates()
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                                ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                requestBackgroundLocationPermission(context)
                            }
                        }

                        NextStopCard(
                            stopName = nextStop.first,
                            peopleWaiting = nextStop.second,
                            distance = nextStop.third,
                            backgroundColor = cardColor,
                            textColor = textColor
                        )

                        ActionButton(
                            text = "Update Next Stop",
                            icon = Icons.Default.Refresh,
                            color = accentColor
                        ) {
                            nextStop = getNextStopDummy()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun NextStopCard(
    stopName: String,
    peopleWaiting: Int,
    distance: Double,
    backgroundColor: Color,
    textColor: Color
) {
    Card(
        backgroundColor = backgroundColor,
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Next Stop",
                style = MaterialTheme.typography.h6,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stopName,
                style = MaterialTheme.typography.body1,
                color = textColor
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "People waiting",
                        tint = textColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$peopleWaiting waiting",
                        style = MaterialTheme.typography.body2,
                        color = textColor
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Distance",
                        tint = textColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.2f km", distance),
                        style = MaterialTheme.typography.body2,
                        color = textColor
                    )
                }
            }
        }
    }
}

// Fungsi dummy untuk mendapatkan halte selanjutnya
fun getNextStopDummy(): Triple<String, Int, Double> {
    val stops = listOf(
        "Halte 1 Campurejo",
        "Halte 2 RSU Lirboyo",
        "Halte 3 Kelurahan Sukorame",
        "Halte 4 RSU Daha Husada",
        "Halte 5 Mojoroto Indah"
    )
    val randomStop = stops[Random.nextInt(stops.size)]
    val randomPeopleWaiting = Random.nextInt(1, 21) // 1 to 20
    val randomDistance = Random.nextDouble(0.1, 5.1) // 0.1 to 5.0
    return Triple(randomStop, randomPeopleWaiting, randomDistance)
}

@Composable
fun TutorialSlider(indicatorColor: Color) {
    val pagerState = rememberPagerState()
    val slides = listOf(
        "Slide 1: Tekan 'Start Sending Location' untuk mulai mengirim lokasi Anda.",
        "Slide 2: Tekan 'Navigate to Nearest Stop' untuk navigasi ke perhentian terdekat.",
        "Slide 3: Pastikan GPS dan izin lokasi diaktifkan untuk pengalaman terbaik."
    )

    Column {
        HorizontalPager(
            count = slides.size,
            state = pagerState,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                elevation = 4.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = slides[page],
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
            activeColor = indicatorColor,
            inactiveColor = indicatorColor.copy(alpha = 0.3f)
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

// Helper functions

// Helper function to get the next stop
fun getNextStop(currentLocation: LatLng): Triple<String, Int, Double>? {
    val sortedStops = markerPositions.sortedBy { (position, _) ->
        calculateDistance(currentLocation, position)
    }

    // Find the first stop that hasn't been passed yet
    return sortedStops.firstOrNull { (position, _) ->
        calculateDistance(currentLocation, position) > 0.1 // 100 meters threshold
    }?.let { (position, title) ->
        val distance = calculateDistance(currentLocation, position)
        // Simulate a random number of people waiting (1-20)
        Triple(title, (1..20).random(), distance)
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

fun openDirectionsInGoogleMaps(context: Context, currentLocation: LatLng) {
    val nearest = findNearestStop(currentLocation)

    nearest?.let { (stopLocation, title) ->
        val gmmIntentUri = Uri.parse("google.navigation:q=${stopLocation.latitude},${stopLocation.longitude}&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            context.startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Google Maps app not installed", Toast.LENGTH_SHORT).show()
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

fun calculateDistance(latLng1: LatLng, latLng2: LatLng): Double {
    val earthRadius = 6371.0

    val dLat = Math.toRadians(latLng2.latitude - latLng1.latitude)
    val dLng = Math.toRadians(latLng2.longitude - latLng1.longitude)

    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(latLng1.latitude)) * Math.cos(Math.toRadians(latLng2.latitude)) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2)

    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return earthRadius * c
}

private const val LOCATION_PERMISSION_REQUEST_CODE = 1
private const val BACKGROUND_LOCATION_PERMISSION_CODE = 2