package com.bussatriaapp.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.bussatriaapp.data.BusStop
import com.bussatriaapp.data.busStops
import com.bussatriaapp.data.markerPositions
import com.bussatriaapp.ui.theme.DarkPrimaryPurple
import com.bussatriaapp.ui.theme.LightPrimaryPurple
import com.bussatriaapp.ui.theme.abuMuda
import com.bussatriaapp.ui.theme.gray
import com.bussatriaapp.ui.theme.onSurface
import com.bussatriaapp.ui.theme.surface
import com.google.android.gms.maps.model.LatLng

@Composable
fun InfoScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    context: Context
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val cardColor = if (isDarkTheme) Color(0xFF1E1E1E) else abuMuda
    val accentColor = if (isDarkTheme) Color(0xFF6200EE) else Color(0xFF3700B3)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Halte", color = textColor) },
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
        },
        content = { paddingValues ->
            Column(
                modifier = modifier
                    .background(backgroundColor)
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(busStops) { busStop ->
                        BusStopItem(
                            busStop = busStop,
                            textColor = textColor,
                            backgroundColor = cardColor,
                            context = context
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun BusStopItem(
    busStop: BusStop,
    textColor: Color,
    backgroundColor: Color,
    context: Context // Add context parameter for navigating to Google Maps
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .clickable { /* handle item click */ }
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = busStop.imageUrl,
                contentDescription = "Bus Stop Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = busStop.name,
            color = textColor,
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = busStop.address,
            color = textColor,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val latLng = markerPositions.find { it.second == busStop.name }?.first
                if (latLng != null) {
                    navigateToGoogleMaps(latLng, context)
                } else {
                    Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            shape = RoundedCornerShape(percent = 20),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 4.dp
            ),
            contentPadding = PaddingValues(8.dp)
        ) {
            Text(
                text = "Rute Halte",
                color = backgroundColor,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )
        }
    }
}

fun navigateToGoogleMaps(latLng: LatLng?, context: Context) {
    val uri = latLng?.let {
        val gmmIntentUri = Uri.parse("google.navigation:q=${it.latitude},${it.longitude}")
        Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
    }

    uri?.let {
        try {
            context.startActivity(it)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Google Maps app not found", Toast.LENGTH_SHORT).show()
        }
    }
}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun InfoScreenPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current // Provide a default context for preview

    InfoScreen(navController = navController, context = context)
}


