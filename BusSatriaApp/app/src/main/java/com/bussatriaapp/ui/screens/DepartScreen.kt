package com.bussatriaapp.ui.screens

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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bussatriaapp.R
import com.bussatriaapp.component.vectorToBitmap
import com.bussatriaapp.component.vectorToBitmapDescriptor
import com.bussatriaapp.data.busStops
import com.bussatriaapp.data.markerPositions
import com.bussatriaapp.data.polylinePoints
import com.bussatriaapp.ui.theme.DarkPrimaryPurple
import com.bussatriaapp.ui.theme.LightPrimaryPurple
import com.bussatriaapp.ui.theme.abuMuda
import com.bussatriaapp.ui.theme.abuTua
import com.bussatriaapp.ui.theme.blue
import com.bussatriaapp.ui.theme.brightBlue
import com.bussatriaapp.ui.theme.gray
import com.bussatriaapp.ui.theme.white
import com.bussatriaapp.ui.viewmodel.LocationViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.bussatriaapp.data.HalteStop
import com.bussatriaapp.data.halteStops
import com.bussatriaapp.ui.theme.green
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import java.util.Calendar
import kotlin.random.Random

@Composable
fun DepartScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val systemUiController = rememberSystemUiController()
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF8F8F8)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cardColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val accentColor = if (isDarkTheme) Color(0xFF6200EE) else Color(0xFF3700B3)

    val locationViewModel: LocationViewModel = hiltViewModel()
    val isTracking by locationViewModel.isTracking.collectAsState()

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var nearestStop by remember { mutableStateOf<Pair<LatLng, String>?>(null) }
    var nextStop by remember { mutableStateOf(Triple("Halte 1 Campurejo", 12, 0.5)) }

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val activity = context as? ComponentActivity
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedHalteStop by remember { mutableStateOf<HalteStop?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var isSearchingNearestStop by remember { mutableStateOf(false) }
    val dropdownBackgroundColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val dropdownTextColor = if (isDarkTheme) Color.White else Color.Black
    val icon = if (isDropdownExpanded)
        Icons.Filled.ArrowDropUp
    else
        Icons.Filled.ArrowDropDown


    LaunchedEffect(isTracking) {
        locationViewModel.checkAndUpdateTrackingState()
    }

    LaunchedEffect(key1 = true) {
        systemUiController.setSystemBarsColor(
            color = backgroundColor,
            darkIcons = !isDarkTheme
        )
    }
    LaunchedEffect(Unit) {
        Log.d("DepartScreen", "Halte stops count: ${halteStops.size}")
        halteStops.forEach {
            Log.d("DepartScreen", "Halte: ${it.name}")
        }
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
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Cari Halte Terdekat",
                            style = MaterialTheme.typography.h6.copy(color = textColor),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        ActionButton(
                            text = "Cari halte terdekat",
                            icon = Icons.Default.Search,
                            color = accentColor,
                            enabled = !isSearchingNearestStop,
                            textColor = if (isDarkTheme) Color.White else Color.Black
                        ) {
                            isSearchingNearestStop = true
                            if (!checkLocationPermission(context)) {
                                requestLocationPermission(activity)
                            } else {
                                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                    location?.let {
                                        val latLng = LatLng(it.latitude, it.longitude)
                                        currentLocation = latLng
                                        nearestStop = findNearestStop(latLng)
                                        isSearchingNearestStop = false
                                    }
                                }
                            }
                        }

                        if (isSearchingNearestStop) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }

                        nearestStop?.let { (_, name) ->
                            Text(
                                text = "Halte terdekat: $name",
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            ActionButton(
                                text = "Pergi ke halte terdekat",
                                icon = Icons.Default.Navigation,
                                color = accentColor,
                                textColor = if (isDarkTheme) Color.White else Color.Black
                            ) {
                                currentLocation?.let { location ->
                                    openDirectionsInGoogleMaps(context, location)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 2. Pemilihan halte dan pengiriman notifikasi
                        Text(
                            text = "Pilih Halte dan Kirim Notifikasi",
                            style = MaterialTheme.typography.h6.copy(color = textColor),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isDropdownExpanded = true }
                                .border(
                                    width = 1.dp,
                                    color = textColor.copy(alpha = 0.5f),
                                    shape = MaterialTheme.shapes.small
                                )
                                .background(cardColor)
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedHalteStop?.name?.drop(3) ?: "Pilih Halte Tunggu",
                                    style = MaterialTheme.typography.body1.copy(color = textColor)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown Arrow",
                                    tint = textColor
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(dropdownBackgroundColor)
                                .padding(horizontal = 16.dp)
                        ) {
                            halteStops.forEach { halteStop ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedHalteStop = halteStop
                                        isDropdownExpanded = false
                                    }
                                ) {
                                    Text(
                                        text = halteStop.name.drop(3),
                                        color = dropdownTextColor
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        ActionButton(
                            text = if (isTracking) "Sudah di dalam bus" else "Kirim notifikasi ke driver",
                            icon = Icons.Default.Send,
                            color = if (isTracking) Color.Red else green,
                            enabled = selectedHalteStop != null || isTracking,
                            textColor = if (isDarkTheme) Color.White else Color.Black,
                            onClick = {
                                if (!isTracking) {
                                    val currentSelectedHalteStop = selectedHalteStop
                                    if (currentSelectedHalteStop != null) {
                                        if (!checkLocationPermission(context)) {
                                            requestLocationPermission(activity)
                                        } else {
                                            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                                            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                                locationViewModel.sendLocationToDriver(currentSelectedHalteStop)
                                                Toast.makeText(context, "Notifikasi berhasil dikirim ke driver dan notifikasi menunggu telah dikirim", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Periksa dan pastikan lokasi Anda telah dinyalakan", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "Silakan pilih halte terlebih dahulu", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    showConfirmDialog = true
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
                        )

                        ConfirmationDialog(
                            showDialog = showConfirmDialog,
                            onConfirm = {
                                locationViewModel.passengerOnBus()
                                Toast.makeText(context, "Anda telah ditandai berada di dalam bus, jumlah penumpang ditambahkan, dan lokasi dihapus", Toast.LENGTH_SHORT).show()
                                showConfirmDialog = false
                            },
                            onDismiss = { showConfirmDialog = false }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
@Composable
fun ConfirmationDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Konfirmasi") },
            text = { Text("Apakah Anda yakin sudah berada di dalam bus?") },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Tidak")
                }
            }
        )
    }
}
@Composable
fun TutorialSlider(indicatorColor: Color) {
    val pagerState = rememberPagerState()
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFF0F0F0)
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val accentColor = if (isDarkTheme) Color(0xFF6200EE) else Color(0xFF3700B3)

    val slides = listOf(
        Triple("Cari Halte Terdekat", "Gunakan fitur 'Cari halte terdekat' untuk menemukan perhentian bus terdekat dari lokasi Anda.", Icons.Default.Search),
        Triple("Pilih Halte", "Pilih halte tempat Anda menunggu dari daftar yang tersedia untuk memberi tahu driver.", Icons.Default.Place),
        Triple("Kirim Notifikasi", "Tekan 'Kirim notifikasi ke driver' untuk memberitahu posisi Anda dan memulai perjalanan.", Icons.Default.Send),
        Triple("Konfirmasi Naik Bus", "Setelah naik bus, tekan 'Sudah di dalam bus' untuk mengonfirmasi keberadaan Anda.", Icons.Default.DirectionsBus),
        Triple("Navigasi ke Halte", "Gunakan 'Pergi ke halte terdekat' untuk mendapatkan petunjuk arah ke perhentian terdekat.", Icons.Default.Navigation)
    )

    Column {
        HorizontalPager(
            count = slides.size,
            state = pagerState,
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                elevation = 8.dp,
                backgroundColor = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = slides[page].third,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = slides[page].first,
                        style = MaterialTheme.typography.h6,
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
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
                .padding(16.dp),
            activeColor = accentColor,
            inactiveColor = accentColor.copy(alpha = 0.3f),
            indicatorWidth = 8.dp,
            indicatorHeight = 8.dp,
            spacing = 12.dp
        )
    }
}
@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    textColor: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White)
    }
}

// Helper functions

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

fun calculateDistance(start: LatLng, end: LatLng): Double {
    val results = FloatArray(1)
    Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, results)
    return results[0].toDouble()
}

private const val LOCATION_PERMISSION_REQUEST_CODE = 1
private const val BACKGROUND_LOCATION_PERMISSION_CODE = 2


