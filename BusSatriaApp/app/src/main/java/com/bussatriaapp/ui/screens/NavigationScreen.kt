//package com.bussatriaapp.ui.screens
//
//
//import android.Manifest
//import android.app.Activity
//import android.content.ActivityNotFoundException
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.content.res.Configuration
//import android.location.Location
//import android.location.LocationManager
//import android.net.Uri
//import android.os.Build
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.camera.core.Camera
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.navigationBarsPadding
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.FloatingActionButton
//import androidx.compose.material.Icon
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.core.app.ActivityCompat
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.google.android.gms.maps.model.LatLng
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.content.ContextCompat
//import androidx.navigation.NavHostController
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.navArgument
//import com.google.android.libraries.navigation.NavigationApi
//import com.google.android.libraries.navigation.Navigator
//import com.google.android.libraries.navigation.RoutingOptions
//import com.google.android.libraries.navigation.SupportNavigationFragment
//import com.google.android.libraries.navigation.Waypoint
//import android.view.View
//import com.google.android.gms.maps.model.CameraPosition
//import com.google.android.gms.maps.CameraUpdateFactory
//import androidx.fragment.app.FragmentActivity
//import kotlinx.coroutines.launch
//
//// Utility functions for location permission
//
//private fun initializeNavigator(
//    activity: FragmentActivity,
//    halteLat: Double,
//    halteLng: Double,
//    onNavigatorReady: (Navigator) -> Unit,
//    onError: (String) -> Unit,
//    onNavigationStarted: () -> Unit
//) {
//    NavigationApi.getNavigator(activity, object : NavigationApi.NavigatorListener {
//        override fun onNavigatorReady(nav: Navigator) {
//            onNavigatorReady(nav)
//            val routingOptions = RoutingOptions().apply {
//                travelMode(RoutingOptions.TravelMode.DRIVING)
//            }
//
//            try {
//                // Create waypoint for destination using the correct builder method
//                val destination = Waypoint.builder()
//                    .setLatLng(halteLat, halteLng)  // Menggunakan setLatLng() yang tersedia di API Waypoint
//                    .build()
//
//                // Set destination and start navigation
//                nav.setDestination(destination, routingOptions)
//                    .setOnResultListener { routeStatus ->
//                        when (routeStatus) {
//                            Navigator.RouteStatus.OK -> {
//                                nav.setAudioGuidance(
//                                    Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE
//                                )
//                                nav.startGuidance()
//                                onNavigationStarted()
//                            }
//                            Navigator.RouteStatus.NO_ROUTE_FOUND ->
//                                onError("Tidak dapat menemukan rute ke halte")
//                            Navigator.RouteStatus.NETWORK_ERROR ->
//                                onError("Error jaringan")
//                            Navigator.RouteStatus.ROUTE_CANCELED ->
//                                onError("Rute dibatalkan")
//                            else ->
//                                onError("Error: $routeStatus")
//                        }
//                    }
//            } catch (e: Exception) {
//                onError("Error saat set destinasi: ${e.message}")
//            }
//        }
//
//        override fun onError(@NavigationApi.ErrorCode errorCode: Int) {
//            val errorMessage = when (errorCode) {
//                NavigationApi.ErrorCode.NOT_AUTHORIZED ->
//                    "API key tidak valid"
//                NavigationApi.ErrorCode.TERMS_NOT_ACCEPTED ->
//                    "Terms of Service belum diterima"
//                NavigationApi.ErrorCode.NETWORK_ERROR ->
//                    "Error jaringan"
//                NavigationApi.ErrorCode.LOCATION_PERMISSION_MISSING ->
//                    "Izin lokasi diperlukan"
//                else -> "Error code: $errorCode"
//            }
//            onError(errorMessage)
//        }
//    })
//}
//
//@Composable
//fun NavigationScreen(
//    navController: NavController,
//    halteLat: Double,
//    halteLng: Double,
//    modifier: Modifier = Modifier
//) {
//    var navigator: Navigator? by remember { mutableStateOf(null) }
//    var isNavigating by remember { mutableStateOf(false) }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//    var navigationFragment: SupportNavigationFragment? by remember { mutableStateOf(null) }
//
//    // State untuk tracking permissions
//    var hasLocationPermission by remember {
//        mutableStateOf(
//            checkLocationPermission(context)
//        )
//    }
//
//    // UI Effects
//    LaunchedEffect(Unit) {
//        if (!hasLocationPermission) {
//            requestLocationPermission(context as ComponentActivity)
//        } else {
//            // Using the refactored initializeNavigator function
//            initializeNavigator(
//                activity = context as FragmentActivity,
//                halteLat = halteLat,
//                halteLng = halteLng,
//                onNavigatorReady = { nav ->
//                    navigator = nav
//                },
//                onError = { error ->
//                    errorMessage = error
//                },
//                onNavigationStarted = {
//                    isNavigating = true
//                }
//            )
//        }
//    }
//
//    Box(modifier = modifier.fillMaxSize()) {
//        // Navigation Fragment Container
//        AndroidView(
//            factory = { context ->
//                SupportNavigationFragment().also { fragment ->
//                    navigationFragment = fragment
//                    // Set default camera position
//                    fragment.getMapAsync { googleMap ->
//                        val cameraPosition = CameraPosition.Builder()
//                            .target(com.google.android.gms.maps.model.LatLng(halteLat, halteLng))
//                            .zoom(15f)
//                            .tilt(45f)
//                            .build()
//                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
//                    }
//                }.requireView()
//            },
//            modifier = Modifier.fillMaxSize()
//        )
//
//        // Error Message
//        errorMessage?.let { message ->
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .align(Alignment.TopCenter)
//            ) {
//                Card(
//                    backgroundColor = MaterialTheme.colors.error,
//                    shape = RoundedCornerShape(8.dp)
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .padding(16.dp)
//                            .fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = message,
//                            color = Color.White
//                        )
//                        IconButton(onClick = { errorMessage = null }) {
//                            Icon(
//                                imageVector = Icons.Default.Close,
//                                contentDescription = "Tutup",
//                                tint = Color.White
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//        // Navigation Controls
//        if (isNavigating) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.BottomCenter)
//                    .padding(16.dp)
//            ) {
//                Card(
//                    backgroundColor = MaterialTheme.colors.surface,
//                    shape = RoundedCornerShape(16.dp),
//                    elevation = 8.dp
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        Text(
//                            text = "Menuju Halte",
//                            style = MaterialTheme.typography.h6
//                        )
//
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        Button(
//                            onClick = {
//                                navigator?.stopGuidance()
//                                navController.popBackStack()
//                            },
//                            colors = ButtonDefaults.buttonColors(
//                                backgroundColor = MaterialTheme.colors.error
//                            ),
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Close,
//                                contentDescription = "Berhenti"
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text("Berhenti Navigasi")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//// 2. Update ActionButton untuk Navigasi
//
//
//fun requestLocationPermission(activity: ComponentActivity) {
//    ActivityCompat.requestPermissions(
//        activity,
//        arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_BACKGROUND_LOCATION
//        ),
//        LOCATION_PERMISSION_REQUEST_CODE
//    )
//}
//
//private const val LOCATION_PERMISSION_REQUEST_CODE = 1001