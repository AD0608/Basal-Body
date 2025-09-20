package com.basalbody.app.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,
)

data class RegisterRequest(
    @SerializedName("fullname")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("password")
    val password: String,
)
