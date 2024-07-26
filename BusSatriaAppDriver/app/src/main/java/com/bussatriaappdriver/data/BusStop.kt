package com.bussatriaappdriver.data

data class BusStop(
    val name: String,
    val address: String,
    val bus1Morning: String,
    val bus1Afternoon: String,
    val bus1Evening: String,
    val bus1Night: String,
    val bus2Morning: String,
    val bus2Afternoon: String,
    val bus2Evening: String,
    val bus2Night: String,
    val bus3Morning: String,
    val bus3Afternoon: String,
    val bus3Evening: String,
    val bus3Night: String,
    val imageUrl: String
)