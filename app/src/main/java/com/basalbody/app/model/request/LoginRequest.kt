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
    val fullName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("gender")
    val gender: String? = null,

    @SerializedName("phone_number")
    val phoneNumber: String? = null,

    @SerializedName("password")
    val password: String? = null,
)
