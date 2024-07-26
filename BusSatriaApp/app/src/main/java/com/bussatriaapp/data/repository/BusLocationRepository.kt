package com.bussatriaapp.data.repository

import com.google.firebase.database.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class BusLocation(val id: String, val position: LatLng)

class BusLocationRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val busLocationsFlow = MutableStateFlow<List<BusLocation>>(emptyList())

    init {
        val buses = listOf("A12345", "B12345", "C12345")
        buses.forEach { busId ->
            database.child("locations").child(busId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(LocationData::class.java)?.let { locationData ->
                        val position = LatLng(locationData.latitude, locationData.longitude)
                        val updatedLocations = busLocationsFlow.value.toMutableList().apply {
                            removeAll { it.id == busId }
                            add(BusLocation(busId, position))
                        }
                        busLocationsFlow.value = updatedLocations
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        }
    }

    fun getBusLocations(): Flow<List<BusLocation>> = busLocationsFlow
}

data class LocationData(val latitude: Double = 0.0, val longitude: Double = 0.0)