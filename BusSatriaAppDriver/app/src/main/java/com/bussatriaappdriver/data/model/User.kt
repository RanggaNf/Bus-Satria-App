package com.bussatriaappdriver.data.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("passwordHash")
    val passwordHash: String? = null,

    @SerializedName("profilePictureUrl")
    val profilePictureUrl: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null
)