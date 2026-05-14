package com.bussatriaappdriver.data.repository

import com.google.firebase.database.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class BusLocation(val id: String, val position: LatLng)
data class PassengerLocation(val id: String, val position: LatLng, val timestamp: Long)

class BusLocationRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val busLocationsFlow = MutableStateFlow<List<BusLocation>>(emptyList())
    private val passengerLocationsFlow = MutableStateFlow<List<PassengerLocation>>(emptyList())
    private val halteInfoFlow = MutableStateFlow<Map<String, Int>>(emptyMap())


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
        firestore.collection("passenger_locations")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val passengerLocations = snapshot.documents.mapNotNull { document ->
                        val id = document.id
                        val latitude = document.getDouble("latitude")
                        val longitude = document.getDouble("longitude")
                        val timestamp = document.getTimestamp("timestamp")?.seconds ?: 0

                        if (latitude != null && longitude != null) {
                            PassengerLocation(id, LatLng(latitude, longitude), timestamp)
                        } else {
                            null
                        }
                    }
                    passengerLocationsFlow.value = passengerLocations
                }
            }
        firestore.collection("stats_halte")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val halteInfo = snapshot.documents.associate { document ->
                        val halteName = document.id
                        val waitingCount = document.getLong("waiting_count")?.toInt() ?: 0
                        halteName to waitingCount
                    }
                    halteInfoFlow.value = halteInfo
                }
            }
    }

    fun getBusLocations(): Flow<List<BusLocation>> = busLocationsFlow
    fun getPassengerLocations(): Flow<List<PassengerLocation>> = passengerLocationsFlow
    fun getHalteInfo(): Flow<Map<String, Int>> = halteInfoFlow
}

data class LocationData(val latitude: Double = 0.0, val longitude: Double = 0.0)