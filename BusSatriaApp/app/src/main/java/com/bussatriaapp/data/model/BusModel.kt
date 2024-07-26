package com.bussatriaapp.data.model

import com.google.gson.annotations.SerializedName


data class Bus(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("route")
    val route: String,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)