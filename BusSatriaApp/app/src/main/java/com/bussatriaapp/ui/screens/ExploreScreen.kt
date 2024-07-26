package com.bussatriaapp.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bussatriaapp.R
import com.bussatriaapp.ui.theme.DarkPrimaryPurple
import com.bussatriaapp.ui.theme.LightPrimaryPurple
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.Alignment
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.bussatriaapp.component.calculateArrowPosition
import com.bussatriaapp.component.calculateArrowRotation
import com.bussatriaapp.component.calculateDistance
import com.bussatriaapp.component.vectorToBitmap
import com.bussatriaapp.component.vectorToBitmapDescriptor
import com.bussatriaapp.data.busStops
import com.bussatriaapp.data.markerPositions
import com.bussatriaapp.data.polylinePoints
import com.bussatriaapp.ui.theme.abuMuda
import com.bussatriaapp.ui.theme.abuTua
import com.bussatriaapp.ui.theme.blue
import com.bussatriaapp.ui.theme.brightBlue
import com.bussatriaapp.ui.theme.gray
import com.bussatriaapp.ui.theme.white
import com.bussatriaapp.ui.viewmodel.BusLocationViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import kotlin.math.atan2

@Composable
fun ExploreScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: BusLocationViewModel = hiltViewModel()
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val buttonColor = if (isDarkTheme) DarkPrimaryPurple else LightPrimaryPurple
    val markerColor = if (isDarkTheme) abuMuda else abuTua

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        GoogleMapView(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = backgroundColor,
            textColor = textColor,
            buttonColor = buttonColor,
            viewModel = viewModel
        )
    }
}

@Composable
fun GoogleMapView(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    textColor: Color,
    buttonColor: Color,
    viewModel: BusLocationViewModel
) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val isDarkTheme = isSystemInDarkTheme()
    val initialPosition = LatLng(-7.809858, 112.004694)
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 14f)
    }

    val busLocationIcon =  vectorToBitmap(context, R.drawable.busmerah)
    val markerIcon = vectorToBitmap(context, R.drawable.halte)
    val arrowIcon = vectorToBitmapDescriptor(context, R.drawable.arrow, if (isDarkTheme) white else gray)
    val nightModeStyle = context.resources.openRawResource(R.raw.map_style_night).bufferedReader().use { it.readText() }
    val lightModeStyle = context.resources.openRawResource(R.raw.map_style_light).bufferedReader().use { it.readText() }
    val mapStyleOptions = if (isDarkTheme) MapStyleOptions(nightModeStyle) else MapStyleOptions(lightModeStyle)

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    val polylineColor = if (isDarkTheme) brightBlue else blue

    fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            context as androidx.activity.ComponentActivity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapStyleOptions = mapStyleOptions)
        ) {
            busStops.forEach { busStop ->
                val position = markerPositions.find { it.second == busStop.name }?.first
                position?.let {
                    markerIcon?.let { icon ->
                        Marker(
                            state = MarkerState(position = it),
                            title = busStop.name,
                            snippet = busStop.address,
                            icon = icon,
                            zIndex = 0f
                        )
                    }
                }
            }

            Polyline(
                points = polylinePoints,
                color = polylineColor,
                width = 5f
            )

            if (cameraPositionState.position.zoom >= 14.5f) {
                if (polylinePoints.size >= 2) {
                    var distanceAccumulated = 0.0
                    for (i in 0 until polylinePoints.size - 1) {
                        val start = polylinePoints[i]
                        val end = polylinePoints[i + 1]
                        val segmentDistance = calculateDistance(start, end)
                        distanceAccumulated += segmentDistance

                        if (distanceAccumulated >= 500) { // 500 meter
                            val arrowPosition = calculateArrowPosition(start, end, 0.5)
                            val rotation = calculateArrowRotation(start, end)

                            arrowIcon?.let { icon ->
                                Marker(
                                    state = MarkerState(position = arrowPosition),
                                    icon = icon,
                                    rotation = rotation,
                                    anchor = Offset(0.5f, 0.5f),
                                    zIndex = 0f
                                )
                            }

                            distanceAccumulated = 0.0 // Reset jarak
                        }
                    }
                }
            }

            // Tambahkan marker untuk lokasi bus yang didapatkan dari viewModel
            viewModel.busLocations.collectAsState().value.forEach { busLocation ->
                Marker(
                    state = MarkerState(position = busLocation.position),
                    title = busLocation.id,
                    icon = busLocationIcon,
                    zIndex = 1f // Pastikan zIndex lebih tinggi dari ikon lainnya
                )
            }

            currentLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Lokasi Kamu",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                    zIndex = 0f
                )
            }
        }

        FloatingActionButton(
            onClick = {
                if (!checkLocationPermission()) {
                    requestLocationPermission()
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            val latLng = LatLng(it.latitude, it.longitude)
                            currentLocation = latLng
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 14f)
                        }
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 115.dp, end = 12.dp)
                .size(45.dp),
            backgroundColor = buttonColor
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_my_location),
                contentDescription = "My Location",
                tint = Color.White
            )
        }
    }
}

private const val LOCATION_PERMISSION_REQUEST_CODE = 1
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ExploreScreenDarkPreview() {
    ExploreScreen(navController = rememberNavController())
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
fun ExploreScreenLightPreview() {
    ExploreScreen(navController = rememberNavController())
}